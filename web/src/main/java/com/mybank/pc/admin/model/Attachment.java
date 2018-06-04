package com.mybank.pc.admin.model;

import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.base.BaseAttachment;

import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Attachment extends BaseAttachment<Attachment> {
	public static final Attachment dao = new Attachment().dao();

	@Override
	public String getTableName() {
		return "s_attachment";
	}

	public List<Attachment> findByObjIdAndModule(Integer objId, String module) {
		return dao.find("select * from " + getTableName() + " where objId=? and module =? and dAt is null", objId,
				module);
	}

	public String getAccessUrl() {
		return CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "qn_url") + getPath();
	}
}