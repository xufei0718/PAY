package com.mybank.pc.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseCollectionRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BaseCollectionRuntimeException() {
		super();
	}

	public BaseCollectionRuntimeException(String message) {
		super(message);
	}

	public BaseCollectionRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseCollectionRuntimeException(Throwable cause) {
		super(cause);
	}

	protected BaseCollectionRuntimeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public Map<String, Object> getExceptionInfo() {
		return getExceptionInfo(this.getCause());
	}

	public static Map<String, Object> getExceptionInfo(Throwable cause) {
		Map<String, Object> exceptionResult = new HashMap<String, Object>();
		if (cause != null) {
			List<String> stackTraceArray = new ArrayList<String>();
			for (StackTraceElement stackTraceElement : cause.getStackTrace()) {
				stackTraceArray.add(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "("
						+ stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + ")");
			}

			exceptionResult.put("info", cause.getClass().getName() + ":" + cause.getMessage());
			exceptionResult.put("stackTrace", stackTraceArray);
		}
		return exceptionResult;
	}

}
