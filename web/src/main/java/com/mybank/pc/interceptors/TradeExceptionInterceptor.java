package com.mybank.pc.interceptors;

import com.jfinal.aop.Duang;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.mybank.pc.collection.trade.CTradeSrv;
import com.mybank.pc.exception.TradeRuntimeException;

public class TradeExceptionInterceptor implements Interceptor {

	private CTradeSrv cTradeSrv = Duang.duang(CTradeSrv.class);

	public void intercept(Invocation invocation) {
		try {
			invocation.invoke();
		} catch (TradeRuntimeException e) {
			cTradeSrv.handlingException(invocation, e);
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

}
