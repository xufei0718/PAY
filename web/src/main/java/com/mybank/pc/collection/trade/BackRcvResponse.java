package com.mybank.pc.collection.trade;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;

import com.jfinal.aop.Clear;
import com.jfinal.aop.Duang;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.LogKit;
import com.mybank.pc.collection.model.UnionpayCallbackLog;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.interceptors.AdminIAuthInterceptor;
import com.mybank.pc.kits.unionpay.acp.SDK;
import com.mybank.pc.kits.unionpay.acp.SDKConstants;

/**
 * 重要：联调测试时请仔细阅读注释！
 * 
 * 产品：代收产品<br>
 * 功能：后台通知接收处理示例 <br>
 * 日期： 2015-09<br>
 * 版本： 1.0.0 版权： 中国银联<br>
 * 声明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码，性能，规范性等方面的保障<br>
 * 该接口参考文档位置：open.unionpay.com帮助中心 下载 产品接口规范 《网关支付产品接口规范》，<br>
 * 《平台接入接口规范-第5部分-附录》（内包含应答码接口规范，全渠道平台银行名称-简码对照表），
 * 测试过程中的如果遇到疑问或问题您可以：1）优先在open平台中查找答案： 调试过程中的问题或其他问题请在
 * https://open.unionpay.com/ajweb/help/faq/list 帮助中心 FAQ 搜索解决方案
 * 测试过程中产生的7位应答码问题疑问请在https://open.unionpay.com/ajweb/help/respCode/respCodeList
 * 输入应答码搜索解决方案 2） 咨询在线人工支持： open.unionpay.com注册一个用户并登陆在右上角点击“在线客服”，咨询人工QQ测试支持。
 * 交易说明： 前台类交易成功才会发送后台通知。后台类交易（有后台通知的接口）交易结束之后成功失败都会发通知。
 * 为保证安全，涉及资金类的交易，收到通知后请再发起查询接口确认交易成功。不涉及资金的交易可以以通知接口respCode=00判断成功。
 * 未收到通知时，查询接口调用时间点请参照此FAQ：https://open.unionpay.com/ajweb/help/faq/list?id=77&level=0&from=0
 */
@Clear(AdminIAuthInterceptor.class)
public class BackRcvResponse extends CoreController {

	private CTradeSrv cTradeSrv = Duang.duang(CTradeSrv.class);
	private CBatchQuerySrv cBatchQuerySrv = Duang.duang(CBatchQuerySrv.class);

	@ActionKey("/coll/backRcvResponse/receive")
	public void receive() {
		LogKit.info("BackRcvResponse接收后台通知开始");
		HttpServletRequest request = getRequest();

		String encoding = request.getParameter(SDKConstants.param_encoding);
		// 获取银联通知服务器发送的后台通知参数
		Map<String, String> reqParam = getAllRequestParam(request);
		UnionpayCallbackLog unionpayCallbackLog = log(reqParam, encoding);
		if ("1".equals(unionpayCallbackLog.getValidate())) {// 验签成功

			// 实时代收回调
			if ("11".equals(unionpayCallbackLog.getTxnType()) && ("02".equals(unionpayCallbackLog.getTxnSubType())
					|| "00".equals(unionpayCallbackLog.getTxnSubType()))) {
				cTradeSrv.updateOrderStatus(reqParam);
			}

			// 批量代收回调
			if ("21".equals(unionpayCallbackLog.getTxnType()) && "02".equals(unionpayCallbackLog.getTxnSubType())) {
				// 00 批次[xxxx]已处理完成，请发起批量查询交易
				if ("00".equals(unionpayCallbackLog.getRespCode())) {
					cBatchQuerySrv.batchQueryOne(reqParam);
				}
			}

		}

		LogKit.info("BackRcvResponse接收后台通知结束");
		// 返回给银联服务器http 200状态码
		renderText("ok");
	}

