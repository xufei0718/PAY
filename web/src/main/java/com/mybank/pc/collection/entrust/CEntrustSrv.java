package com.mybank.pc.collection.entrust;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mybank.pc.collection.model.CollectionEntrust;
import com.mybank.pc.collection.model.UnionpayEntrust;
import com.mybank.pc.exception.EntrustRuntimeException;
import com.mybank.pc.exception.TxnKey;
import com.mybank.pc.exception.ValidateEERException;
import com.mybank.pc.interceptors.EntrustExceptionInterceptor;
import com.mybank.pc.kits.unionpay.acp.AcpResponse;
import com.mybank.pc.kits.unionpay.acp.AcpService;
import com.mybank.pc.kits.unionpay.acp.SDK;
import com.mybank.pc.kits.unionpay.acp.SDKConfig;
import com.mybank.pc.kits.unionpay.acp.SDKConstants;
import com.mybank.pc.merchant.model.MerchantInfo;

public class CEntrustSrv {

	public Kv[] establishAll(Kv kv) {
		Kv[] result = new Kv[3];
		try {
			result[0] = this.establish(kv.set("merCode", SDK.MER_CODE_REALTIME_YS_2));
		} catch (ValidateEERException e) {
			e.printStackTrace();
			result[0] = Kv.create().set("isSuccess", false).set("unionpayEntrust", null);
		} catch (EntrustRuntimeException e) {
			e.printStackTrace();
			result[0] = Kv.create().set("isSuccess", false).set("unionpayEntrust", e.getContext());
		} catch (Exception e) {
			e.printStackTrace();
			result[0] = Kv.create().set("isSuccess", false).set("unionpayEntrust", null);
		}

		try {
			result[1] = this.establish(kv.set("merCode", SDK.MER_CODE_REALTIME_YS_4));
		} catch (ValidateEERException e) {
			e.printStackTrace();
			result[1] = Kv.create().set("isSuccess", false).set("unionpayEntrust", null);
		} catch (EntrustRuntimeException e) {
			e.printStackTrace();
			result[1] = Kv.create().set("isSuccess", false).set("unionpayEntrust", e.getContext());
		} catch (Exception e) {
			e.printStackTrace();
			result[1] = Kv.create().set("isSuccess", false).set("unionpayEntrust", null);
		}

		try {
			result[2] = this.establish(kv.set("merCode", SDK.MER_CODE_BATCH_CH));
		} catch (ValidateEERException e) {
			e.printStackTrace();
			result[2] = Kv.create().set("isSuccess", false).set("unionpayEntrust", null);
		} catch (EntrustRuntimeException e) {
			e.printStackTrace();
			result[2] = Kv.create().set("isSuccess", false).set("unionpayEntrust", e.getContext());
		} catch (Exception e) {
			e.printStackTrace();
			result[2] = Kv.create().set("isSuccess", false).set("unionpayEntrust", null);
		}
		return result;
	}

