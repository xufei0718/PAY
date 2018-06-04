package com.mybank.pc.collection.model.sender;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.LogKit;
import com.mybank.pc.exception.ValidateUnionpayRespException;
import com.mybank.pc.kits.unionpay.acp.AcpResponse;
import com.mybank.pc.kits.unionpay.acp.AcpService;
import com.mybank.pc.kits.unionpay.acp.SDK;
import com.mybank.pc.kits.unionpay.acp.SDKConfig;
import com.mybank.pc.kits.unionpay.acp.SDKConstants;

public class SendProxy {

	private String merId;

	private SDK sdk;

	private SDKConfig sdkConfig;

	private AcpService acpService;

	private String reqUrl;

	private String encoding;

	private String signMethod;

	private Map<String, String> reqData = null;

	private AcpResponse acpResponse = null;

	private int status;

	private Map<String, String> rspData = null;

	private String result;

	private String respCode;

	private String respMsg;

	public SendProxy(String merId) {
		this.merId = merId;

		this.sdk = SDK.getByMerId(merId);
		this.sdkConfig = sdk.getSdkConfig();
		this.acpService = sdk.getAcpService();
		this.signMethod = sdkConfig.getSignMethod();
	}

	public SendProxy send() throws Exception {
		// 发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;
		// 这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		this.acpResponse = acpService.post(reqData, reqUrl,
				this.encoding == null ? SDKConstants.UTF_8_ENCODING : encoding);
		this.status = this.acpResponse.getStatus();
		this.rspData = this.acpResponse.getRspData();
		this.result = this.acpResponse.getResult();
		return this;
	}

	public boolean validateResp() throws ValidateUnionpayRespException {
		try {
			if (StringUtils.isBlank(encoding)) {
				encoding = SDKConstants.UTF_8_ENCODING;
			}
			boolean isEmpty = rspData.isEmpty();
			boolean isValidate = acpService.validate(rspData, encoding);

			// 未返回正确的http状态
			if (isEmpty) {
				LogKit.error("未获取到返回报文或返回http状态码非200");
				throw new RuntimeException("未获取到返回报文或返回http状态码非200");
			}
			if (isValidate) {
				LogKit.info("验证签名成功");
			} else {
				LogKit.error("验证签名失败");
				throw new RuntimeException("验证签名失败");
			}

			return isValidate;
		} catch (Exception e) {
			ValidateUnionpayRespException vure = new ValidateUnionpayRespException(e);
			vure.setSendProxy(this);
			throw vure;
		}
	}

	/**
	 * 版本号，交易类型、子类，签名方法，签名值等关键域未上送，返回“Invalid request.”<br>
	 * 交易类型和请求地址校验有误，返回“Invalid request URI.”
	 * 
	 * @return
	 */
	public boolean isInvalidRequestOrURI() {
		return result != null && (result.contains("Invalid request.") || result.contains("Invalid request URI."));
	}

	public String decryptData(String base64EncryptedInfo) {
		if (StringUtils.isBlank(base64EncryptedInfo)) {
			return base64EncryptedInfo;
		} else {
			if (StringUtils.isBlank(encoding)) {
				encoding = SDKConstants.UTF_8_ENCODING;
			}
			return acpService.decryptData(base64EncryptedInfo, encoding);
		}
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public SDK getSdk() {
		return sdk;
	}

	public void setSdk(SDK sdk) {
		this.sdk = sdk;
	}

	public SDKConfig getSdkConfig() {
		return sdkConfig;
	}

	public void setSdkConfig(SDKConfig sdkConfig) {
		this.sdkConfig = sdkConfig;
	}

	public AcpService getAcpService() {
		return acpService;
	}

	public void setAcpService(AcpService acpService) {
		this.acpService = acpService;
	}

	public String getReqUrl() {
		return reqUrl;
	}

	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Map<String, String> getReqData() {
		return reqData;
	}

	public void setReqData(Map<String, String> reqData) {
		this.reqData = reqData;
	}

	public AcpResponse getAcpResponse() {
		return acpResponse;
	}

	public void setAcpResponse(AcpResponse acpResponse) {
		this.acpResponse = acpResponse;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Map<String, String> getRspData() {
		return rspData;
	}

	public void setRspData(Map<String, String> rspData) {
		this.rspData = rspData;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getSignMethod() {
		return signMethod;
	}

	public void setSignMethod(String signMethod) {
		this.signMethod = signMethod;
	}

	public String getRespCode() {
		if (StringUtils.isBlank(this.respCode)) {
			if (this.rspData != null) {
				return (this.respCode = this.rspData.get("respCode"));
			}
		} else {
			return this.respCode;
		}
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespMsg() {
		if (StringUtils.isBlank(this.respMsg)) {
			if (this.rspData != null) {
				return (this.respMsg = this.rspData.get("respMsg"));
			}
		} else {
			return this.respMsg;
		}
		return respMsg;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

}