	public UnionpayCallbackLog log(Map<String, String> reqParam, String encoding) {
		UnionpayCallbackLog unionpayCallbackLog = new UnionpayCallbackLog();
		unionpayCallbackLog.setCat(new Date());
		unionpayCallbackLog.setInfo(JsonKit.toJson(reqParam));

		// 重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
		if (!SDK.validate(reqParam, encoding)) {
			LogKit.error("验证签名结果[失败].");
			// 验签失败，需解决验签问题
			unionpayCallbackLog.setValidate("0");
		} else {
			LogKit.info("验证签名结果[成功].");
			// 交易成功，更新商户订单状态
			unionpayCallbackLog.setValidate("1");

			String queryId = reqParam.get("queryId");
			String orderId = reqParam.get("orderId"); // 获取后台通知的数据，其他字段也可用类似方式获取
			String respCode = reqParam.get("respCode");
			String respMsg = reqParam.get("respMsg");
			String settleAmt = reqParam.get("settleAmt");
			String settleCurrencyCode = reqParam.get("settleCurrencyCode");
			String settleDate = reqParam.get("settleDate");
			String traceNo = reqParam.get("traceNo");
			String traceTime = reqParam.get("traceTime");
			String batchNo = reqParam.get("batchNo");
			String merId = reqParam.get("merId");
			String txnTime = reqParam.get("txnTime");
			String txnType = reqParam.get("txnType");
			String txnSubType = reqParam.get("txnSubType");
			String txnAmt = reqParam.get("txnAmt");

			unionpayCallbackLog.setQueryId(queryId);
			unionpayCallbackLog.setOrderId(orderId);
			unionpayCallbackLog.setRespCode(respCode);
			unionpayCallbackLog.setRespMsg(respMsg);
			unionpayCallbackLog.setSettleAmt(settleAmt);
			unionpayCallbackLog.setSettleCurrencyCode(settleCurrencyCode);
			unionpayCallbackLog.setSettleDate(settleDate);
			unionpayCallbackLog.setTraceNo(traceNo);
			unionpayCallbackLog.setTraceTime(traceTime);
			unionpayCallbackLog.setBatchNo(batchNo);
			unionpayCallbackLog.setMerId(merId);
			unionpayCallbackLog.setTxnTime(txnTime);
			unionpayCallbackLog.setTxnType(txnType);
			unionpayCallbackLog.setTxnSubType(txnSubType);
			unionpayCallbackLog.setTxnAmt(txnAmt);
		}
		unionpayCallbackLog.save();

		return unionpayCallbackLog;
	}

	/**
	 * 获取请求参数中所有的信息 当商户上送frontUrl或backUrl地址中带有参数信息的时候，
	 * 这种方式会将url地址中的参数读到map中，会导多出来这些信息从而致验签失败，这个时候可以自行修改过滤掉url中的参数或者使用getAllRequestParamStream方法。
	 * 
	 * @param request
	 * @return
	 */
	public Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				// 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				if (res.get(en) == null || "".equals(res.get(en))) {
					// System.out.println("======为空的字段名===="+en);
					res.remove(en);
				}
			}
		}
		return res;
	}

	/**
	 * 获取请求参数中所有的信息。
	 * 非struts可以改用此方法获取，好处是可以过滤掉request.getParameter方法过滤不掉的url中的参数。
	 * struts可能对某些content-type会提前读取参数导致从inputstream读不到信息，所以可能用不了这个方法。理论应该可以调整struts配置使不影响，但请自己去研究。
	 * 调用本方法之前不能调用req.getParameter("key");这种方法，否则会导致request取不到输入流。
	 * 
	 * @param request
	 * @return
	 */
	public Map<String, String> getAllRequestParamStream() {
		HttpServletRequest request = getRequest();
		Map<String, String> res = new HashMap<String, String>();
		try {
			String notifyStr = new String(IOUtils.toByteArray(request.getInputStream()), "UTF-8");
			LogKit.info("收到通知报文：" + notifyStr);
			String[] kvs = notifyStr.split("&");
			for (String kv : kvs) {
				String[] tmp = kv.split("=");
				if (tmp.length >= 2) {
					String key = tmp[0];
					String value = URLDecoder.decode(tmp[1], "UTF-8");
					res.put(key, value);
				}
			}
		} catch (UnsupportedEncodingException e) {
			LogKit.info("getAllRequestParamStream.UnsupportedEncodingException error: " + e.getClass() + ":"
					+ e.getMessage());
		} catch (IOException e) {
			LogKit.info("getAllRequestParamStream.IOException error: " + e.getClass() + ":" + e.getMessage());
		}
		return res;
	}
}
