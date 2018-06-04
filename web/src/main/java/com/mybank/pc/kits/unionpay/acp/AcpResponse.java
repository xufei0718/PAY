package com.mybank.pc.kits.unionpay.acp;

import java.util.Map;

public class AcpResponse {

	private int status;

	private Map<String, String> rspData;

	private String result;

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

}
