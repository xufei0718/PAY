package com.mybank.pc.exception;

/**
 * 建立委托请求校验异常
 * 
 * @author hkun
 *
 */
public class ValidateEERException extends BaseCollectionRuntimeException {

	private static final long serialVersionUID = 1L;

	public ValidateEERException() {
		super();
	}

	public ValidateEERException(String message) {
		super(message);
	}

	public ValidateEERException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidateEERException(Throwable cause) {
		super(cause);
	}

	protected ValidateEERException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
