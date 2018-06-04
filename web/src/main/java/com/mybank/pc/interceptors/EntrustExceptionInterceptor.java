package com.mybank.pc.interceptors;

import com.jfinal.aop.Duang;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.mybank.pc.collection.entrust.CEntrustSrv;
import com.mybank.pc.exception.EntrustRuntimeException;

public class EntrustExceptionInterceptor implements Interceptor {

	private CEntrustSrv cEntrustSrv = Duang.duang(CEntrustSrv.class);

	public void intercept(Invocation invocation) {
		try {
			invocation.invoke();
		} catch (EntrustRuntimeException e) {
			cEntrustSrv.handlingException(invocation, e);
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

}
