package com.mybank.pc.collection.model.sender;

import java.util.HashMap;
import java.util.Map;

import com.mybank.pc.collection.model.UnionpayCollection;
import com.mybank.pc.kits.unionpay.acp.AcpService;
import com.mybank.pc.kits.unionpay.acp.SDK;
import com.mybank.pc.kits.unionpay.acp.SDKConfig;
import com.mybank.pc.kits.unionpay.acp.SDKConstants;

public class RealtimeRequestBuilder implements SenderBuilder {

	@Override
	public SendProxy buildTo(BaseUnionpayCollectionTrade unionpayCollectionTrade) {
		UnionpayCollection unionpayCollection = (UnionpayCollection) unionpayCollectionTrade;

		SendProxy sendProxy = new SendProxy(unionpayCollection.getMerId());
		SDK sdk = sendProxy.getSdk();
		SDKConfig sdkConfig = sendProxy.getSdkConfig();
		AcpService acpService = sendProxy.getAcpService();

		Map<String, String> contentData = new HashMap<String, String>();
		/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
		// 版本号
		contentData.put("version", unionpayCollection.getVersion());
		// 字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("encoding", unionpayCollection.getEncoding());
		// 签名方法 目前只支持01-RSA方式证书加密
		contentData.put("signMethod", sendProxy.getSignMethod());
		// 交易类型
		contentData.put("txnType", unionpayCollection.getTxnType());
		// 交易子类型
		contentData.put("txnSubType", unionpayCollection.getTxnSubType());
		// 业务类型
		contentData.put("bizType", unionpayCollection.getBizType());
		// 渠道类型
		contentData.put("channelType", unionpayCollection.getChannelType());

		/*** 商户接入参数 ***/
		// 商户号码
		contentData.put("merId", unionpayCollection.getMerId());
		// 接入类型，商户接入固定填0，不需修改
		contentData.put("accessType", unionpayCollection.getAccessType());
		// 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
		contentData.put("orderId", unionpayCollection.getOrderId());
		// 订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		contentData.put("txnTime", unionpayCollection.getTxnTime());
		// 交易币种（境内商户一般是156人民币）
		contentData.put("currencyCode", unionpayCollection.getCurrencyCode());
		// 交易金额，单位分，不要带小数点
		contentData.put("txnAmt", unionpayCollection.getTxnAmt());
		// 账号类型
		contentData.put("accType", unionpayCollection.getAccType());

		///////// 不对敏感信息加密使用：
		// contentData.put("accNo",accNo); //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		////////

		////////// 如果商户号开通了 商户对敏感信息加密的权限那么，需要对
		////////// 卡号accNo，pin和phoneNo，cvn2，expired加密（如果这些上送的话），对敏感信息加密使用：
		contentData.put("encryptCertId", acpService.getEncryptCertId());
		String accNoEnc = acpService.encryptData(unionpayCollection.getAccNo(), SDKConstants.UTF_8_ENCODING);
		contentData.put("accNo", accNoEnc);
		//////////

		if (sdk == SDK.REALTIME_YS_2_SDK || sdk == SDK.REALTIME_YS_4_SDK) {
			Map<String, String> customerInfoMap = new HashMap<String, String>();
			// 【代收的customerInfo送什么验证要素是配置到银联后台到商户号上的，这些验证要素可以在商户的《全渠道入网申请表》中找到，也可以请咨询您的业务人员或者银联业务运营接口人】
			// 以下上送要素是参考《测试商户号777290058110097代收、实名认证交易必送验证要素配置说明.txt》贷记卡（实名认证交易-后台）部分
			customerInfoMap.put("certifTp", unionpayCollection.getCertifTp()); // 证件类型
			customerInfoMap.put("certifId", unionpayCollection.getCertifId()); // 证件号码
			customerInfoMap.put("customerNm", unionpayCollection.getCustomerNm()); // 姓名
			customerInfoMap.put("phoneNo", unionpayCollection.getPhoneNo()); // 手机号
			customerInfoMap.put("cvn2", unionpayCollection.getCvn2()); // 卡背面的cvn2三位数字
			customerInfoMap.put("expired", unionpayCollection.getExpired()); // 有效期
																				// 年在前月在后
			String customerInfoStr = acpService.getCustomerInfoWithEncrypt(customerInfoMap,
					unionpayCollection.getAccNo(), SDKConstants.UTF_8_ENCODING);
			//////////
			contentData.put("customerInfo", customerInfoStr);
		}
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
		contentData.put("reqReserved", unionpayCollection.getReqReserved());

		sendProxy.setReqData(acpService.sign(contentData, SDKConstants.UTF_8_ENCODING));
		// 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的acpsdk.backTransUrl
		sendProxy.setReqUrl(sdkConfig.getBackRequestUrl());

		unionpayCollection.setSendProxy(sendProxy);
		return sendProxy;
	}

}
