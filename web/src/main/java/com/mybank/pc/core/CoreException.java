package com.mybank.pc.core;

/**
 * Created by yuhaihui8913 on 2016/12/6.
 */
public class CoreException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String code = "999";
	private String msg;

	public CoreException() {
		super();
	}

	public CoreException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public CoreException(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public CoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public CoreException(Throwable cause) {
		super(cause);
	}

	protected CoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
