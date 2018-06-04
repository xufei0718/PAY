package com.mybank.pc.collection.trade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.CardBin;
import com.mybank.pc.admin.model.User;
import com.mybank.pc.collection.model.CollectionClear;
import com.mybank.pc.collection.model.CollectionEntrust;
import com.mybank.pc.collection.model.CollectionTrade;
import com.mybank.pc.collection.model.UnionpayCollection;
import com.mybank.pc.collection.model.sender.SendProxy;
import com.mybank.pc.exception.TradeRuntimeException;
import com.mybank.pc.exception.TxnKey;
import com.mybank.pc.exception.ValidateCTRException;
import com.mybank.pc.exception.ValidateUnionpayRespException;
import com.mybank.pc.interceptors.TradeExceptionInterceptor;
import com.mybank.pc.kits.AppKit;
import com.mybank.pc.kits.DateKit;
import com.mybank.pc.kits.FeeKit;
import com.mybank.pc.kits.unionpay.acp.AcpResponse;
import com.mybank.pc.kits.unionpay.acp.SDK;
import com.mybank.pc.merchant.model.MerchantCust;
import com.mybank.pc.merchant.model.MerchantInfo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class CTradeSrv {

	public Kv initiate(Kv kv) {
		Kv result = Kv.create();
		boolean isSuccess = false;

		Kv initiateRequest = validateAndBuildInitiateRequest(kv);
		if (isSuccess = initiateRequest.getBoolean("isValidate")) {
			UnionpayCollection unionpayCollection = (UnionpayCollection) initiateRequest.get("unionpayCollection");
			CollectionTrade collectionTrade = (CollectionTrade) initiateRequest.get("collectionTrade");
			result.set("unionpayCollection", unionpayCollection).set("collectionTrade", collectionTrade);

			// 需先保存订单信息后，再在需要时发起请求
			try {
				boolean isRealtime = "1".equals(kv.getStr("bussType"));
				// 加急需发起实时交易请求
				if (isRealtime) {
					unionpayCollection.assemblyRealtimeRequest();
				}
				if (saveOrder(unionpayCollection, collectionTrade)) {
					isSuccess = isRealtime ? sendRealtimeOrder(unionpayCollection, collectionTrade) : true;
				}
			} catch (TradeRuntimeException e) {
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				TradeRuntimeException xe = new TradeRuntimeException(e);
				xe.setCollectionTrade(collectionTrade);
				xe.setUnionpayCollection(unionpayCollection);
				throw xe;
			}
		} else {
			if (initiateRequest.containsKey("exception")) {
				Exception e = (Exception) initiateRequest.get("exception");
				if (e instanceof ValidateCTRException) {
					throw (ValidateCTRException) e;
				} else {
					throw new TradeRuntimeException(e);
				}
			}
		}
		return result.set("isSuccess", isSuccess);
	}

	public Kv validateAndBuildInitiateRequest(Kv kv) {
		// 银联调用相关参数
		String txnType = null; // 交易类型

		// 平台调用相关参数
		SDK sdk = null;
		String tradeType = "1";// 1代收 2代付
		String finalCode = "1";// 最终处理结果：0成功 1处理中 2失败
		String clearStatus = "1";// 清分标识：0已清分 1未清分
		String merchantFeeTradeType = null;// 商户手续费交易类型 1加急 2标准

		Kv result = Kv.create();
		boolean isValidate = false;

		UnionpayCollection unionpayCollection = new UnionpayCollection();
		CollectionTrade collectionTrade = new CollectionTrade();

		try {
			String bussType = kv.getStr("bussType");
			String accNo = kv.getStr("accNo");
			String txnAmt = kv.getStr("txnAmt");
			String custID = kv.getStr("custID");
			String merchantID = kv.getStr("merchantID");
			String operID = kv.getStr("operID");

			if (StringUtils.isBlank(bussType) || (!(bussType = bussType.trim()).equals("1") && !bussType.equals("2"))) {
				throw new ValidateCTRException("非法的业务类型[" + bussType + "]");
			}
			BigDecimal originaltxnAmt = null;
			long numTxnAmt = -1;
			try {
				originaltxnAmt = new BigDecimal(txnAmt = txnAmt.trim());
				numTxnAmt = originaltxnAmt.multiply(new BigDecimal(100)).longValue();
				if (numTxnAmt < 1) {
					throw new RuntimeException();
				}
				txnAmt = String.valueOf(numTxnAmt);
			} catch (Exception e) {
				throw new ValidateCTRException("非法的交易金额[" + txnAmt + "]");
			}
			Object minAmt = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "minAmt");
			if (minAmt != null) {
				try {
					long numMiniAmt = new BigDecimal(String.valueOf(minAmt).trim()).multiply(new BigDecimal(100))
							.longValue();
					if (numTxnAmt < numMiniAmt) {
						throw new RuntimeException();
					}
				} catch (Exception e) {
					throw new ValidateCTRException("交易金额不得小于[" + minAmt + "]");
				}
			}
			if (StringUtils.isBlank(merchantID)) {
				throw new ValidateCTRException("商户ID不能为空");
			}
			MerchantInfo merchantInfo = MerchantInfo.dao.findById(merchantID = merchantID.trim());
			if (merchantInfo == null || "1".equals(merchantInfo.getStatus()) || merchantInfo.getDat() != null) {
				throw new ValidateCTRException("商户[" + merchantID + "]" + "不存在或已停用/已删除");
			}
			BigDecimal maxTradeAmount = merchantInfo.getMaxTradeAmount();
			if (maxTradeAmount != null) {
				long numMaxAmt = -1;
				try {
					numMaxAmt = maxTradeAmount.multiply(new BigDecimal(100)).longValue();
					if (numTxnAmt > numMaxAmt) {
						throw new RuntimeException();
					}
				} catch (Exception e) {
					throw new ValidateCTRException("交易金额不得大于于[" + numMaxAmt + "]");
				}
			}
			if (StringUtils.isBlank(custID)) {
				throw new ValidateCTRException("客户ID不能为空");
			}
			MerchantCust merchantCust = MerchantCust.dao.findById(custID = custID.trim());
			if (merchantCust == null || merchantCust.getDat() != null) {
				throw new ValidateCTRException("客户[" + custID + "]" + "不存在或已删除");
			}
			if (StringUtils.isBlank(accNo) && StringUtils.isBlank(accNo = merchantCust.getBankcardNo())) {
				throw new ValidateCTRException("账号不能为空");
			}
			CardBin cardBin = FeeKit.getCardBin(accNo = accNo.trim());
			if (cardBin == null || !"0".equals(cardBin.getCardType())) {
				throw new ValidateCTRException("不支持的卡类型!!");
			}
			boolean isRealtimeBuss = "1".equals(bussType) ? true : false;
			String merId = null;
			if (isRealtimeBuss) {// 加急
				if (Long.valueOf(txnAmt) > 500000) {
					sdk = SDK.getSDK(SDK.MER_CODE_REALTIME_YS_4);
				} else {
					sdk = SDK.getSDK(SDK.MER_CODE_REALTIME_YS_2);
				}
				merchantFeeTradeType = "1";
				txnType = "11";

				unionpayCollection.setMerId(merId = sdk.getMerId());
				unionpayCollection.toCollection();
			} else if ("2".equals(bussType)) {// 批量
				sdk = SDK.getSDK(SDK.MER_CODE_BATCH_CH);
				merchantFeeTradeType = "2";
				txnType = "21"; // 取值：21 批量交易

				unionpayCollection.setMerId(merId = sdk.getMerId());
				unionpayCollection.toEntrustCollection();
			}

			CollectionEntrust query = new CollectionEntrust();
			query.setAccNo(accNo);
			query.setMerId(merId);
			CollectionEntrust collectionEntrust = query.findOne();
			if (collectionEntrust == null || !"0".equals(collectionEntrust.getStatus())) {
				throw new ValidateCTRException("客户委托信息不存在或未处于已委托状态");
			}

			String formattedTradeType = StringUtils.leftPad(tradeType, 2, '0');
			String formattedBussType = StringUtils.leftPad(bussType, 2, '0');
			String formattedCustID = StringUtils.leftPad(custID, isRealtimeBuss ? 10 : 8, '0');

			Date now = new Date();
			String txnTime = new SimpleDateFormat("yyyyMMddHHmmss").format(now);

			// 订单号 实时最大长度40，批量最大长度32
			String orderId = generateOrderId(now, txnType, unionpayCollection.getTxnSubType(), bussType,
					formattedBussType, formattedCustID);
			// 流水号
			String tradeNo = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(now) + formattedTradeType
					+ formattedBussType + formattedCustID;

			// 银行手续费
			collectionTrade.setBankFee(FeeKit.getBankFeeByYuan(originaltxnAmt, cardBin, merId));
			// 商户手续费
			if ("2".equals(merchantInfo.getMerchantType())) {// 外部商户
				collectionTrade.setMerFee(
						FeeKit.getMerchantFee(originaltxnAmt, Integer.valueOf(merchantID), merchantFeeTradeType));
			} else if ("1".equals(merchantInfo.getMerchantType())) {// 内部商户
				collectionTrade.setMerFee(new BigDecimal(0));
			}

			String reqReserved = "from=pac";

			unionpayCollection.setCustomerNm(collectionEntrust.getCustomerNm());
			unionpayCollection.setCertifTp(collectionEntrust.getCertifTp());
			unionpayCollection.setCertifId(collectionEntrust.getCertifId());
			unionpayCollection.setAccNo(accNo);
			unionpayCollection.setPhoneNo(collectionEntrust.getPhoneNo());
			unionpayCollection.setCvn2(collectionEntrust.getCvn2());
			unionpayCollection.setExpired(collectionEntrust.getExpired());
			unionpayCollection.setTradeNo(tradeNo);
			unionpayCollection.setOrderId(orderId);
			unionpayCollection.setTxnType(txnType);
			unionpayCollection.setTxnTime(txnTime);
			unionpayCollection.setTxnAmt(txnAmt);
			unionpayCollection.setMerchantID(merchantID);
			unionpayCollection.setReqReserved(reqReserved);
			unionpayCollection.setReqReserved1(reqReserved);
			unionpayCollection.setFinalCode(finalCode);
			unionpayCollection.setStatus("2".equals(bussType) ? "0" : null);
			unionpayCollection.setCat(now);
			unionpayCollection.setMat(now);
			unionpayCollection.setOperID(operID);
			unionpayCollection.setQueryResultCount(0);

			collectionTrade.setTradeNo(tradeNo);
			collectionTrade.setTradeTime(now);
			collectionTrade.setTradeType(tradeType);
			collectionTrade.setBussType(bussType);
			collectionTrade.setAmount(originaltxnAmt);
			collectionTrade.setCustID(Integer.valueOf(custID));
			collectionTrade.setCustName(collectionEntrust.getCustomerNm());
			collectionTrade.setCardID(collectionEntrust.getCertifId());
			collectionTrade.setMobileBank(collectionEntrust.getPhoneNo());
			collectionTrade.setBankcardNo(accNo);
			collectionTrade.setFinalCode(finalCode);
			collectionTrade.setClearStatus(clearStatus);
			collectionTrade.setCat(now);
			collectionTrade.setMat(now);
			collectionTrade.setOperID(operID);
			collectionTrade.setMerchantID(Integer.valueOf(merchantID));

			isValidate = true;
		} catch (Exception e) {
			e.printStackTrace();
			isValidate = false;
			result.set("errorMessage", e.getMessage());
			result.set("exception", e);
		} finally {
			result.set("isValidate", isValidate);
			result.set("unionpayCollection", unionpayCollection).set("collectionTrade", collectionTrade);
		}
		return result;
	}

	/**
	 * 订单号 实时最大长度40，批量最大长度32
	 * 
	 * @param now
	 * @param txnType
	 * @param txnSubType
	 * @param bussType
	 * @param formattedBussType
	 * @param formattedCustID
	 * @return
	 */
	public static String generateOrderId(Date now, String txnType, String txnSubType, String bussType,
			String formattedBussType, String formattedCustID) {
		int maxLength = "1".equals(bussType) ? 40 : 32;
		String orderId = null;
		try {
			orderId = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(now) + txnType + txnSubType + formattedBussType
					+ formattedCustID;
			if (orderId.length() > maxLength) {
				orderId = orderId.substring(0, maxLength);
			}
			LogKit.info("生成的orderId[" + orderId + "]");
			if (UnionpayCollection.findByOrderId(orderId) != null) {
				int tryCount = 10;
				while (--tryCount > 0) {
					orderId = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + txnType + txnSubType
							+ formattedBussType + formattedCustID;
					if (orderId.length() > maxLength) {
						orderId = orderId.substring(0, maxLength);
					}
					LogKit.info("生成的orderId[" + orderId + "]");
					if (UnionpayCollection.findByOrderId(orderId) == null) {
						return orderId;
					}
				}
			} else {
				return orderId;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("系统内部错误，生成订单号失败[" + e.getMessage() + "]");
		}
		throw new RuntimeException("系统内部错误，生成订单号失败[" + orderId + "]");
	}

	/**
	 * @param unionpayCollection
	 * @param collectionTrade
	 */
	@Before({ TradeExceptionInterceptor.class, Tx.class })
	@TxnKey("saveOrder")
	public boolean saveOrder(UnionpayCollection unionpayCollection, CollectionTrade collectionTrade) {
		try {
			return unionpayCollection.save() && collectionTrade.save();
		} catch (Exception e) {
			e.printStackTrace();
			collectionTrade.setFinalCode("2");
			unionpayCollection.setFinalCode("2");

			TradeRuntimeException xe = new TradeRuntimeException(e);
			xe.setCollectionTrade(collectionTrade);
			xe.setUnionpayCollection(unionpayCollection);
			throw xe;
		}
	}

	@Before({ TradeExceptionInterceptor.class, Tx.class })
	@TxnKey("realtime-sendOrder")
	public boolean sendRealtimeOrder(UnionpayCollection unionpayCollection, CollectionTrade collectionTrade) {
		try {
			unionpayCollection.sendRealtimeOrder();
			return handlingTradeResult(unionpayCollection, collectionTrade);
		} catch (TradeRuntimeException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			TradeRuntimeException xe = new TradeRuntimeException(e);
			xe.setCollectionTrade(collectionTrade);
			xe.setUnionpayCollection(unionpayCollection);
			throw xe;
		}
	}

	/**
	 * 
	 * 应答码规范参考open.unionpay.com帮助中心 下载 产品接口规范 《平台接入接口规范-第5部分-附录》
	 * 
	 * @param rspData
	 * @param acpService
	 * @param encoding
	 * @param unionpayCollection
	 * @param collectionTrade
	 * @return
	 */
	private boolean handlingTradeResult(UnionpayCollection unionpayCollection, CollectionTrade collectionTrade) {
		boolean isSuccess = false;
		try {
			Date now = new Date();
			try {
				unionpayCollection.validateRealtimeResp();
			} catch (ValidateUnionpayRespException vure) {
				vure.printStackTrace();
				SendProxy sendProxy = vure.getSendProxy();
				unionpayCollection.setResp(JsonKit.toJson(sendProxy.getAcpResponse()));
				unionpayCollection.setExceInfo(JsonKit.toJson(vure.getExceptionInfo()));
				if (sendProxy.isInvalidRequestOrURI()) {
					isSuccess = false;
					collectionTrade.setMat(now);
					collectionTrade.setFinalCode("2");// 失败
					unionpayCollection.setMat(now);
					unionpayCollection.setFinalCode("2");// 失败
					unionpayCollection.update();
					collectionTrade.update();
				} else {
					unionpayCollection.setMat(now);
					unionpayCollection.setFinalCode("3");// 状态未明 需后续处理
					unionpayCollection.update();
					SyncStatusExecutor.scheduleNotClearSingleQuery(unionpayCollection.getOrderId(), 10,
							TimeUnit.SECONDS);
				}
				throw vure;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

			Map<String, String> rspData = unionpayCollection.getRealtimeRspData();
			String respCode = rspData.get("respCode");
			String respMsg = rspData.get("respMsg");
			String queryId = rspData.get("queryId");

			unionpayCollection.setRespCode(respCode);
			unionpayCollection.setRespMsg(respMsg);
			unionpayCollection.setQueryId(queryId);
			unionpayCollection.setMat(now);

			collectionTrade.setResCode(respCode);
			collectionTrade.setResMsg(respMsg);
			collectionTrade.setMat(now);

			// 00 交易已受理(不代表交易已成功），等待接收后台通知更新订单状态,也可以主动发起 查询交易确定交易状态。
			//// 后续需发起交易状态查询交易确定交易状态
			if ("00".equals(respCode) || "03".equals(respCode) || "04".equals(respCode) || "05".equals(respCode)) {
				// 如果是配置了敏感信息加密，如果需要获取卡号的铭文，可以按以下方法解密卡号
				// String accNo1 = resmap.get("accNo");
				// String accNo2 = AcpService.decryptData(accNo1, "UTF-8");
				// //解密卡号使用的证书是商户签名私钥证书acpsdk.signCert.path
				// LogUtil.writeLog("解密后的卡号："+accNo2);
				isSuccess = true;
				SyncStatusExecutor.scheduleInProcessSingleQuery(unionpayCollection.getOrderId(), 5, TimeUnit.SECONDS);
			} else {
				isSuccess = false;
				collectionTrade.setFinalCode("2");
				unionpayCollection.setFinalCode("2");
			}

			unionpayCollection.update();
			collectionTrade.update();
			return isSuccess;
		} catch (Exception e) {
			e.printStackTrace();
			TradeRuntimeException xe = new TradeRuntimeException(e);
			xe.setCollectionTrade(collectionTrade);
			xe.setUnionpayCollection(unionpayCollection);
			throw xe;
		}
	}

	public void updateOrderStatus(Map<String, String> reqParam) {
		String respCode = reqParam.get("respCode");
		String orderId = reqParam.get("orderId");
		try {
			UnionpayCollection unionpayCollection = UnionpayCollection.findByOrderId(orderId);
			if ("00".equals(respCode) || "A6".equals(respCode)) {// 交易成功
				syncOrderStatus(unionpayCollection);
			} else if ("03".equals(respCode) || "04".equals(respCode) || "05".equals(respCode)) {// 订单处理中或交易状态未明
				setResultByCallback(unionpayCollection, reqParam, "1");// 处理中
				SyncStatusExecutor.scheduleInProcessSingleQuery(unionpayCollection.getOrderId(), 5, 5,
						TimeUnit.SECONDS);
			} else {// 交易失败
				setResultByCallback(unionpayCollection, reqParam, "2");// 失败
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setResultByCallback(UnionpayCollection unionpayCollection, Map<String, String> reqParam,
			String finalCode) {
		Date now = new Date();
		String respCode = reqParam.get("respCode");
		String respMsg = reqParam.get("respMsg");

		String queryId = reqParam.get("queryId");
		String settleAmt = reqParam.get("settleAmt");
		String settleCurrencyCode = reqParam.get("settleCurrencyCode");
		String settleDate = reqParam.get("settleDate");
		String traceNo = reqParam.get("traceNo");
		String traceTime = reqParam.get("traceTime");
		if (unionpayCollection != null) {
			unionpayCollection.setFinalCode(finalCode);
			unionpayCollection.setResultCode(respCode);
			unionpayCollection.setResultMsg(respMsg);
			unionpayCollection.setResult(JsonKit.toJson(reqParam));
			unionpayCollection.setQueryId(queryId);
			unionpayCollection.setSettleAmt(settleAmt);
			unionpayCollection.setSettleCurrencyCode(settleCurrencyCode);
			unionpayCollection.setSettleDate(settleDate);
			unionpayCollection.setTraceNo(traceNo);
			unionpayCollection.setTraceTime(traceTime);
			unionpayCollection.setMat(now);
			unionpayCollection.update();
		}
		CollectionTrade collectionTrade = CollectionTrade.findByTradeNo(unionpayCollection);
		if (collectionTrade != null) {
			collectionTrade.setFinalCode(finalCode);
			collectionTrade.setResultCode(respCode);
			collectionTrade.setResultMsg(respMsg);
			collectionTrade.setMat(now);
			collectionTrade.update();
		}
	}

	public void syncOrderStatus(UnionpayCollection unionpayCollection) throws Exception {
		if (unionpayCollection != null) {
			new StatusSynchronizer(unionpayCollection).sync();
		}
	}

	public void handlingException(Invocation invocation, TradeRuntimeException e) {
		Method method = invocation.getMethod();
		if (!method.isAnnotationPresent(TxnKey.class)) {
			return;
		}

		UnionpayCollection unionpayCollection = e.getUnionpayCollection();
		CollectionTrade collectionTrade = e.getCollectionTrade();

		TxnKey txnKey = method.getAnnotation(TxnKey.class);
		String txnKeyValue = txnKey.value();

		if (txnKeyValue.equals("saveOrder")) {
			try {
				collectionTrade.setFinalCode("2");// 失败
				collectionTrade.save();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				unionpayCollection.setExceInfo(JsonKit.toJson(e.getExceptionInfo()));
				unionpayCollection.setFinalCode("2");// 失败
				unionpayCollection.save();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (txnKeyValue.equals("realtime-sendOrder")) {
			try {
				collectionTrade.update();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				if (StringUtils.isBlank(unionpayCollection.getResp())) {
					SendProxy sendProxy = unionpayCollection.getSendProxy();
					AcpResponse acpResponse = sendProxy == null ? null : sendProxy.getAcpResponse();
					if (acpResponse != null) {
						unionpayCollection.setResp(JsonKit.toJson(acpResponse));
					}
				}
				unionpayCollection.setExceInfo(JsonKit.toJson(e.getExceptionInfo()));
				unionpayCollection.update();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	public Kv exportTradeDetailExcel(Kv kv, boolean isAdmin, User currUser) {
		List<CollectionTrade> tradeList = CollectionTrade.find(kv);
		if (CollectionUtils.isEmpty(tradeList)) {
			return Kv.by("errorMsg", "没有查询到要导出的数据");
		}
		String fileName = null;
		try {
			ExcelWriter writer = ExcelUtil.getWriter();
			List<Map<String, String>> excelList = collectionTradeToExcelData(tradeList, isAdmin);
			Map<String, String> alias = new HashMap<>();
			alias.put("tradeNo", "交易流水号");
			alias.put("merchantNo", "商户编号");
			alias.put("merchantName", "商户名称");
			alias.put("tradeTime", "交易时间");
			alias.put("tradeType", "交易类型");
			alias.put("bussType", "业务类型");
			alias.put("custName", "客户姓名");
			alias.put("cardID", "身份证号");
			alias.put("mobileBank", "银行预留手机号");
			alias.put("bankcardNo", "银行卡号");
			alias.put("amount", "交易金额");
			if (isAdmin) {
				alias.put("bankFee", "银行代收手续费");
			}
			alias.put("merFee", "商户代收手续费");
			alias.put("resCode", "响应码");
			alias.put("resMsg", "响应信息");
			alias.put("resultCode", "交易结果返回码");
			alias.put("resultMsg", "交易结果信息");
			alias.put("finalCode", "最终处理结果");
			alias.put("clearStatus", "清分状态");
			alias.put("clearDate", "清分时间");
			alias.put("clearNo", "清分流水号");
			alias.put("mat", "最后更新时间");
			alias.put("operLoginname", "操作员");

			String bTime = kv.getStr("bTime");
			String eTime = kv.getStr("eTime");
			String excelTitle = bTime + "-" + eTime + "代收交易详情";
			writer.merge(alias.size() - 1, excelTitle);

			writer.setHeaderAlias(alias);
			writer.write(excelList);

			List<String> totalAlias = new ArrayList<String>();
			totalAlias.add("总笔数");
			totalAlias.add("总金额");
			totalAlias.add("成功笔数");
			totalAlias.add("成功金额");
			totalAlias.add("清分笔数");
			if (isAdmin) {
				totalAlias.add("银行代收手续费总额");
			}
			totalAlias.add("商户代收手续费总额");
			writer.writeRow(totalAlias);
			List<String> total = collectionTradeToExcelTotal(tradeList, isAdmin);
			writer.writeRow(total);

			String fileFolder = "dsjyxq/" + DateKit.dateToStr(new Date(), DateKit.yyyy_MM_dd) + "/";
			File file = FileUtil.file(PathKit.getWebRootPath() + AppKit.getExcelPath() + fileFolder);
			if (!file.exists())
				file.mkdirs();
			fileName = fileFolder + (currUser != null ? currUser.getLoginname() : "") + DateUtil.current(true) + ".xls";
			file = FileUtil.file(PathKit.getWebRootPath() + AppKit.getExcelPath() + fileName);

			OutputStream out = new FileOutputStream(file);
			writer.flush(out);
			writer.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			LogKit.error("文件导出失败=>" + e.getMessage());
			return Kv.by("errorMsg", "文件导出失败");
		}
		return Kv.by("fileName", fileName);
	}

	private List<String> collectionTradeToExcelTotal(List<CollectionTrade> list, boolean isAdmin) {
		List<String> accum = new ArrayList<String>();
		int totalNum = 0;// 总笔数
		BigDecimal totalAmount = new BigDecimal(0);// 总金额
		int successNum = 0;// 成功笔数
		BigDecimal successAmount = new BigDecimal(0);// 成功金额
		int totalClearNum = 0;// 清分笔数
		BigDecimal totalBankFee = new BigDecimal(0);// 银行代收手续费总额
		BigDecimal totalMerFee = new BigDecimal(0);// 商户代收手续费总额
		try {
			if (CollectionUtils.isNotEmpty(list)) {
				BigDecimal amount = null;
				BigDecimal bankFee = null;
				BigDecimal merFee = null;
				for (CollectionTrade collectionTrade : list) {
					amount = collectionTrade.getAmount();
					bankFee = collectionTrade.getBankFee();
					merFee = collectionTrade.getMerFee();

					++totalNum;
					if (amount != null) {
						totalAmount = totalAmount.add(amount);
					}
					if ("0".equals(collectionTrade.getFinalCode())) {
						++successNum;
						if (amount != null) {
							successAmount = successAmount.add(amount);
						}
					}
					if ("0".equals(collectionTrade.getClearStatus())) {
						++totalClearNum;
					}
					if (bankFee != null) {
						totalBankFee = totalBankFee.add(bankFee);
					}
					if (merFee != null) {
						totalMerFee = totalMerFee.add(merFee);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		accum.add(String.valueOf(totalNum));
		accum.add(totalAmount.toPlainString());
		accum.add(String.valueOf(successNum));
		accum.add(successAmount.toPlainString());
		accum.add(String.valueOf(totalClearNum));
		if (isAdmin) {
			accum.add(totalBankFee.toPlainString());
		}
		accum.add(totalMerFee.toPlainString());

		return accum;
	}

	private List<Map<String, String>> collectionTradeToExcelData(List<CollectionTrade> list, boolean isAdmin) {
		List<Map<String, String>> accum = new ArrayList<Map<String, String>>();
		if (CollectionUtils.isEmpty(list)) {
			return accum;
		}
		CollectionTrade.getOperInfo(list);
		CollectionTrade.getCollectionClear(list);

		Map<String, String> map = null;
		MerchantInfo merchantInfo = null;
		User oper = null;
		CollectionClear collectionClear = null;
		for (CollectionTrade collectionTrade : list) {
			map = new LinkedHashMap<String, String>();
			merchantInfo = collectionTrade.getMerchantInfo();
			oper = collectionTrade.getOper();
			collectionClear = collectionTrade.getCollectionClear();

			map.put("tradeNo", collectionTrade.getTradeNo());
			map.put("merchantNo", merchantInfo == null ? "" : merchantInfo.getMerchantNo());
			map.put("merchantName", merchantInfo == null ? "" : merchantInfo.getMerchantName());
			map.put("tradeTime", DateKit.dateToStr(collectionTrade.getTradeTime(), DateKit.STR_DATEFORMATE));
			map.put("tradeType", collectionTrade.getTradeTypeTxt());
			map.put("bussType", collectionTrade.getBussTypeTxt());
			map.put("custName", collectionTrade.getCustName());
			map.put("cardID", collectionTrade.getCardID());
			map.put("mobileBank", collectionTrade.getMobileBank());
			map.put("bankcardNo", collectionTrade.getBankcardNo());
			map.put("amount", collectionTrade.getAmount().toPlainString());
			if (isAdmin) {
				map.put("bankFee", collectionTrade.getBankFee().toPlainString());
			}
			map.put("merFee", collectionTrade.getMerFee().toPlainString());
			map.put("resCode", collectionTrade.getResCode());
			map.put("resMsg", collectionTrade.getResMsg());
			map.put("resultCode", collectionTrade.getResultCode());
			map.put("resultMsg", collectionTrade.getResultMsg());
			map.put("finalCode", collectionTrade.getFinalCodeTxt());
			map.put("clearStatus", collectionTrade.getClearStatusTxt());
			map.put("clearDate", DateKit.dateToStr(collectionTrade.getClearDate(), DateKit.STR_DATEFORMATE));
			map.put("clearNo", collectionClear == null ? "" : collectionClear.getClearNo());
			map.put("mat", DateKit.dateToStr(collectionTrade.getMat(), DateKit.STR_DATEFORMATE));
			map.put("operLoginname", oper == null ? "" : oper.getLoginname());

			accum.add(map);
		}
		return accum;
	}
}
