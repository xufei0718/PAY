package com.mybank.pc.exception;

public class ValidateCTRException extends BaseCollectionRuntimeException {

	private static final long serialVersionUID = 1L;

	public ValidateCTRException() {
		super();
	}

	public ValidateCTRException(String message) {
		super(message);
	}

	public ValidateCTRException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidateCTRException(Throwable cause) {
		super(cause);
	}

	protected ValidateCTRException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
