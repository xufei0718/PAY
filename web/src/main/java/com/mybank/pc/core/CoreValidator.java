package com.mybank.pc.core;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.validate.Validator;
import com.mybank.pc.Consts;

/**
 * Created by yuhaihui8913 on 2017/12/1.
 */
public abstract class CoreValidator extends Validator {
	public String getErrorJSON() {
		String msg = getController().getAttrForStr(Consts.REQ_JSON_CODE.fail.name());
		JSONObject jo = new JSONObject();
		jo.put("resCode", Consts.REQ_JSON_CODE.fail.name());
		jo.put("resMsg", msg);
		return jo.toJSONString();
	}
}
