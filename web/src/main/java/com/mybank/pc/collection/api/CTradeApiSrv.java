package com.mybank.pc.collection.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Clear;
import com.jfinal.aop.Duang;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.CardBin;
import com.mybank.pc.collection.model.CollectionEntrust;
import com.mybank.pc.collection.model.CollectionTrade;
import com.mybank.pc.collection.model.UnionpayCollection;
import com.mybank.pc.collection.trade.CTradeSrv;
import com.mybank.pc.exception.TradeRuntimeException;
import com.mybank.pc.exception.ValidateCTRException;
import com.mybank.pc.interceptors.AdminIAuthInterceptor;
import com.mybank.pc.kits.FeeKit;
import com.mybank.pc.kits.unionpay.acp.SDK;
import com.mybank.pc.merchant.model.MerchantInfo;

@Clear(AdminIAuthInterceptor.class)
public class CTradeApiSrv {

	private CEntrustApiSrv cEntrustApiSrv = Duang.duang(CEntrustApiSrv.class);
	private CTradeSrv cTradeSrv = Duang.duang(CTradeSrv.class);

	public Kv initiate(Kv reqParam) {
		Kv result = Kv.create();
		boolean isSuccess = false;
		UnionpayCollection unionpayCollection = null;
		CollectionTrade collectionTrade = null;

		Kv initiateRequest = validateAndBuildInitiateRequest(reqParam);
		if (isSuccess = initiateRequest.getBoolean("isValidate")) {
			unionpayCollection = (UnionpayCollection) initiateRequest.get("unionpayCollection");
			collectionTrade = (CollectionTrade) initiateRequest.get("collectionTrade");

			// 需先保存订单信息后，再在需要时发起请求
			try {
				boolean isRealtime = "1".equals(reqParam.getStr("bussType"));
				// 加急需发起实时交易请求
				if (isRealtime) {
					unionpayCollection.assemblyRealtimeRequest();
				}
				if (cTradeSrv.saveOrder(unionpayCollection, collectionTrade)) {
					isSuccess = isRealtime ? cTradeSrv.sendRealtimeOrder(unionpayCollection, collectionTrade) : true;
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
					e.printStackTrace();
					throw (ValidateCTRException) e;
				} else {
					throw new TradeRuntimeException(e);
				}
			}
		}

		return result.set("isSuccess", isSuccess).set("unionpayCollection", unionpayCollection);
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
			String merCode = kv.getStr("merCode");
			String merchantID = kv.getStr("merchantID");
			String bussType = kv.getStr("bussType");
			String orderId = kv.getStr("orderId");
			String txnAmt = kv.getStr("txnAmt");
			String txnSubType = kv.getStr("txnSubType");

			String accNo = kv.getStr("accNo");
			String certifTp = kv.getStr("certifTp");
			String certifId = kv.getStr("certifId");
			String customerNm = kv.getStr("customerNm");
			String phoneNo = kv.getStr("phoneNo");
			String cvn2 = kv.getStr("cvn2");
			String expired = kv.getStr("expired");

			if (StringUtils.isNotBlank(txnSubType)) {
				if (!("00".equals(txnSubType) || "02".equals(txnSubType))) {
					throw new ValidateCTRException("非法的交易子类型[" + txnSubType + "]");
				}
			}
			if (UnionpayCollection.findByOrderId(orderId) != null) {
				throw new ValidateCTRException("订单号重复[" + orderId + "]");
			}
			if (StringUtils.isBlank(bussType) || (!(bussType = bussType.trim()).equals("1") && !bussType.equals("2"))) {
				throw new ValidateCTRException("非法的业务类型[" + bussType + "]");
			}
			long numTxnAmt = -1;
			BigDecimal originaltxnAmt = null;
			try {
				numTxnAmt = new BigDecimal(txnAmt = txnAmt.trim()).longValue();
				if (numTxnAmt < 1) {
					throw new RuntimeException();
				}
				originaltxnAmt = new BigDecimal(numTxnAmt).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
			} catch (Exception e) {
				throw new ValidateCTRException("非法的交易金额[" + txnAmt + "]");
			}
			Object minAmt = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "minAmt");
			if (minAmt != null) {
				long numMiniAmt = -1;
				try {
					numMiniAmt = new BigDecimal(String.valueOf(minAmt).trim()).multiply(new BigDecimal(100))
							.longValue();
					if (numTxnAmt < numMiniAmt) {
						throw new RuntimeException();
					}
				} catch (Exception e) {
					throw new ValidateCTRException("交易金额不得小于[" + numMiniAmt + "]");
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

			CardBin cardBin = FeeKit.getCardBin(accNo = accNo.trim());
			if (cardBin == null) {
				throw new ValidateCTRException("不支持的卡类型!!");
			}
			boolean isRealtimeBuss = "1".equals(bussType) ? true : false;
			String merId = null;
			sdk = SDK.getSDK(merCode);
			if (sdk == null) {
				throw new ValidateCTRException("非法的系统商户号代码[" + merCode + "]");
			}
			merId = sdk.getMerId();

			if (isRealtimeBuss) {// 加急
				merchantFeeTradeType = "1";
				txnType = "11";
				unionpayCollection.setMerId(merId);
				if (sdk.equals(SDK.TEST_SDK) || sdk.equals(SDK.REALTIME_CH_SDK)) {
					unionpayCollection.toEntrustCollection();
				} else {
					unionpayCollection.toCollection();
				}
			} else if ("2".equals(bussType)) {// 批量
				merchantFeeTradeType = "2";
				txnType = "21"; // 取值：21 批量交易
				unionpayCollection.setMerId(merId);
				unionpayCollection.toEntrustCollection();
			}
			if (StringUtils.isNotBlank(txnSubType)) {
				unionpayCollection.setTxnSubType(txnSubType);
			}

			CollectionEntrust query = new CollectionEntrust();
			query.setAccNo(accNo);
			query.setMerId(merId);
			CollectionEntrust collectionEntrust = query.findOne();
			if (collectionEntrust == null || !"0".equals(collectionEntrust.getStatus())) {
				try {
					Kv entrustReqParam = Kv.by("merCode", merCode).set("merchantID", merchantID).set("accNo", accNo)
							.set("certifTp", certifTp).set("certifId", certifId).set("customerNm", customerNm)
							.set("phoneNo", phoneNo).set("cvn2", cvn2).set("expired", expired);
					cEntrustApiSrv.establish(entrustReqParam);
					collectionEntrust = query.findOne();
				} catch (Exception e) {
					e.printStackTrace();
					throw new ValidateCTRException("建立委托失败");
				}
			} else if (collectionEntrust != null && "0".equals(collectionEntrust.getStatus())) {
				cEntrustApiSrv.addMerchantCust(merchantID, accNo, certifId, customerNm, phoneNo);
			}
			if (collectionEntrust == null || !"0".equals(collectionEntrust.getStatus())) {
				throw new ValidateCTRException("客户委托信息不存在或未处于已委托状态");
			}

			String formattedTradeType = StringUtils.leftPad(tradeType, 2, '0');
			String formattedBussType = StringUtils.leftPad(bussType, 2, '0');

			Date now = new Date();
			String txnTime = isRealtimeBuss ? new SimpleDateFormat("yyyyMMddHHmmss").format(now) : null;
			// 流水号
			String tradeNo = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(now) + formattedTradeType
					+ formattedBussType + accNo;

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
			unionpayCollection.setQueryResultCount(0);

			collectionTrade.setTradeNo(tradeNo);
			collectionTrade.setTradeTime(now);
			collectionTrade.setTradeType(tradeType);
			collectionTrade.setBussType(bussType);
			collectionTrade.setAmount(originaltxnAmt);
			collectionTrade.setCustName(collectionEntrust.getCustomerNm());
			collectionTrade.setCardID(collectionEntrust.getCertifId());
			collectionTrade.setMobileBank(collectionEntrust.getPhoneNo());
			collectionTrade.setBankcardNo(accNo);
			collectionTrade.setFinalCode(finalCode);
			collectionTrade.setClearStatus(clearStatus);
			collectionTrade.setCat(now);
			collectionTrade.setMat(now);
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

}
