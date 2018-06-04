package com.mybank.pc.collection.trade;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Duang;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.mybank.pc.collection.model.CollectionTrade;
import com.mybank.pc.collection.model.UnionpayBatchCollection;
import com.mybank.pc.collection.model.UnionpayBatchCollectionQuery;
import com.mybank.pc.collection.model.UnionpayCallbackLog;
import com.mybank.pc.collection.model.UnionpayCollection;
import com.mybank.pc.collection.model.UnionpayCollectionQuery;
import com.mybank.pc.collection.model.sender.SendProxy;
import com.mybank.pc.exception.BaseCollectionRuntimeException;
import com.mybank.pc.kits.unionpay.acp.AcpResponse;
import com.mybank.pc.kits.unionpay.acp.file.collection.model.BatchCollectionRequest;
import com.mybank.pc.kits.unionpay.acp.file.collection.model.BatchCollectionResponse;
import com.mybank.pc.kits.unionpay.acp.file.collection.model.RequestContent;
import com.mybank.pc.kits.unionpay.acp.file.collection.model.ResponseContent;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

public class StatusSynchronizer {

	private static CBatchQuerySrv cBatchQuerySrv = Duang.duang(CBatchQuerySrv.class);

	private UnionpayCollection unionpayCollection;

	private UnionpayBatchCollection unionpayBatchCollection;

	public StatusSynchronizer(UnionpayCollection unionpayCollection) {
		this.unionpayCollection = unionpayCollection;
	}

	public void sync() throws Exception {
		if (unionpayCollection != null) {
			String lock = buildSyncLock(unionpayCollection);
			synchronized (lock) {
				try {
					boolean isBatchTradeOrder = unionpayCollection.isBatchTradeOrder();
					// 批量订单
					if (isBatchTradeOrder) {
						try {
							syncBatchOrderStatus(unionpayCollection);
							String merId = unionpayCollection.getMerId();
							String batchNo = unionpayCollection.getBatchNo();
							String txnTime = unionpayCollection.getTxnTime();
							String resultCode = unionpayCollection.getResultCode();

							boolean canContinue = CollectionUtils
									.isNotEmpty(UnionpayCallbackLog.findCallback(Kv.by("respCode", "00")
											.set("merId", merId).set("batchNo", batchNo).set("txnTime", txnTime)));
							canContinue = canContinue || DateUtil.between(unionpayBatchCollection.getCat(), new Date(),
									DateUnit.MINUTE, false) > 120;
							canContinue = canContinue && (StringUtils.isBlank(resultCode) || "03".equals(resultCode)
									|| "04".equals(resultCode) || "05".equals(resultCode));
							if (!canContinue) {
								return;
							}
						} catch (Exception e) {
							e.printStackTrace();
							if (this.unionpayBatchCollection != null) {
								return;
							}
						}
					}
					// 实时或带查询订单
					syncSingleOrderStatus(unionpayCollection);
				} catch (Exception e) {
					e.printStackTrace();
					if (unionpayCollection != null) {
						unionpayCollection.update();
					}
					throw e;
				}
			}
		}
	}

	private static String buildSyncLock(UnionpayCollection unionpayCollection) {
		String orderId = unionpayCollection.getOrderId();
		return ("syncOrderStatus-" + (StringUtils.isNotBlank(orderId) ? orderId : "")).intern();
	}

