package com.mybank.pc.core;

import cn.hutool.log.StaticLog;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.CardBin;
import com.mybank.pc.admin.model.Param;
import com.mybank.pc.admin.model.Taxonomy;

import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/11/16.
 */
public class CoreData {

	public static void loadAllCache() {
		loadParam();
		loadTax();
		loadCardBin();
	}

	public static void loadParam() {
		List<Param> list = Param.dao.find("select * from " + Param.TABLE);
		CacheKit.removeAll(Consts.CACHE_NAMES.paramCache.name());
        for (Param p : list) {

            // RenderManager.me().getEngine().getEngineConfig().addSharedObject(p.getK(),p.getVal());
            CacheKit.put(Consts.CACHE_NAMES.paramCache.name(), p.getK(), p.getVal());
        }
        StaticLog.info("系统参数加载成功");
	}

	public static void loadTax() {
		CacheKit.removeAll(Consts.CACHE_NAMES.taxonomy.name());
		List<Taxonomy> list = Taxonomy.dao.findAllModule();
		List<Taxonomy> list1 = null;
		for (Taxonomy taxonomy : list) {
			list1 = Taxonomy.dao.findByModuleExcept(taxonomy.getModule());
			CacheKit.put(Consts.CACHE_NAMES.taxonomy.name(), taxonomy.getModule().concat("List"), list1);
			for (Taxonomy taxonomy1 : list1) {
				CacheKit.put(Consts.CACHE_NAMES.taxonomy.name(), taxonomy1.getId().toString(), taxonomy1);
			}
		}
	}

	public static void loadCardBin(){
		CacheKit.removeAll(Consts.CACHE_NAMES.cardBin.name());
		List<CardBin> list=CardBin.dao.find("select * from card_bin");
		for (CardBin cardBin:list){
			CacheKit.put(Consts.CACHE_NAMES.cardBin.name(),cardBin.getCBin(),cardBin);
		}
	}
}
