package com.mybank.pc.collection.model.sender;

import java.util.HashMap;
import java.util.Map;

import com.mybank.pc.collection.model.UnionpayBatchCollectionQuery;
import com.mybank.pc.kits.unionpay.acp.AcpService;
import com.mybank.pc.kits.unionpay.acp.SDKConfig;
import com.mybank.pc.kits.unionpay.acp.SDKConstants;

public class BatchQueryRequestBuilder implements SenderBuilder {

	@Override
	public SendProxy buildTo(BaseUnionpayCollectionTrade unionpayCollectionTrade) {
		UnionpayBatchCollectionQuery unionpayBatchCollectionQuery = (UnionpayBatchCollectionQuery) unionpayCollectionTrade;

		SendProxy sendProxy = new SendProxy(unionpayBatchCollectionQuery.getMerId());
		SDKConfig sdkConfig = sendProxy.getSdkConfig();
		AcpService acpService = sendProxy.getAcpService();

		Map<String, String> contentData = new HashMap<String, String>();
		/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
		// 版本号
		contentData.put("version", unionpayBatchCollectionQuery.getVersion());
		// 字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("encoding", unionpayBatchCollectionQuery.getEncoding());
		// 签名方法 目前只支持01-RSA方式证书加密
		contentData.put("signMethod", sendProxy.getSignMethod());
		// 交易类型
		contentData.put("txnType", unionpayBatchCollectionQuery.getTxnType());
		// 交易子类型
		contentData.put("txnSubType", unionpayBatchCollectionQuery.getTxnSubType());
		// 业务类型
		contentData.put("bizType", unionpayBatchCollectionQuery.getBizType());
		// 渠道类型
		contentData.put("channelType", unionpayBatchCollectionQuery.getChannelType());

		/*** 商户接入参数 ***/
		// 接入类型，商户接入固定填0，不需修改
		contentData.put("accessType", unionpayBatchCollectionQuery.getAccessType());
		// 商户号码
		contentData.put("merId", unionpayBatchCollectionQuery.getMerId());

		/** 与批量查询相关的参数 **/
		// 被查询批量交易批次号
		contentData.put("batchNo", unionpayBatchCollectionQuery.getBatchNo());
		// 原批量代收请求的交易时间
		contentData.put("txnTime", unionpayBatchCollectionQuery.getTxnTime());
		// 请求方保留域
		contentData.put("reqReserved", unionpayBatchCollectionQuery.getReqReserved());

		sendProxy.setReqData(acpService.sign(contentData, SDKConstants.UTF_8_ENCODING));
		// 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的acpsdk.backTransUrl
		sendProxy.setReqUrl(sdkConfig.getBatchTransUrl());

		unionpayBatchCollectionQuery.setSendProxy(sendProxy);
		return sendProxy;
	}

}
