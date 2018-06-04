package com.mybank.pc.admin.role;

import com.jfinal.core.Controller;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.Role;
import com.mybank.pc.core.CoreValidator;

import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称: [cms] 包: [com.dbd.cms.controller.admin.role] 类名称: [RoleValidator] 类描述:
 * [] 创建人: [于海慧] 创建时间: [2016/12/6] 修改人: [] 修改时间: [] 修改备注: [] 版本: [v1.0]
 */
public class RoleValidator extends CoreValidator {
	public static final String ROLENAME_EXIST = "角色名称被占用";

	protected void validate(Controller controller) {
		String ak = getActionKey();
		Role role = controller.getModel(Role.class, "", true);
		List<Role> roleList;
		if (ak.equals("/ad02/save")) {
			roleList = Role.dao.find("select * from s_role where name=?", role.getName());
			if (!roleList.isEmpty()) {
				addError(Consts.REQ_JSON_CODE.fail.name(), ROLENAME_EXIST);
			}

		} else if (ak.equals("/ad02/update")) {
			roleList = Role.dao.find("select * from s_role where name=? and id<>?", role.getName(), role.getId());
			if (!roleList.isEmpty()) {
				addError(Consts.REQ_JSON_CODE.fail.name(), ROLENAME_EXIST);
			}
		}
	}

	protected void handleError(Controller controller) {
		controller.renderJson(getErrorJSON());
	}
}
