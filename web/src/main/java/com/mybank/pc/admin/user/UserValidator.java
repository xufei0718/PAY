package com.mybank.pc.admin.user;

import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.User;
import com.mybank.pc.core.CoreValidator;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/12/1.
 */
public class UserValidator extends CoreValidator {
	public static final String LOGINNAME_EXIST = "登录名已被占用";
	public static final String NICKNAME_EXIST = "昵称已被占用";
	public static final String EMAIL_EXIST = "邮箱地址已被占用";
	public static final String PHONE_EXIST = "手机号已被占用";

	@Override
	protected void validate(Controller controller) {
		User user = controller.getModel(User.class, "", true);
		String ak = getActionKey();
		LogKit.debug("ActionKey =" + ak);
		if (ak.equals("/ad01/save")) {

			List _list = User.dao.find("select id from s_user where loginname=?", user.getLoginname());
			if (!_list.isEmpty()) {
				addError(Consts.REQ_JSON_CODE.fail.name(), LOGINNAME_EXIST);
				return;
			}
			// _list=User.dao.find("select id from s_user where
			// nickname=?",user.getNickname());
			// if(!_list.isEmpty()){
			// addError(Consts.REQ_JSON_CODE.fail.name(),NICKNAME_EXIST);
			// return ;
			// }
			_list = User.dao.find("select id from s_user where email=?", user.getEmail());
			if (!_list.isEmpty()) {
				addError(Consts.REQ_JSON_CODE.fail.name(), EMAIL_EXIST);
				return;
			}
			_list = User.dao.find("select id from s_user where phone=?", user.getPhone());
			if (!_list.isEmpty()) {
				addError(Consts.REQ_JSON_CODE.fail.name(), PHONE_EXIST);
				return;
			}

		} else if (ak.equals("/ad01/update")) {
			BigInteger id = user.getId();
			User _user = User.dao.findFirst("select id from s_user where loginname=? and id<>?", user.getLoginname(),
					user.getId());
			if (_user != null) {
				addError(Consts.REQ_JSON_CODE.fail.name(), LOGINNAME_EXIST);
				return;
			}
			// _user=User.dao.findFirst("select id from s_user where nickname=?
			// and id<>?",user.getNickname(),user.getId());
			// if(_user!=null){
			// addError(Consts.REQ_JSON_CODE.fail.name(),NICKNAME_EXIST);
			// return ;
			// }
			_user = User.dao.findFirst("select id from s_user where email=? and id<>?", user.getEmail(), user.getId());
			if (_user != null) {
				addError(Consts.REQ_JSON_CODE.fail.name(), EMAIL_EXIST);
				return;
			}
			_user = User.dao.findFirst("select id from s_user where phone=? and id<>?", user.getPhone(), user.getId());
			if (_user != null) {
				addError(Consts.REQ_JSON_CODE.fail.name(), PHONE_EXIST);
				return;
			}
		}
	}

	@Override
	protected void handleError(Controller controller) {
		controller.renderJson(getErrorJSON());
	}
}
