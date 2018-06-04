package com.mybank.pc.exception;

public class EntrustRuntimeException extends BaseCollectionRuntimeException {

	private static final long serialVersionUID = 1L;

	private Object context;

	public EntrustRuntimeException() {
		super();
	}

	public EntrustRuntimeException(String message) {
		super(message);
	}

	public EntrustRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntrustRuntimeException(Throwable cause) {
		super(cause);
	}

	protected EntrustRuntimeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public Object getContext() {
		return context;
	}

	public void setContext(Object context) {
		this.context = context;
	}

}
