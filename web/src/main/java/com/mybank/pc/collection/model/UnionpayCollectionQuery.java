package com.mybank.pc.collection.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.mybank.pc.collection.model.base.BaseUnionpayCollectionQuery;
import com.mybank.pc.collection.model.sender.BaseUnionpayCollectionTrade;
import com.mybank.pc.collection.model.sender.QueryRequestBuilder;
import com.mybank.pc.collection.model.sender.SendProxy;
import com.mybank.pc.collection.model.sender.SenderBuilder;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

/**
 * Generated by JFinal.
 */
public class UnionpayCollectionQuery extends BaseUnionpayCollectionQuery<UnionpayCollectionQuery>
		implements BaseUnionpayCollectionTrade {
	private static final long serialVersionUID = 1L;
	public static final UnionpayCollectionQuery dao = new UnionpayCollectionQuery().dao();

	private SendProxy sendProxy;

	public UnionpayCollectionQuery assemblyQueryRequest() {
		accept(new QueryRequestBuilder());
		setReq(JsonKit.toJson(sendProxy.getReqData()));
		return this;
	}

	/**
	 * 对请求参数进行签名并发送http post请求，接收同步应答报文
	 * 
	 * @return
	 * @throws Exception
	 */
	public SendProxy queryResult() throws Exception {
		if (sendProxy == null) {
			assemblyQueryRequest();
		}
		sendProxy.send();
		this.setResp(JsonKit.toJson(sendProxy.getRspData()));
		return sendProxy;
	}

	public void setFieldFromQueryResp() {
		Map<String, String> queryRspData = sendProxy == null ? null : sendProxy.getRspData();
		if (queryRspData != null) {
			String respCode = queryRspData.get("respCode");
			String respMsg = queryRspData.get("respMsg");
			String acqInsCode = queryRspData.get("acqInsCode");
			String queryId = queryRspData.get("queryId");
			String traceNo = queryRspData.get("traceNo");
			String traceTime = queryRspData.get("traceTime");
			String settleAmt = queryRspData.get("settleAmt");
			String settleCurrencyCode = queryRspData.get("settleCurrencyCode");
			String settleDate = queryRspData.get("settleDate");
			String exchangeRate = queryRspData.get("exchangeRate");
			String exchangeDate = queryRspData.get("exchangeDate");
			String currencyCode = queryRspData.get("currencyCode");
			String txnAmt = queryRspData.get("txnAmt");
			String origRespCode = queryRspData.get("origRespCode");
			String origRespMsg = queryRspData.get("origRespMsg");
			String accNo = sendProxy.decryptData(queryRspData.get("accNo"));
			String payCardType = queryRspData.get("payCardType");
			String payType = queryRspData.get("payType");
			String payCardNo = queryRspData.get("payCardNo");
			String payCardIssueName = queryRspData.get("payCardIssueName");
			String cardTransData = queryRspData.get("cardTransData");
			String issuerIdentifyMode = queryRspData.get("issuerIdentifyMode");

			setRespCode(respCode);
			setRespMsg(respMsg);
			setAcqInsCode(acqInsCode);
			setQueryId(queryId);
			setTraceNo(traceNo);
			setTraceTime(traceTime);
			setSettleAmt(settleAmt);
			setSettleCurrencyCode(settleCurrencyCode);
			setSettleDate(settleDate);
			setExchangeRate(exchangeRate);
			setExchangeDate(exchangeDate);
			setCurrencyCode(currencyCode);
			setTxnAmt(txnAmt);
			setOrigRespCode(origRespCode);
			setOrigRespMsg(origRespMsg);
			setAccNo(accNo);
			setPayCardType(payCardType);
			setPayType(payType);
			setPayCardNo(payCardNo);
			setPayCardIssueName(payCardIssueName);
			setCardTransData(cardTransData);
			setIssuerIdentifyMode(issuerIdentifyMode);
		}
	}

	public boolean validateQueryResp() {
		return sendProxy.validateResp();
	}

	public boolean isTimeout(int timeoutMinute) {
		return isTimeout(getTxnTime(), getMat(), getRespCode(), timeoutMinute);
	}

	public boolean isTimeout(Date queryDate, int timeoutMinute) {
		return isTimeout(getTxnTime(), queryDate, getRespCode(), timeoutMinute);
	}

	public static boolean isTimeout(String time, Date queryTime, String respCode, int timeoutMinute) {
		try {
			Date timeDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(time);
			return "34".equals(respCode)
					&& DateUtil.between(timeDate, queryTime, DateUnit.MINUTE, false) > timeoutMinute;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<UnionpayCollectionQuery> findUnionpayCollectionQuery() {
		return findUnionpayCollectionQuery(this);
	}

	public static List<UnionpayCollectionQuery> findByOrderId(String orderId) {
		SqlPara sqlPara = Db.getSqlPara("collection_trade.findUnionpayCollectionQuery", Kv.by("orderId", orderId));
		return UnionpayCollectionQuery.dao.find(sqlPara);
	}

	public static List<UnionpayCollectionQuery> findUnionpayCollectionQuery(UnionpayCollectionQuery param) {
		SqlPara sqlPara = Db.getSqlPara("collection_trade.findUnionpayCollectionQuery", param);
		return UnionpayCollectionQuery.dao.find(sqlPara);
	}

	@Override
	public BaseUnionpayCollectionTrade accept(SenderBuilder senderBuilder) {
		SendProxy tmpSendProxy = senderBuilder.buildTo(this);
		if (this.sendProxy == null) {
			this.sendProxy = tmpSendProxy;
		}
		return this;
	}

	@Override
	public void setSendProxy(SendProxy sendProxy) {
		this.sendProxy = sendProxy;
	}

	@JSONField(serialize = false)
	public SendProxy getSendProxy() {
		return this.sendProxy;
	}
}