package com.mybank.pc.collection.api;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mybank.pc.collection.model.CollectionEntrust;
import com.mybank.pc.collection.model.UnionpayEntrust;
import com.mybank.pc.exception.EntrustRuntimeException;
import com.mybank.pc.exception.TxnKey;
import com.mybank.pc.exception.ValidateEERException;
import com.mybank.pc.interceptors.EntrustExceptionInterceptor;
import com.mybank.pc.kits.unionpay.acp.AcpService;
import com.mybank.pc.kits.unionpay.acp.SDK;
import com.mybank.pc.kits.unionpay.acp.SDKConstants;
import com.mybank.pc.merchant.model.MerchantCust;
import com.mybank.pc.merchant.model.MerchantInfo;

public class CEntrustApiSrv {

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
					addMerchantCust(merchantID, accNo, certifId, customerNm, phoneNo);
					respKv.set("unionpayEntrust", tmpUnionpayEntrust);
					return respKv.set("isSuccess", isSuccess);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Map<String, String> entrustRspData = unionpayEntrust.sendEntrustRequest();
			isSuccess = handlingEstablishResult(entrustRspData, acpService, SDKConstants.UTF_8_ENCODING,
					unionpayEntrust);
			if (isSuccess) {
				addMerchantCust(merchantID, accNo, certifId, customerNm, phoneNo);
			}
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
		if (kv == null || kv.isEmpty()) {
			throw new ValidateEERException("请求参数为空");
		}

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

	public void addMerchantCust(String merchantID, String accNo, String certifId, String customerNm, String phoneNo) {
		MerchantCust merchantCust = findMerchantCustOne(merchantID, customerNm, certifId, phoneNo, accNo);
		if (merchantCust == null) {
			MerchantInfo merchantInfo = MerchantInfo.dao.findById(merchantID = merchantID.trim());
			if (merchantInfo != null) {
				Date now = new Date();

				merchantCust = new MerchantCust();
				merchantCust.setMerID(merchantInfo.getId());
				merchantCust.setMerNo(merchantInfo.getMerchantNo());
				merchantCust.setCustName(customerNm);
				merchantCust.setCardID(certifId);
				merchantCust.setMobileBank(phoneNo);
				merchantCust.setBankcardNo(accNo);
				merchantCust.setCat(now);
				merchantCust.setMat(now);

				merchantCust.save();
			}
		}
	}

	public MerchantCust findMerchantCustOne(String merID, String custName, String cardID, String mobileBank,
			String bankcardNo) {
		SqlPara sqlPara = Db.getSqlPara("collection_api.findMerchantCustOne",
				Kv.by("merID", merID).set("custName", custName).set("cardID", cardID).set("mobileBank", mobileBank)
						.set("bankcardNo", bankcardNo));
		return MerchantCust.dao.findFirst(sqlPara);
	}

}