	public void syncBatchOrderStatus(UnionpayCollection unionpayCollection) {
		String batchId = unionpayCollection.getBatchId();
		String merId = unionpayCollection.getMerId();
		String batchNo = unionpayCollection.getBatchNo();
		String txnTime = unionpayCollection.getTxnTime();

		this.unionpayBatchCollection = UnionpayBatchCollection.findByIdOrBizColumn(batchId, merId, batchNo, txnTime);
		if (unionpayBatchCollection == null) {
			throw new RuntimeException("批量交易信息不存在["
					+ Kv.by("batchId", batchId).set("merId", merId).set("batchNo", batchNo).set("txnTime", txnTime)
					+ "]");
		}

		List<UnionpayBatchCollectionQuery> queryHistory = unionpayBatchCollection.findQueryHistory();
		if (CollectionUtils.isEmpty(queryHistory)) {
			if (unionpayBatchCollection.allowQuery()) {
				cBatchQuerySrv.batchQuery(unionpayBatchCollection);
			}
		} else {
			String orderId = unionpayCollection.getOrderId();
			UnionpayBatchCollectionQuery historyLastsQuery = queryHistory.get(0);
			String batchQueryRespCode = historyLastsQuery.getRespCode();
			if ("00".equals(batchQueryRespCode)) {// 查询成功
				BatchCollectionResponse batchCollectionResponse = historyLastsQuery.toBatchCollectionResponse();
				if (batchCollectionResponse != null) {
					ResponseContent responseContent = batchCollectionResponse.getContentByOrderId(orderId);
					cBatchQuerySrv.updateOrderStatus(responseContent);
				}
			} else if ("34".equals(batchQueryRespCode) && historyLastsQuery.isTimeout()) {// 交易超时
				BatchCollectionRequest batchCollectionRequest = unionpayBatchCollection.toBatchCollectionRequest();
				if (batchCollectionRequest != null) {
					RequestContent requestContent = batchCollectionRequest.getContentByOrderId(orderId);
					cBatchQuerySrv.resetBatchStatus(requestContent, historyLastsQuery);
				}
			} else if (unionpayBatchCollection.allowQuery()) {
				cBatchQuerySrv.batchQuery(unionpayBatchCollection);
			}
		}
		this.unionpayCollection = unionpayCollection.findById(unionpayCollection.getId());
	}

