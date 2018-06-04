package com.mybank.pc.kits.unionpay.acp.file.collection.model;

/**
 * 拒绝文件体
 * 
 * @author hkun
 *
 */
public class RejectContent {

	public static final String[] TITLES = new String[] { "respCode", "respMsg" };

	/**
	 * 应答码 M 格式：N2
	 */
	private String respCode;

	/**
	 * 应答码描述 M 格式：ANS1..512
	 */
	private String respMsg;

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespMsg() {
		return respMsg;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

}
