package com.mybank.pc.collection.trade;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.mybank.pc.collection.model.CollectionTrade;
import com.mybank.pc.collection.model.UnionpayBatchCollection;
import com.mybank.pc.collection.model.UnionpayBatchCollectionBatchno;
import com.mybank.pc.collection.model.UnionpayCollection;
import com.mybank.pc.collection.model.sender.SendProxy;
import com.mybank.pc.exception.ValidateUnionpayRespException;
import com.mybank.pc.kits.unionpay.acp.AcpResponse;
import com.mybank.pc.kits.unionpay.acp.SDK;

public class CBatchTradeSrv {

	public void sendBatchOrder() {
		String batch_ch_merId = SDK.getSDK(SDK.MER_CODE_BATCH_CH).getMerId();
		sendBatchOrderByMerId(batch_ch_merId);
		String test_merId = SDK.getSDK(SDK.MER_CODE_TEST).getMerId();
		sendBatchOrderByMerId(test_merId);
	}

	public void sendBatchOrderByMerId(String merId) {
		String txnTime = null;
		String nextBatchNo = null;
		UnionpayBatchCollection unionpayBatchCollection = null;

		int count = 0;
		boolean isSaved = false;
		List<UnionpayCollection> toBeSentOrder = null;
		SendProxy sendProxy = null;
		Kv kv = Kv.create();
		Date now = new Date();
		try {
			txnTime = new SimpleDateFormat("yyyyMMddHHmmss").format(now);
			nextBatchNo = UnionpayBatchCollectionBatchno.getNextBatchNoToString(txnTime, merId);

			kv.set("batchNo", nextBatchNo).set("txnTime", txnTime).set("merId", merId).set("mat", now);
			if ((count = UnionpayCollection.updateToBeSentUnionpayCollectionBatchNo(kv)) > 0) {
				toBeSentOrder = UnionpayCollection.findToBeSentUnionpayCollectionByBatchNo(kv);
				unionpayBatchCollection = UnionpayBatchCollection.buildUnionpayBatchCollection(now, txnTime,
						nextBatchNo, merId, toBeSentOrder);
				unionpayBatchCollection.assemblyBatchRequest();

				if (isSaved = unionpayBatchCollection.save()) {
					sendProxy = unionpayBatchCollection.sendBatchOrder();
					handlingBatchTradeResult(unionpayBatchCollection, toBeSentOrder);
				}
			}
		} catch (ValidateUnionpayRespException vure) {// 校验响应信息失败，需后续处理
			vure.printStackTrace();
			if (unionpayBatchCollection != null && isSaved) {
				String result = JsonKit.toJson(sendProxy.getAcpResponse());
				String exceInfo = JsonKit.toJson(vure.getExceptionInfo());

				unionpayBatchCollection.setResp(result);
				unionpayBatchCollection.setExceInfo(exceInfo);
				unionpayBatchCollection.setMat(now);

				if (sendProxy.isInvalidRequestOrURI()) {
					unionpayBatchCollection.setFinalCode("2");// 失败
					unionpayBatchCollection.update();
					for (UnionpayCollection unionpayCollection : toBeSentOrder) {
						try {
							unionpayCollection.resetBatchStatus();
							unionpayCollection.setResult(result);
							unionpayCollection.setExceInfo(exceInfo);
							unionpayCollection.setMat(now);
							unionpayCollection.update();
						} catch (Exception uce) {
							uce.printStackTrace();
						}
					}
				} else {
					unionpayBatchCollection.setFinalCode("4");// 状态未明 需后续处理
					unionpayBatchCollection.update();
					for (UnionpayCollection unionpayCollection : toBeSentOrder) {
						try {
							unionpayCollection.setFinalCode("3");// 状态未明 需后续处理
							unionpayCollection.setResult(result);
							unionpayCollection.setExceInfo(exceInfo);
							unionpayCollection.setMat(now);
							unionpayCollection.update();
						} catch (Exception uce) {
							uce.printStackTrace();
						}
					}
					Integer batchId = unionpayBatchCollection.getId();
					SyncStatusExecutor.scheduleNotClearBatchQuery(batchId == null ? null : String.valueOf(batchId),
							merId, unionpayBatchCollection.getBatchNo(), txnTime, 60, TimeUnit.SECONDS);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (count <= 0 || (!isSaved) && StringUtils.isNotBlank(txnTime) && StringUtils.isNotBlank(nextBatchNo)
						&& nextBatchNo.length() == 4) {
					UnionpayBatchCollectionBatchno.decrementBatchNo(txnTime, merId, nextBatchNo);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			try {
				if (unionpayBatchCollection != null && StringUtils.isBlank(unionpayBatchCollection.getResp())) {
					AcpResponse acpResponse = sendProxy == null ? null : sendProxy.getAcpResponse();
					if (acpResponse != null) {
						unionpayBatchCollection.setResp(JsonKit.toJson(acpResponse));
						unionpayBatchCollection.update();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (count > 0 && CollectionUtils.isEmpty(toBeSentOrder)) {
					toBeSentOrder = UnionpayCollection.findToBeSentUnionpayCollectionByBatchNo(kv);
				}
				if (CollectionUtils.isNotEmpty(toBeSentOrder)) {
					String respCode = sendProxy == null ? null : sendProxy.getRespCode();
					// 没有成功也没有最终失败需要更新订单状态为待发送
					if ((!isSaved) || (!isSuccessed(respCode) && !isFailed(respCode))) {
						String status = null;
						for (UnionpayCollection unionpayCollection : toBeSentOrder) {
							status = unionpayCollection.getStatus();
							// 已确认状态的订单
							if ("1".equals(status)) {
								try {
									unionpayCollection.resetBatchStatus();
									unionpayCollection.setMat(now);
									unionpayCollection.update();
								} catch (Exception uce) {
									uce.printStackTrace();
								}
							}
						}
					}
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param rspData
	 * @param acpService
	 * @param encoding
	 * @param unionpayBatchCollection
	 * @return
	 */
	private boolean handlingBatchTradeResult(UnionpayBatchCollection unionpayBatchCollection,
			List<UnionpayCollection> toBeSentOrder) {
		boolean isSuccess = false;
		String unionpayBatchCollectionId = unionpayBatchCollection.getId() == null ? ""
				: unionpayBatchCollection.getId().toString();
		try {
			unionpayBatchCollection.validateBatchOrderResp();
			SendProxy sendProxy = unionpayBatchCollection.getSendProxy();
			String respCode = sendProxy.getRespCode();
			String respMsg = sendProxy.getRespMsg();

			Date now = new Date();

			unionpayBatchCollection.setRespCode(respCode);
			unionpayBatchCollection.setRespMsg(respMsg);
			unionpayBatchCollection.setMat(now);

			// 00：交易已受理
			// 其他：03：交易通讯超时，请发起查询交易 04：交易状态未明，请查询对账结果 05：交易已受理，请稍后查询交易结果
			// 都需发起交易批量状态查询交易确定交易状态
			if (isSuccessed(respCode)) {
				if (CollectionUtils.isNotEmpty(toBeSentOrder)) {
					for (UnionpayCollection unionpayCollection : toBeSentOrder) {
						try {
							unionpayCollection.setRespCode(respCode);
							unionpayCollection.setRespMsg(respMsg);
							unionpayCollection.setBatchId(unionpayBatchCollectionId);
							unionpayCollection.setMat(now);
							updateOrderStatusInSuccess(unionpayCollection);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				isSuccess = true;
				unionpayBatchCollection.setFinalCode("1");// 处理中
				unionpayBatchCollection.setNextAllowQueryDate();
			} else {
				unionpayBatchCollection.setFinalCode("2");// 失败
				isSuccess = false;

				// 10-29 有关商户端上送报文格式检查导致的错误，不认为与批量请求相关的交易订单的最终处理结果为失败，需后续处理
				// 其他的应答码则认为与批量请求相关的交易订单的最终处理结果为失败
				boolean isFailed = isFailed(respCode);
				if (CollectionUtils.isNotEmpty(toBeSentOrder)) {
					for (UnionpayCollection unionpayCollection : toBeSentOrder) {
						try {
							unionpayCollection.setRespCode(respCode);
							unionpayCollection.setRespMsg(respMsg);
							unionpayCollection.setBatchId(unionpayBatchCollectionId);
							unionpayCollection.setMat(now);
							updateOrderStatusInNotSuccess(unionpayCollection, isFailed);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			unionpayBatchCollection.update();

			return isSuccess;
		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;
			throw e;
		}
	}

	public static boolean isSuccessed(String respCode) {
		// 00：交易已受理
		// 其他：03：交易通讯超时，请发起查询交易 04：交易状态未明，请查询对账结果 05：交易已受理，请稍后查询交易结果
		return ("00".equals(respCode) || "03".equals(respCode) || "04".equals(respCode) || "05".equals(respCode));
	}

	/**
	 * 10-29 有关商户端上送报文格式检查导致的错误，不认为与批量请求相关的交易订单的最终处理结果为失败，需后续处理<br>
	 * 其他的应答码则认为与批量请求相关的交易订单的最终处理结果为失败
	 * 
	 * @param respCode
	 * @return
	 */
	public static boolean isFailed(String respCode) {
		// 10：报文格式错误 11：验证签名失败 12：重复交易 13：报文交易要素缺失 14：批量文件格式错误
		return StringUtils.isNotBlank(respCode) && !("10".equals(respCode) || "11".equals(respCode)
				|| "12".equals(respCode) || "13".equals(respCode) || "14".equals(respCode));
	}

	public void updateOrderStatusInSuccess(UnionpayCollection unionpayCollection) {
		unionpayCollection.setStatus("2");// 已发送
		unionpayCollection.update();

		SqlPara sqlPara = Db.getSqlPara("collection_trade.findByTradeNo", unionpayCollection);
		CollectionTrade collectionTrade = CollectionTrade.dao.findFirst(sqlPara);
		if (collectionTrade != null) {
			collectionTrade.setResCode(unionpayCollection.getRespCode());
			collectionTrade.setResMsg(unionpayCollection.getRespMsg());

			collectionTrade.setMat(unionpayCollection.getMat());
			collectionTrade.update();
		}
	}

	public void updateOrderStatusInNotSuccess(UnionpayCollection unionpayCollection, boolean isFailed) {
		CollectionTrade collectionTrade = CollectionTrade.findByTradeNo(unionpayCollection);
		if (collectionTrade != null) {
			collectionTrade.setResCode(unionpayCollection.getRespCode());
			collectionTrade.setResMsg(unionpayCollection.getRespMsg());
			collectionTrade.setMat(unionpayCollection.getMat());
		}

		if (isFailed) {
			unionpayCollection.setFinalCode("2");// 失败
			unionpayCollection.update();
			if (collectionTrade != null) {
				collectionTrade.setFinalCode("2");// 失败
			}
		} else {
			unionpayCollection.resetBatchStatus();
			unionpayCollection.update();
		}

		if (collectionTrade != null) {
			collectionTrade.update();
		}
	}

}
