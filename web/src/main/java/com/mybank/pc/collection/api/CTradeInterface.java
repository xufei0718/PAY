package com.mybank.pc.collection.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Duang;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.mybank.pc.collection.model.UnionpayCollection;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.exception.ValidateCTRException;
import com.mybank.pc.interceptors.AdminIAuthInterceptor;
import com.mybank.pc.kits.RSAKit;

@Clear(AdminIAuthInterceptor.class)
public class CTradeInterface extends CoreController {

	private CTradeApiSrv cTradeApiSrv = Duang.duang(CTradeApiSrv.class);

	@ActionKey("/coll/api/trade/initiate")
	public void initiate() {
		String req = getPara("req");
		LogKit.info("req-CTradeInterface-initiate-reqparam[" + req + "]");

		Kv resp = Kv.create();
		String errorMsg = "";
		Kv reqParam = null;
		boolean isValidateReq = false;
		try {
			String decryptReq = RSAKit.decrypt(req, CollAPIRSAKey.COLL_API.getPrivateKey());
			JSONObject reqJson = JSON.parseObject(decryptReq);

			String merCode = reqJson.getString("merCode");
			String merchantID = reqJson.getString("merchantID");
			String bussType = reqJson.getString("bussType");
			String orderId = reqJson.getString("orderId");
			String txnAmt = reqJson.getString("txnAmt");
			String txnSubType = reqJson.getString("txnSubType");

			String accNo = reqJson.getString("accNo");
			String certifTp = reqJson.getString("certifTp");
			String certifId = reqJson.getString("certifId");
			String customerNm = reqJson.getString("customerNm");
			String phoneNo = reqJson.getString("phoneNo");
			String cvn2 = reqJson.getString("cvn2");
			String expired = reqJson.getString("expired");

			reqParam = Kv.by("merCode", merCode).set("merchantID", merchantID).set("bussType", bussType)
					.set("orderId", orderId).set("txnAmt", txnAmt).set("txnSubType", txnSubType).set("accNo", accNo)
					.set("certifTp", certifTp).set("certifId", certifId).set("customerNm", customerNm)
					.set("phoneNo", phoneNo).set("cvn2", cvn2).set("expired", expired);

			LogKit.info("req-CTradeInterface-initiate[" + reqParam + "]");
			isValidateReq = true;
		} catch (Exception e) {
			e.printStackTrace();
			reqParam = null;
			isValidateReq = false;
		}

		if (isValidateReq) {
			try {
				resp = cTradeApiSrv.initiate(reqParam);
				if (!resp.getBoolean("isSuccess")) {
					UnionpayCollection unionpayCollection = (UnionpayCollection) resp.get("unionpayCollection");
					if (unionpayCollection != null) {
						errorMsg = unionpayCollection.getRespMsg();
					}
				}
			} catch (ValidateCTRException ve) {
				ve.printStackTrace();
				resp = Kv.create().set("isSuccess", false).set("unionpayCollection", null);
				errorMsg = "发起交易失败，" + ve.getMessage();
			} catch (Exception e) {
				e.printStackTrace();
				resp = Kv.create().set("isSuccess", false).set("unionpayCollection", null);
				errorMsg = "发起交易失败，系统内部错误";
			}
		} else {
			resp.set("isSuccess", false).set("unionpayCollection", null);
			errorMsg = "请求数据非法";
		}

		resp.set("errorMsg", errorMsg);
		LogKit.info("resp-CTradeInterface-initiate-respcontent[" + resp + "]");

		try {
			String respJson = JSON.toJSONString(resp);
			String encryptRespJson = RSAKit.encrypt(respJson,
					RSAKit.getPublicKey(CollAPIRSAKey.COLL_CLIENT.getPublicKey()));
			LogKit.info("resp-CTradeInterface-initiate[" + encryptRespJson + "]");
			renderJson(encryptRespJson);
		} catch (Exception e) {
			e.printStackTrace();
			renderJson("系统异常!!!");
		}
	}

	@ActionKey("/coll/api/trade/q")
	public void query() {
		String req = getPara("req");
		LogKit.info("req-CTradeInterface-query-reqparam[" + req + "]");

		Kv resp = Kv.create();
		String errorMsg = "";
		boolean isValidateReq = false;
		boolean isSuccess = false;
		String orderId = null;
		String merchantID = null;
		try {
			String decryptReq = RSAKit.decrypt(req, CollAPIRSAKey.COLL_API.getPrivateKey());
			JSONObject reqJson = JSON.parseObject(decryptReq);

			orderId = reqJson.getString("orderId");
			merchantID = reqJson.getString("merchantID");
			if (orderId != null && merchantID != null) {
				isValidateReq = true;
			}

			LogKit.info("req-CTradeInterface-query[" + orderId + "]");
		} catch (Exception e) {
			e.printStackTrace();
			isValidateReq = false;
		}

		UnionpayCollection unionpayCollection = null;
		if (isValidateReq) {
			try {
				unionpayCollection = UnionpayCollection.findByOrderIdAndMerchantID(orderId, merchantID);
				if (unionpayCollection != null) {
					isSuccess = true;
				} else {
					isSuccess = false;
					errorMsg = "订单数据不存在[" + orderId + "]";
				}
			} catch (Exception e) {
				e.printStackTrace();
				isSuccess = false;
				errorMsg = "查询失败，系统内部错误";
			}
		} else {
			errorMsg = "请求数据非法";
		}

		resp.set("isSuccess", isSuccess).set("unionpayCollection", unionpayCollection).set("errorMsg", errorMsg);
		LogKit.info("resp-CTradeInterface-query-respcontent[" + resp + "]");

		try {
			String respJson = JSON.toJSONString(resp);
			String encryptRespJson = RSAKit.encrypt(respJson,
					RSAKit.getPublicKey(CollAPIRSAKey.COLL_CLIENT.getPublicKey()));
			LogKit.info("resp-CTradeInterface-query[" + encryptRespJson + "]");
			renderJson(encryptRespJson);
		} catch (Exception e) {
			e.printStackTrace();
			renderJson("系统异常!!!");
		}
	}

}
