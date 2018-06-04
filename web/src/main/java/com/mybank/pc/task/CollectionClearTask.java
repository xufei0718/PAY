package com.mybank.pc.task;

import com.jfinal.aop.Duang;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.collection.clear.CClearSrv;
import com.mybank.pc.kits.DateKit;

import java.util.Date;

public class CollectionClearTask implements Runnable {
    private CClearSrv cClearSrv= Duang.duang(CClearSrv.class, Tx.class);
    @Override
    public void run() {
        String clearTime=StrKit.isBlank((String)CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),"clearTime"))?"23:30:00.000":(String)CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),"clearTime");
        LogKit.info("每日清分的计划任务准备开始执行，执行时间是:"+ DateKit.dateToStr(new Date(),DateKit.format4Login));
        clearTime=DateKit.dateToStr(new Date(), DateKit.yyyy_MM_dd) + " " +clearTime;
        LogKit.info("现在系统设置的清分时间为:"+clearTime);
        try {
            cClearSrv.doClear(DateKit.strToDate(clearTime , DateKit.format4Login));
        } catch (ActiveRecordException ae){
            LogKit.error("清分任务执行失败，请手动重试。原因:"+ae.getLocalizedMessage());
        } catch (RuntimeException re){
            LogKit.error("清分任务执行失败，请手动重试。原因:"+re.getLocalizedMessage());
        }
        LogKit.info("每日清分的计划任务执行结束，结束时间是:"+DateKit.dateToStr(new Date(),DateKit.format4Login));
    }
}
