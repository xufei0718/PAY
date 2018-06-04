package com.mybank.pc.interceptors;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.JFinal;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.core.CoreException;
import com.mybank.pc.kits.ReqKit;
import com.mybank.pc.kits.ResKit;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yuhaihui8913 on 2016/12/6.
 */
public class ExceptionInterceptor implements Interceptor {

	public void intercept(Invocation invocation) {
		CoreController controller = (CoreController) invocation.getController();
		HttpServletRequest request = controller.getRequest();
		try {
			invocation.invoke();
		} catch (Exception e) {
			doLog(invocation, e);
			boolean isAjax = ReqKit.isAjaxRequest(request);
			String msg = formatException(e);
			if (isAjax) {
				if (e instanceof CoreException) {
					controller.renderFailJSON(((CoreException) e).getMsg());
				} else {
					controller.renderFailJSON(ResKit.getMsg("500_msg"));
				}
			} else {
				String redirctUrl = request.getHeader("referer");
				if (StrKit.isBlank(redirctUrl)) {
					redirctUrl = request.getRequestURI();
				}

				String ak = invocation.getActionKey();
				String ck = invocation.getControllerKey();
				controller.setAttr(controller.ERROR_MSG, msg);
				controller.setAttr("redirctUrl", redirctUrl);

				/**
				 *
				 * 此处需要按照项目需求进行修改，当出现异常的时候，需要如何进行页面的渲染。
				 *
				 */
				if (e instanceof CoreException) {
					controller.render("/WEB-INF/template/common/999.html");
				} else {
					controller.render("/WEB-INF/template/common/500.html");
				}
			}
		} finally {
		}
	}

	private void doLog(Invocation ai, Exception e) {
		// 开发模式
		if (JFinal.me().getConstants().getDevMode()) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder("\n---Exception Log Begin---\n");
		sb.append("Controller:").append(ai.getController().getClass().getName()).append("\n");
		sb.append("Method:").append(ai.getMethodName()).append("\n");
		sb.append("Exception Type:").append(e.getClass().getName()).append("\n");
		sb.append("Exception Details:");
		LogKit.error(sb.toString(), e);
	}

	private static String formatException(Exception e) {
		String message = null;
		Throwable ourCause = e;
		while ((ourCause = e.getCause()) != null) {
			e = (Exception) ourCause;
		}
		String eClassName = e.getClass().getName();
		// 一些常见异常提示
		if ("java.lang.NumberFormatException".equals(eClassName)) {
			message = ResKit.getMsg("NumberFormatException_msg");
		} else if (e instanceof CoreException) {
			message = "[" + ((CoreException) e).getCode() + "]" + e.getMessage();
		} else if (e instanceof RuntimeException) {
			message = e.toString();
		}

		// 获取默认异常提示
		if (StrKit.isBlank(message)) {
			message = ResKit.getMsg("SysBusy_msg");
		}
		// 替换特殊字符
		message = message.replaceAll("\"", "'");
		return message;
	}
}
