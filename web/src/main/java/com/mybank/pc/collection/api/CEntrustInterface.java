package com.mybank.pc.collection.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Duang;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.exception.EntrustRuntimeException;
import com.mybank.pc.exception.ValidateEERException;
import com.mybank.pc.kits.RSAKit;

@Clear
public class CEntrustInterface extends CoreController {

	private CEntrustApiSrv cEntrustApiSrv = Duang.duang(CEntrustApiSrv.class);

	@ActionKey("/coll/api/entrust/establish")
	public void establish() {
		String req = getPara("req");
		LogKit.info("req-CEntrustInterface-establish-reqparam[" + req + "]");

		Kv resp = Kv.create();
		String errorMsg = "";
		Kv reqParam = null;
		boolean isValidateReq = false;
		try {
			String decryptReq = RSAKit.decrypt(req, CollAPIRSAKey.COLL_API.getPrivateKey());
			JSONObject reqJson = JSON.parseObject(decryptReq);

			String merCode = reqJson.getString("merCode");
			String merchantID = reqJson.getString("merchantID");

			String accNo = reqJson.getString("accNo");
			String certifTp = reqJson.getString("certifTp");
			String certifId = reqJson.getString("certifId");
			String customerNm = reqJson.getString("customerNm");
			String phoneNo = reqJson.getString("phoneNo");
			String cvn2 = reqJson.getString("cvn2");
			String expired = reqJson.getString("expired");

			reqParam = Kv.by("merCode", merCode).set("merchantID", merchantID).set("accNo", accNo)
					.set("certifTp", certifTp).set("certifId", certifId).set("customerNm", customerNm)
					.set("phoneNo", phoneNo).set("cvn2", cvn2).set("expired", expired);

			LogKit.info("req-CEntrustInterface-establish[" + reqParam + "]");
			CEntrustApiSrv.validateEstablishRequest(reqParam);
			isValidateReq = true;
		} catch (ValidateEERException e) {
			e.printStackTrace();
			resp = resp.set("isSuccess", false).set("unionpayEntrust", null);
			errorMsg = e.getMessage();
			isValidateReq = false;
		} catch (Exception e) {
			e.printStackTrace();
			reqParam = null;
			isValidateReq = false;
		}

		if (isValidateReq) {
			try {
				resp = cEntrustApiSrv.establish(reqParam);
			} catch (EntrustRuntimeException e) {
				e.printStackTrace();
				resp = resp.set("isSuccess", false).set("unionpayEntrust", null);
				errorMsg = e.getMessage();
			} catch (Exception e) {
				e.printStackTrace();
				resp = resp.set("isSuccess", false).set("unionpayEntrust", null);
				errorMsg = "系统异常";
			}
		} else {
			resp.set("isSuccess", false).set("unionpayEntrust", null);
			errorMsg = "请求数据非法";
		}

		resp.set("errorMsg", errorMsg);
		LogKit.info("resp-CEntrustInterface-establish-respcontent[" + resp + "]");

		try {
			String respJson = JSON.toJSONString(resp);
			String encryptRespJson = RSAKit.encrypt(respJson,
					RSAKit.getPublicKey(CollAPIRSAKey.COLL_CLIENT.getPublicKey()));
			LogKit.info("resp-CEntrustInterface-establish[" + encryptRespJson + "]");
			renderJson(encryptRespJson);
		} catch (Exception e) {
			e.printStackTrace();
			renderJson("系统异常!!!");
		}
	}

}
