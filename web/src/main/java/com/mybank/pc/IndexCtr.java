package com.mybank.pc;

import com.jfinal.aop.Clear;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.interceptors.AdminAAuthInterceptor;
import com.mybank.pc.interceptors.AdminIAuthInterceptor;

@Clear(AdminIAuthInterceptor.class)
public class IndexCtr extends CoreController{
    public void index(){
        String domain= CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),"siteDomain");
        redirect(domain+"/ad/index.html");
    }
}
