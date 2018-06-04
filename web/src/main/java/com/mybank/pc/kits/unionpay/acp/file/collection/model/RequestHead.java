package com.mybank.pc.kits.unionpay.acp.file.collection.model;

import java.util.Arrays;

import com.mybank.pc.kits.unionpay.acp.file.BatchTxtLine;
import com.mybank.pc.kits.unionpay.acp.file.UnionPayFileUtils;

/**
 * 请求文件头
 * 
 * @author hkun
 *
 */
public class RequestHead implements BatchTxtLine {

	public static final String[] TITLES = new String[] { "version", "totalAmount", "totalNumber", "reqReserved1",
			"reqReserved2" };

	/**
	 * 版本号 M 格式：NS5。取值：5.0.0
	 */
	private String version = "5.0.0";

	/**
	 * 总金额 M 格式：N1..12。以分为单位
	 */
	private String totalAmount;

	/**
	 * 总笔数 M 格式：N1..6。单个批次最多支持10万笔
	 */
	private String totalNumber;

	/**
	 * 请求方保留域1 O 格式：ANS1..30。支持汉字
	 */
	private String reqReserved1;

	/**
	 * 请求方保留域2 O 格式：ANS1..30。支持汉字
	 */
	private String reqReserved2;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}

	public String getReqReserved1() {
		return reqReserved1;
	}

	public void setReqReserved1(String reqReserved1) {
		this.reqReserved1 = reqReserved1;
	}

	public String getReqReserved2() {
		return reqReserved2;
	}

	public void setReqReserved2(String reqReserved2) {
		this.reqReserved2 = reqReserved2;
	}

	@Override
	public String getTxtLine() {
		return UnionPayFileUtils.toTxtLine(this, Arrays.asList(TITLES));
	}

}