package com.mybank.pc.collection.model.sender;

import java.util.HashMap;
import java.util.Map;

import com.mybank.pc.collection.model.UnionpayBatchCollection;
import com.mybank.pc.kits.unionpay.acp.AcpService;
import com.mybank.pc.kits.unionpay.acp.SDKConfig;
import com.mybank.pc.kits.unionpay.acp.SDKConstants;

public class BatchRequestBuilder implements SenderBuilder {

	@Override
	public SendProxy buildTo(BaseUnionpayCollectionTrade unionpayCollectionTrade) {
		UnionpayBatchCollection unionpayBatchCollection = (UnionpayBatchCollection) unionpayCollectionTrade;

		SendProxy sendProxy = new SendProxy(unionpayBatchCollection.getMerId());
		SDKConfig sdkConfig = sendProxy.getSdkConfig();
		AcpService acpService = sendProxy.getAcpService();

		Map<String, String> contentData = new HashMap<String, String>();
		/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
		// 版本号
		contentData.put("version", unionpayBatchCollection.getVersion());
		// 字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("encoding", unionpayBatchCollection.getEncoding());
		// 签名方法 目前只支持01-RSA方式证书加密
		contentData.put("signMethod", sendProxy.getSignMethod());
		// 交易类型
		contentData.put("txnType", unionpayBatchCollection.getTxnType());
		// 交易子类型
		contentData.put("txnSubType", unionpayBatchCollection.getTxnSubType());
		// 业务类型
		contentData.put("bizType", unionpayBatchCollection.getBizType());
		// 渠道类型
		contentData.put("channelType", unionpayBatchCollection.getChannelType());

		/*** 商户接入参数 ***/
		// 接入类型，商户接入固定填0，不需修改
		contentData.put("accessType", unionpayBatchCollection.getAccessType());
		// 商户号码
		contentData.put("merId", unionpayBatchCollection.getMerId());

		/** 与批量文件内容相关的参数 **/
		// 批量交易时填写，当天唯一,0001-9999，商户号+批次号+上交易时间确定一笔交易
		contentData.put("batchNo", unionpayBatchCollection.getBatchNo());
		// 前8位需与文件中的委托日期保持一致
		contentData.put("txnTime", unionpayBatchCollection.getTxnTime());
		// 批量交易时填写，填写批量文件中总的交易比数
		contentData.put("totalQty", unionpayBatchCollection.getTotalQty());
		// 批量交易时填写，填写批量文件中总的交易金额
		contentData.put("totalAmt", unionpayBatchCollection.getTotalAmt());

		// 使用DEFLATE压缩算法压缩后，Base64编码的方式传输经压缩编码的文件内容，文件中的商户号必须与merId一致
		// 示例文件位置在src/assets下
		contentData.put("fileContent", AcpService.enCodeFileContentByString(
				unionpayBatchCollection.getRequestFileContent(), SDKConstants.UTF_8_ENCODING));
		// 后台通知地址（需设置为【外网】能访问 http
		// https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，失败的交易银联不会发送后台通知
		// 后台通知参数详见open.unionpay.com帮助中心 下载 产品接口规范 代收产品接口规范 代收交易 商户通知
		// 注意:1.需设置为外网能访问，否则收不到通知 2.http https均可
		// 3.收单后台通知后需要10秒内返回http200或302状态码
		// 4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200，那么银联会间隔一段时间再次发送。总共发送5次，每次的间隔时间为0,1,2,4分钟。
		// 5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d
		// 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
		contentData.put("backUrl", sdkConfig.getBackUrl());
		// 请求方保留域
		contentData.put("reqReserved", unionpayBatchCollection.getReqReserved());

		// 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		sendProxy.setReqData(acpService.sign(contentData, SDKConstants.UTF_8_ENCODING));
		// 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的acpsdk.batchTransUrl
		sendProxy.setReqUrl(sdkConfig.getBatchTransUrl());

		unionpayBatchCollection.setSendProxy(sendProxy);
		return sendProxy;
	}

}