	public void syncSingleOrderStatus(UnionpayCollection unionpayCollection) {
		try {
			UnionpayCollectionQuery query = new UnionpayCollectionQuery();
			query.setOrderId(unionpayCollection.getOrderId());
			List<UnionpayCollectionQuery> queryHistory = query.findUnionpayCollectionQuery();
			if (CollectionUtils.isEmpty(queryHistory)) {
				queryResult(unionpayCollection);
			} else {
				CollectionTrade collectionTrade = CollectionTrade.findByTradeNo(unionpayCollection);
				UnionpayCollectionQuery lastsQuery = queryHistory.get(0);

				String queryRespCode = lastsQuery.getRespCode();
				String origRespCode = lastsQuery.getOrigRespCode();
				String origRespMsg = lastsQuery.getOrigRespMsg();
				String resp = lastsQuery.getResp();
				String queryId = lastsQuery.getQueryId();
				String settleAmt = lastsQuery.getSettleAmt();
				String settleCurrencyCode = lastsQuery.getSettleCurrencyCode();
				String settleDate = lastsQuery.getSettleDate();
				String traceNo = lastsQuery.getTraceNo();
				String traceTime = lastsQuery.getTraceTime();

				boolean isFail = false;
				boolean origRespCodeIsNotBlank = StringUtils.isNotBlank(origRespCode);
				boolean isUnkonwOrigRespCode = "03".equals(origRespCode) || "04".equals(origRespCode)
						|| "05".equals(origRespCode);// 订单处理中或交易状态未明

				// 查询成功
				if ("00".equals(queryRespCode)) {
					Date now = new Date();
					if ("00".equals(origRespCode) || "A6".equals(origRespCode)) {// 成功
						unionpayCollection.setFinalCode("0");
					} else if (origRespCodeIsNotBlank && (!isUnkonwOrigRespCode)) {// 失败
						unionpayCollection.setFinalCode("2");
					}

					unionpayCollection.setResultCode(origRespCode);
					unionpayCollection.setResultMsg(origRespMsg);
					unionpayCollection.setResult(resp);
					unionpayCollection.setQueryId(queryId);
					unionpayCollection.setSettleAmt(settleAmt);
					unionpayCollection.setSettleCurrencyCode(settleCurrencyCode);
					unionpayCollection.setSettleDate(settleDate);
					unionpayCollection.setTraceNo(traceNo);
					unionpayCollection.setTraceTime(traceTime);
					unionpayCollection.setMat(now);
					unionpayCollection.update();
					if (collectionTrade != null) {
						collectionTrade.setFinalCode("0");// 成功
						collectionTrade.setResultCode(origRespCode);
						collectionTrade.setResultMsg(origRespMsg);
						collectionTrade.setMat(now);
						collectionTrade.update();
					}
				} else if ("34".equals(queryRespCode)) {// 订单不存在
					isFail = syncInRespCode34(unionpayCollection, lastsQuery);
				}
				// 订单处理中或交易状态未明
				if (isUnkonwOrigRespCode || ((!"00".equals(queryRespCode)) && !isFail)) {
					queryResult(unionpayCollection);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void queryResult(UnionpayCollection unionpayCollection) throws Exception {
		boolean isSaved = false;
		UnionpayCollectionQuery query = null;
		SendProxy sendProxy = null;
		try {
			query = unionpayCollection.buildQuery();
			if (isSaved = query.save()) {
				unionpayCollection.queryResult();
				sendProxy = query.getSendProxy();
				handlingQueryResult(unionpayCollection, query);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (query != null) {
				AcpResponse acpResponse = sendProxy == null ? null : sendProxy.getAcpResponse();
				if (acpResponse != null) {
					query.setResp(JsonKit.toJson(acpResponse));
				}
				query.setExceInfo(JsonKit.toJson(BaseCollectionRuntimeException.getExceptionInfo(e)));
			}
			throw e;
		} finally {
			if (query != null) {
				if (!isSaved) {
					query.save();
				} else {
					query.setMat(new Date());
					query.update();
				}
			}
		}
	}

	public void handlingQueryResult(UnionpayCollection unionpayCollection, UnionpayCollectionQuery query) {
		query.validateQueryResp();
		query.setFieldFromQueryResp();

		Date now = new Date();
		String respCode = query.getRespCode();
		String origRespCode = query.getOrigRespCode();
		String origRespMsg = query.getOrigRespMsg();
		if (("00").equals(respCode)) {// 如果查询交易成功

			Integer queryCount = unionpayCollection.getQueryResultCount();
			unionpayCollection.setQueryResultCount(queryCount == null ? 1 : queryCount + 1);
			unionpayCollection.setResultCode(origRespCode);
			unionpayCollection.setResultMsg(origRespMsg);
			unionpayCollection.setQueryId(query.getQueryId());
			unionpayCollection.setSettleAmt(query.getSettleAmt());
			unionpayCollection.setSettleCurrencyCode(query.getSettleCurrencyCode());
			unionpayCollection.setSettleDate(query.getSettleDate());
			unionpayCollection.setTraceNo(query.getTraceNo());
			unionpayCollection.setTraceTime(query.getTraceTime());
			unionpayCollection.setResult(query.getResp());
			unionpayCollection.setMat(now);

			CollectionTrade collectionTrade = CollectionTrade.findByTradeNo(unionpayCollection);
			if (collectionTrade != null) {
				collectionTrade.setResultCode(origRespCode);
				collectionTrade.setResultMsg(origRespMsg);
				collectionTrade.setMat(now);
			}

			if ("00".equals(origRespCode) || "A6".equals(origRespCode)) {
				// 交易成功，更新商户订单状态
				unionpayCollection.setFinalCode("0");// 成功
				unionpayCollection.update();
				if (collectionTrade != null) {
					collectionTrade.setFinalCode("0");// 成功
					collectionTrade.update();
				}
			} else if (("03").equals(origRespCode) || ("04").equals(origRespCode) || ("05").equals(origRespCode)) {
				// 订单处理中或交易状态未明，需稍后发起交易状态查询交易 【如果最终尚未确定交易是否成功请以对账文件为准】
				unionpayCollection.update();
				if (collectionTrade != null) {
					collectionTrade.update();
				}
			} else {
				// 其他应答码为交易失败
				unionpayCollection.setFinalCode("2");// 失败
				unionpayCollection.update();
				if (collectionTrade != null) {
					collectionTrade.setFinalCode("2");// 失败
					collectionTrade.update();
				}
			}
		} else if (("34").equals(respCode)) {
			syncInRespCode34(unionpayCollection, query);
		} else {
			// 查询交易本身失败，如应答码10/11检查查询报文是否正确
		}
	}

	private boolean syncInRespCode34(UnionpayCollection unionpayCollection, UnionpayCollectionQuery query) {
		boolean isFail = false;
		try {
			Date now = new Date();
			CollectionTrade collectionTrade = CollectionTrade.findByTradeNo(unionpayCollection);
			int timeoutMinute = unionpayCollection.isBatchTradeOrder() ? UnionpayBatchCollection.TIMEOUT_MINUTE
					: UnionpayCollection.TIMEOUT_MINUTE;
			if (query.isTimeout(timeoutMinute)) {// 订单不存在且超时,视为原交易失败
				unionpayCollection.setFinalCode("2");// 失败
				unionpayCollection.setResultCode(query.getRespCode());
				unionpayCollection.setResultMsg(query.getRespMsg());
				unionpayCollection.setResult(query.getResp());
				unionpayCollection.setMat(now);
				unionpayCollection.update();
				if (collectionTrade != null) {
					collectionTrade.setFinalCode("2");// 失败
					collectionTrade.setResultCode(query.getRespCode());
					collectionTrade.setResultMsg(query.getRespMsg());
					collectionTrade.setMat(now);
					collectionTrade.update();
				}
				isFail = true;
			} else {
				UnionpayCallbackLog lastsCallbackLog = null;
				List<UnionpayCallbackLog> callbackLog = UnionpayCallbackLog
						.findCallbackByOrderId(unionpayCollection.getOrderId());
				if (CollectionUtils.isNotEmpty(callbackLog)) {
					lastsCallbackLog = callbackLog.get(0);
					isFail = UnionpayCollection.isFailCode(lastsCallbackLog.getRespCode());
				} else {
					String respCode = unionpayCollection.getRespCode();
					String resultCode = unionpayCollection.getResultCode();
					isFail = UnionpayCollection.isFailCode(respCode) || UnionpayCollection.isFailCode(resultCode);
				}

				if (isFail) {
					unionpayCollection.setFinalCode("2");// 失败
				}
				if (lastsCallbackLog != null) {
					unionpayCollection.setResultCode(lastsCallbackLog.getRespCode());
					unionpayCollection.setResultMsg(lastsCallbackLog.getRespMsg());
					unionpayCollection.setResult(lastsCallbackLog.getInfo());
				} else {
					unionpayCollection.setResultCode(query.getRespCode());
					unionpayCollection.setResultMsg(query.getRespMsg());
					unionpayCollection.setResult(query.getResp());
				}
				unionpayCollection.setMat(now);
				unionpayCollection.update();

				if (collectionTrade != null) {
					if (isFail) {
						collectionTrade.setFinalCode("2");// 失败
					}
					if (lastsCallbackLog != null) {
						collectionTrade.setResultCode(lastsCallbackLog.getRespCode());
						collectionTrade.setResultMsg(lastsCallbackLog.getRespMsg());
					} else {
						collectionTrade.setResultCode(query.getRespCode());
						collectionTrade.setResultMsg(query.getRespMsg());
					}
					collectionTrade.setMat(now);
					collectionTrade.update();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isFail;
	}

}
