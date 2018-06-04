package com.mybank.pc.admin.role;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.Res;
import com.mybank.pc.admin.model.Role;
import com.mybank.pc.admin.model.RoleRes;
import com.mybank.pc.admin.model.UserRole;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.interceptors.AdminAAuthInterceptor;
import com.mybank.pc.interceptors.OpLogInterceptor;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by yuhaihui8913 on 2016/12/5.
 */
// @Before(AdminIAuthInterceptor.class)
public class RoleCtr extends CoreController {
	public void list() {
		String search = getPara("search");
		StringBuffer where = new StringBuffer("from s_role sr where 1=1 ");
		Page page = null;
		if (!isParaBlank("search")) {
			where.append(" and instr(sr.name,?)>0");
			page = Role.dao.paginate(getPN(), getPS(), "select * ", where.toString(), search);
		} else {
			page = Role.dao.paginate(getPN(), getPS(), "select * ", where.toString());
		}
		renderJson(page);
	}

	@Before({OpLogInterceptor.class,RoleValidator.class, Tx.class })
	public void save() {
		Role role = getModel(Role.class, "", true);
		role.save();
		CacheKit.removeAll(Consts.CACHE_NAMES.userRoles.name());
		renderSuccessJSON("角色新增成功", "");
	}

	@Before({ RoleValidator.class, Tx.class })
	public void update() {
		Role role = getModel(Role.class, "", true);
		role.update();
		CacheKit.removeAll(Consts.CACHE_NAMES.userRoles.name());
		renderSuccessJSON("角色更新成功", "");
	}

	@Before(Tx.class)
	public void del() {
		if (isParaExists("ids")) {
			String ids = getPara("ids");
			String[] _dis = ids.split(",");
			int id = 0;
			for (String s : _dis) {
				id = Integer.parseInt(s);
				Role.dao.deleteById(id);
				RoleRes.dao.delByRoleId(id);
				UserRole.dao.delByRoleId(id);
			}
			CacheKit.removeAll(Consts.CACHE_NAMES.userReses.name());
			CacheKit.removeAll(Consts.CACHE_NAMES.userRoles.name());
			renderSuccessJSON("角色删除成功", "");

		} else {
			renderFailJSON("角色删除失败,没有得到角色id", "");
		}
	}

	@Clear(AdminAAuthInterceptor.class)
	public void loadRes() {
		int roleId = getParaToInt("roleId", -1);
		List<Res> list = Res.dao.find("select sr.* from s_res sr,s_role_res srr where sr.id=srr.resId and srr.roleId=?",
				roleId);
		List<Long> rrIds = new ArrayList<>();
		for (Res r : list) {
			rrIds.add(r.getId());
		}
		String ret = Res.dao.listTree(rrIds);
		StaticLog.info(ret);
		renderJson(ret);
	}

	@Before(Tx.class)
	public void setRes() {
		int roleId = getParaToInt("roleId");
		RoleRes.dao.delByRoleId(roleId);
		String resIds = getPara("resIds");
		if (StrKit.notBlank(resIds)) {
			String[] resIds_array = StrUtil.split(resIds, ",");
			String resId = null;
			RoleRes rr = null;
			Res res=null;
			for (String s : resIds_array) {
				rr = new RoleRes();
				res=Res.dao.findById(new Integer(s));
				rr.setResId(new Integer(s));
				rr.setRoleId(roleId);
				rr.save();
				//处理 父亲节点部分子节点被选中的情况
				if(res.getPid()!=0){
					List<RoleRes> list=RoleRes.dao.find("select * from s_role_res where roleId=? and resId=?",roleId,res.getPid());
					if(list.isEmpty()){
						rr=new RoleRes();
						rr.setRoleId(roleId);
						rr.setResId(res.getPid());
						rr.save();
					}
				}
			}
		}
		CacheKit.removeAll(Consts.CACHE_NAMES.userReses.name());
		CacheKit.removeAll(Consts.CACHE_NAMES.userRoles.name());
		renderSuccessJSON("设置权限成功", "");
	}
}
