package com.mybank.pc.merchant.user;

import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.User;
import com.mybank.pc.core.CoreValidator;
import com.mybank.pc.merchant.model.MerchantInfo;

import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/12/1.
 */
public class MerchantUserValidator extends CoreValidator {
	public static final String LOGINNAME_EXIST = "登录名已被占用";
	public static final String NICKNAME_EXIST = "姓名已被占用";
	public static final String EMAIL_EXIST = "邮箱地址已被占用";
	public static final String PHONE_EXIST = "手机号已被占用";

	@Override
	protected void validate(Controller controller) {
		User user = controller.getModel(User.class, "", true);


		String ak = getActionKey();
		LogKit.debug("ActionKey =" + ak);
		if (ak.equals("/mer02/save")) {
			MerchantInfo merInfo = controller.getAttr(Consts.CURR_USER_MER);
			List _list = User.dao.find("select id from s_user where loginname=? and d_at is null", user.getLoginname()+"@"+merInfo.getMerchantNo());
			if (!_list.isEmpty()) {
				addError(Consts.REQ_JSON_CODE.fail.name(), LOGINNAME_EXIST);
				return;
			}


		}
		/*else if (ak.equals("/mer02/update")) {
			BigInteger id = user.getId();
			User _user = User.dao.findFirst("select id from s_user where loginname=? and id<>?", user.getLoginname(),
					user.getId());
			if (_user != null) {
				addError(Consts.REQ_JSON_CODE.fail.name(), LOGINNAME_EXIST);
				return;
			}

		}*/
	}

	@Override
	protected void handleError(Controller controller) {
		controller.renderJson(getErrorJSON());
	}
}
