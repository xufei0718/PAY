package com.mybank.pc.task;

import com.jfinal.aop.Duang;
import com.mybank.pc.collection.clear.CClearSrv;


public class ClearEmailSendTask implements Runnable {
    CClearSrv cClearSrv= Duang.duang(CClearSrv.class.getSimpleName(),CClearSrv.class);
    @Override
    public void run() {
        cClearSrv.sendClearEmail(null);
    }
}
