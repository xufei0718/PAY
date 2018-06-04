package com.mybank.pc.exception;

import com.mybank.pc.collection.model.sender.SendProxy;

public class ValidateUnionpayRespException extends BaseCollectionRuntimeException {

	private static final long serialVersionUID = 1L;

	private SendProxy sendProxy;

	private String merid;

	public ValidateUnionpayRespException() {
		super();
	}

	public ValidateUnionpayRespException(String message) {
		super(message);
	}

	public ValidateUnionpayRespException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidateUnionpayRespException(Throwable cause) {
		super(cause);
	}

	protected ValidateUnionpayRespException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public String getMerid() {
		return merid;
	}

	public void setMerid(String merid) {
		this.merid = merid;
	}

	public SendProxy getSendProxy() {
		return sendProxy;
	}

	public void setSendProxy(SendProxy sendProxy) {
		this.sendProxy = sendProxy;
	}

}