	@Before({ EntrustExceptionInterceptor.class, Tx.class })
	@TxnKey("establish")
	public Kv establish(Kv kv) {
		Kv respKv = Kv.create();
		UnionpayEntrust unionpayEntrust = new UnionpayEntrust();
		respKv.set("unionpayEntrust", unionpayEntrust);
		boolean isSuccess = false;
		try {
			String merCode = kv.getStr("merCode");

			String accNo = kv.getStr("accNo");
			String certifTp = kv.getStr("certifTp");
			String certifId = kv.getStr("certifId");
			String customerNm = kv.getStr("customerNm");
			String phoneNo = kv.getStr("phoneNo");
			String cvn2 = kv.getStr("cvn2");
			String expired = kv.getStr("expired");

			String merchantID = kv.getStr("merchantID");
			String operID = kv.getStr("operID");

			Date now = new Date();
			String orderId = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(now) + accNo;
			if (orderId.length() > 40) {
				orderId = orderId.substring(0, 40);
			}
			String txnTime = new SimpleDateFormat("yyyyMMddHHmmss").format(now);

			unionpayEntrust.setCustomerNm(customerNm);
			unionpayEntrust.setCertifTp(certifTp);
			unionpayEntrust.setCertifId(certifId);
			unionpayEntrust.setAccNo(accNo);
			unionpayEntrust.setPhoneNo(phoneNo);
			unionpayEntrust.setCvn2(cvn2);
			unionpayEntrust.setExpired(expired);
			unionpayEntrust.setTradeNo(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(now) + certifId);
			unionpayEntrust.setOrderId(orderId);
			unionpayEntrust.setTxnTime(txnTime);
			unionpayEntrust.setMerchantID(merchantID);
			unionpayEntrust.setCat(now);
			unionpayEntrust.setMat(now);
			unionpayEntrust.setOperID(operID);

			SDK sdk = SDK.getSDK(merCode);
			AcpService acpService = sdk.getAcpService();
			String merId = sdk.getMerId();
			unionpayEntrust.setMerId(merId);

			if (SDK.MER_CODE_REALTIME_YS_2.equals(merCode) || SDK.MER_CODE_REALTIME_YS_4.equals(merCode)) {
				unionpayEntrust.toRealAuthBack();
			} else {
				unionpayEntrust.toEntrust();
			}

			// 如果存在成功实名认证/建立委托调用记录则直接处理
			try {
				UnionpayEntrust tmpUnionpayEntrust = unionpayEntrust.findSuccessOne();
				if (tmpUnionpayEntrust != null) {
					isSuccess = handlingSuccessCase(tmpUnionpayEntrust);
					respKv.set("unionpayEntrust", tmpUnionpayEntrust);
					return respKv.set("isSuccess", isSuccess);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Map<String, String> entrustRspData = unionpayEntrust.sendEntrustRequest();
			isSuccess = handlingEstablishResult(entrustRspData, acpService, SDKConstants.UTF_8_ENCODING,
					unionpayEntrust);
			return respKv.set("isSuccess", isSuccess);
		} catch (ValidateEERException e) {
			e.printStackTrace();
			throw e;
		} catch (EntrustRuntimeException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			EntrustRuntimeException xe = new EntrustRuntimeException(e);
			xe.setContext(unionpayEntrust);
			throw xe;
		}
	}

	public static void validateEstablishRequest(Kv kv) {
		String merCode = kv.getStr("merCode");
		String merchantID = kv.getStr("merchantID");

		SDK sdk = SDK.getSDK(merCode);
		if (sdk == null) {
			throw new ValidateEERException("非法的系统商户号代码[" + merCode + "]");
		}
		if (StringUtils.isBlank(merchantID)) {
			throw new ValidateEERException("商户ID不能为空");
		}
		MerchantInfo merchantInfo = MerchantInfo.dao.findById(merchantID = merchantID.trim());
		if (merchantInfo == null || "1".equals(merchantInfo.getStatus()) || merchantInfo.getDat() != null) {
			throw new ValidateEERException("商户[" + merchantID + "]" + "不存在或已停用/已删除");
		}
	}

	/**
	 * 存在成功的实名认证/建立委托调用记录，直接更新委托状态
	 * 
	 * @param unionpayEntrust
	 * @return
	 */
	private boolean handlingSuccessCase(UnionpayEntrust unionpayEntrust) {
		boolean result = true;
		try {
			CollectionEntrust query = new CollectionEntrust();
			query.setCustomerNm(unionpayEntrust.getCustomerNm());
			query.setCertifId(unionpayEntrust.getCertifId());
			query.setAccNo(unionpayEntrust.getAccNo());
			query.setMerId(unionpayEntrust.getMerId());

			CollectionEntrust collectionEntrust = query.findOne();
			boolean needSave = collectionEntrust == null;
			if (needSave) {
				collectionEntrust = new CollectionEntrust();
				collectionEntrust.setCustomerNm(unionpayEntrust.getCustomerNm());
				collectionEntrust.setCertifTp(unionpayEntrust.getCertifTp());
				collectionEntrust.setCertifId(unionpayEntrust.getCertifId());
				collectionEntrust.setAccNo(unionpayEntrust.getAccNo());
				collectionEntrust.setPhoneNo(unionpayEntrust.getPhoneNo());
				collectionEntrust.setCvn2(unionpayEntrust.getCvn2());
				collectionEntrust.setExpired(unionpayEntrust.getExpired());
				collectionEntrust.setMerId(unionpayEntrust.getMerId());
				collectionEntrust.setCat(unionpayEntrust.getCat());
			}
			collectionEntrust.setMat(new Date());
			collectionEntrust.setOperID(unionpayEntrust.getOperID());
			collectionEntrust.setStatus("0");

			if (needSave) {
				collectionEntrust.save();
			} else {
				collectionEntrust.update();
			}
			return result;
		} catch (Exception e) {
			EntrustRuntimeException xe = new EntrustRuntimeException(e);
			xe.setContext(unionpayEntrust);
			throw xe;
		}
	}

	/**
	 * 应答码规范参考open.unionpay.com帮助中心 下载 产品接口规范 《平台接入接口规范-第5部分-附录》
	 * 
	 * @param rspData
	 * @param acpService
	 * @param encoding
	 */
	private boolean handlingEstablishResult(Map<String, String> rspData, AcpService acpService, String encoding,
			UnionpayEntrust unionpayEntrust) {
		boolean result = false;
		try {
			CollectionEntrust query = new CollectionEntrust();
			query.setCustomerNm(unionpayEntrust.getCustomerNm());
			query.setCertifId(unionpayEntrust.getCertifId());
			query.setAccNo(unionpayEntrust.getAccNo());
			query.setMerId(unionpayEntrust.getMerId());

			CollectionEntrust collectionEntrust = query.findOne();
			boolean needSave = collectionEntrust == null;
			if (needSave) {
				collectionEntrust = new CollectionEntrust();
				collectionEntrust.setCustomerNm(unionpayEntrust.getCustomerNm());
				collectionEntrust.setCertifTp(unionpayEntrust.getCertifTp());
				collectionEntrust.setCertifId(unionpayEntrust.getCertifId());
				collectionEntrust.setAccNo(unionpayEntrust.getAccNo());
				collectionEntrust.setPhoneNo(unionpayEntrust.getPhoneNo());
				collectionEntrust.setCvn2(unionpayEntrust.getCvn2());
				collectionEntrust.setExpired(unionpayEntrust.getExpired());
				collectionEntrust.setMerId(unionpayEntrust.getMerId());
				collectionEntrust.setCat(unionpayEntrust.getCat());
			}
			collectionEntrust.setMat(unionpayEntrust.getCat());
			collectionEntrust.setOperID(unionpayEntrust.getOperID());

			boolean isEmpty = rspData.isEmpty();
			boolean isValidate = acpService.validate(rspData, encoding);

			if (isEmpty) {// 未返回正确的http状态
				LogKit.error("未获取到返回报文或返回http状态码非200");
				throw new RuntimeException("未获取到返回报文或返回http状态码非200");
			}
			if (isValidate) {
				LogKit.info("验证签名成功");
			} else {
				LogKit.error("验证签名失败");
				throw new RuntimeException("验证签名失败");
			}

			String respCode = rspData.get("respCode");
			String respMsg = rspData.get("respMsg");

			unionpayEntrust.setRespCode(respCode);
			unionpayEntrust.setRespMsg(respMsg);
			if (("00").equals(respCode)) {// 成功
				collectionEntrust.setStatus("0");
				unionpayEntrust.setFinalCode("0");
				result = true;
			} else {
				String status = collectionEntrust.getStatus();
				if (StringUtils.isBlank(status)) {
					collectionEntrust.setStatus("1");
				}
				unionpayEntrust.setFinalCode("2");
			}

			if (needSave) {
				collectionEntrust.save();
			} else {
				collectionEntrust.update();
			}
			unionpayEntrust.save();
			return result;
		} catch (Exception e) {
			EntrustRuntimeException xe = new EntrustRuntimeException(e);
			xe.setContext(unionpayEntrust);
			throw xe;
		}
	}

	@Before({ EntrustExceptionInterceptor.class, Tx.class })
	@TxnKey("terminate")
	public boolean terminate(Kv kv, String userId) {
		UnionpayEntrust unionpayEntrust = new UnionpayEntrust();
		try {
			String merCode = kv.getStr("merCode");
			String accNo = kv.getStr("accNo");
			int merchantID = kv.getInt("merchantID");

			SDK sdk = SDK.getSDK(merCode);
			SDKConfig sdkConfig = sdk.getSdkConfig();
			AcpService acpService = sdk.getAcpService();
			String encoding = "UTF-8";
			String merId = sdk.getMerId();

			unionpayEntrust.setAccNo(accNo);
			unionpayEntrust.setMerId(merId);

			CollectionEntrust query = new CollectionEntrust();
			query.setAccNo(accNo);
			query.setMerId(merId);
			CollectionEntrust collectionEntrust = query.findOne();
			if (collectionEntrust == null) {
				throw new RuntimeException("委托状态信息不存在");
			}

			Date now = new Date();
			String orderId = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(now) + accNo;
			if (orderId.length() > 40) {
				orderId = orderId.substring(0, 40);
			}
			String txnTime = new SimpleDateFormat("yyyyMMddHHmmss").format(now);

			unionpayEntrust.setCustomerNm(collectionEntrust.getCustomerNm());
			unionpayEntrust.setCertifTp(collectionEntrust.getCertifTp());
			unionpayEntrust.setCertifId(collectionEntrust.getCertifId());
			unionpayEntrust.setPhoneNo(collectionEntrust.getPhoneNo());
			unionpayEntrust.setCvn2(collectionEntrust.getCvn2());
			unionpayEntrust.setExpired(collectionEntrust.getExpired());
			unionpayEntrust.setTradeNo(
					new SimpleDateFormat("yyyyMMddHHmmssSSS").format(now) + collectionEntrust.getCertifId());
			unionpayEntrust.setOrderId(orderId);
			unionpayEntrust.setTxnType("74");
			unionpayEntrust.setTxnSubType("04");
			unionpayEntrust.setTxnTime(txnTime);
			unionpayEntrust.setMerchantID(String.valueOf(merchantID));
			unionpayEntrust.setCat(now);
			unionpayEntrust.setMat(now);
			unionpayEntrust.setOperID(userId);

			Map<String, String> contentData = new HashMap<String, String>();

			/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
			contentData.put("version", sdkConfig.getVersion()); // 版本号
			contentData.put("encoding", encoding); // 字符集编码 可以使用UTF-8,GBK两种方式
			contentData.put("signMethod", sdkConfig.getSignMethod()); // 签名方法
																		// 目前只支持01-RSA方式证书加密
			contentData.put("txnType", "74"); // 交易类型 11-代收
			contentData.put("txnSubType", "04"); // 交易子类型 01-实名认证
			contentData.put("bizType", "000501"); // 业务类型 代收产品
			contentData.put("channelType", "07"); // 渠道类型07-PC

			/*** 商户接入参数 ***/
			contentData.put("merId", merId); // 商户号码（商户号码777290058110097仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】
			contentData.put("accessType", "0"); // 接入类型，商户接入固定填0，不需修改
			contentData.put("orderId", orderId); // 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
			contentData.put("txnTime", txnTime); // 订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效

			// 如果商户号开通了【商户对敏感信息加密】的权限那么需要对 accNo，pin和phoneNo，
			// cvn2，expired加密（如果这些上送的话），对敏感信息加密使用：
			String accNoEnc = acpService.encryptData(accNo, "UTF-8"); // 这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
			contentData.put("accNo", accNoEnc);
			contentData.put("encryptCertId", acpService.getEncryptCertId());// 加密证书的certId，配置在acp_sdk.properties文件
																			// acpsdk.encryptCert.path属性下

			// 如果商户号未开通【商户对敏感信息加密】权限那么不需对敏感信息加密使用：
			// contentData.put("accNo",accNo); //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号

			/** 对请求参数进行签名并发送http post请求，接收同步应答报文 **/

			Map<String, String> reqData = acpService.sign(contentData, encoding); // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
			unionpayEntrust.setReq(JsonKit.toJson(contentData));

			String requestBackUrl = sdkConfig.getBackRequestUrl(); // 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的
																	// acpsdk.backTransUrl
			AcpResponse acpResponse = acpService.post(reqData, requestBackUrl, encoding); // 发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
			Map<String, String> rspData = acpResponse.getRspData();

			unionpayEntrust.setResp(JsonKit.toJson(rspData));
			return handlingTerminateResult(rspData, acpService, encoding, unionpayEntrust, collectionEntrust);
		} catch (EntrustRuntimeException e) {
			throw e;
		} catch (Exception e) {
			EntrustRuntimeException xe = new EntrustRuntimeException(e);
			xe.setContext(unionpayEntrust);
			throw xe;
		}
	}

	private boolean handlingTerminateResult(Map<String, String> rspData, AcpService acpService, String encoding,
			UnionpayEntrust unionpayEntrust, CollectionEntrust collectionEntrust) {
		boolean result = false;
		try {
			collectionEntrust.setMat(unionpayEntrust.getCat());
			collectionEntrust.setOperID(unionpayEntrust.getOperID());

			boolean isEmpty = rspData.isEmpty();
			boolean isValidate = acpService.validate(rspData, encoding);

			if (isEmpty) {// 未返回正确的http状态
				LogKit.error("未获取到返回报文或返回http状态码非200");
				throw new RuntimeException("未获取到返回报文或返回http状态码非200");
			}
			if (isValidate) {
				LogKit.info("验证签名成功");
			} else {
				LogKit.error("验证签名失败");
				throw new RuntimeException("验证签名失败");
			}

			String respCode = rspData.get("respCode");
			String respMsg = rspData.get("respMsg");

			unionpayEntrust.setRespCode(respCode);
			unionpayEntrust.setRespMsg(respMsg);
			if (("00").equals(respCode)) {// 成功
				collectionEntrust.setStatus("2");
				unionpayEntrust.setFinalCode("0");
				result = true;
			} else {
				if (StringUtils.isBlank(collectionEntrust.getStatus())) {
					collectionEntrust.setStatus("1");
				}
				unionpayEntrust.setFinalCode("2");
			}

			collectionEntrust.update();
			unionpayEntrust.save();
			return result;
		} catch (Exception e) {
			EntrustRuntimeException xe = new EntrustRuntimeException(e);
			xe.setContext(unionpayEntrust);
			throw xe;
		}
	}

	public void handlingException(Invocation invocation, EntrustRuntimeException e) {
		Method method = invocation.getMethod();
		if (method.isAnnotationPresent(TxnKey.class)) {
			TxnKey txnKey = method.getAnnotation(TxnKey.class);
			String txnKeyValue = txnKey.value();

			UnionpayEntrust unionpayEntrust = (UnionpayEntrust) e.getContext();
			unionpayEntrust.setExceInfo(JsonKit.toJson(e.getExceptionInfo()));
			unionpayEntrust.setFinalCode("2");
			unionpayEntrust.save();

			CollectionEntrust query = new CollectionEntrust();
			String customerNm = unionpayEntrust.getCustomerNm();
			query.setCustomerNm(customerNm == null ? "" : customerNm);
			String certifId = unionpayEntrust.getCertifId();
			query.setCertifId(certifId == null ? "" : certifId);
			String accNo = unionpayEntrust.getAccNo();
			query.setAccNo(accNo == null ? "" : accNo);
			String merId = unionpayEntrust.getMerId();
			query.setMerId(merId == null ? "" : merId);

			CollectionEntrust collectionEntrust = query.findOne();
			boolean needSave = false;
			if (collectionEntrust == null) {
				if (txnKeyValue.equals("establish")) {
					collectionEntrust = new CollectionEntrust();
					collectionEntrust.setCustomerNm(unionpayEntrust.getCustomerNm());
					collectionEntrust.setCertifTp(unionpayEntrust.getCertifTp());
					collectionEntrust.setCertifId(unionpayEntrust.getCertifId());
					collectionEntrust.setAccNo(unionpayEntrust.getAccNo());
					collectionEntrust.setPhoneNo(unionpayEntrust.getPhoneNo());
					collectionEntrust.setCvn2(unionpayEntrust.getCvn2());
					collectionEntrust.setExpired(unionpayEntrust.getExpired());
					collectionEntrust.setMerId(unionpayEntrust.getMerId());
					collectionEntrust.setCat(unionpayEntrust.getCat());
					needSave = true;
				}
				if (txnKeyValue.equals("terminate")) {
					return;
				}
			}

			collectionEntrust.setMat(unionpayEntrust.getCat());
			if (StringUtils.isBlank(collectionEntrust.getStatus())) {
				collectionEntrust.setStatus("1");
			}
			collectionEntrust.setOperID(unionpayEntrust.getOperID());

			if (needSave) {
				collectionEntrust.save();
			} else {
				collectionEntrust.update();
			}
		}

	}

}
