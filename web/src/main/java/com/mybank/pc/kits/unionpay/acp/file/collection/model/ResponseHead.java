package com.mybank.pc.kits.unionpay.acp.file.collection.model;

import java.util.Arrays;

import com.mybank.pc.kits.unionpay.acp.file.BatchTxtLine;
import com.mybank.pc.kits.unionpay.acp.file.UnionPayFileUtils;

/**
 * 結果文件头
 * 
 * @author hkun
 *
 */
public class ResponseHead implements BatchTxtLine {

	public static final String[] TITLES = new String[] { "version", "totalAmount", "totalNumber", "reqReserved1",
			"reqReserved2", "successAmount", "successCount", "unionpayReserved1", "unionpayReserved2" };

	/**
	 * 版本号 R 格式：NS5。取值：5.0.0
	 */
	private String version;

	/**
	 * 总金额 R 格式：N1..12。以分为单位
	 */
	private String totalAmount;

	/**
	 * 总笔数 R 格式：N1..6。单个批次最多支持10万笔
	 */
	private String totalNumber;

	/**
	 * 请求方保留域1 R 格式：ANS1..30。支持汉字
	 */
	private String reqReserved1;

	/**
	 * 请求方保留域2 R 格式：ANS1..30。支持汉字
	 */
	private String reqReserved2;

	/**
	 * 成功金额 M 格式：N1..12。以分为单位
	 */
	private String successAmount;

	/**
	 * 成功笔数 M 格式：N1..6。
	 */
	private String successCount;

	/**
	 * 银联保留域1 O 格式：ANS1..30。支持汉字
	 */
	private String unionpayReserved1;

	/**
	 * 银联保留域2 O 格式：ANS1..30。支持汉字
	 */
	private String unionpayReserved2;

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

	public String getSuccessAmount() {
		return successAmount;
	}

	public void setSuccessAmount(String successAmount) {
		this.successAmount = successAmount;
	}

	public String getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(String successCount) {
		this.successCount = successCount;
	}

	public String getUnionpayReserved1() {
		return unionpayReserved1;
	}

	public void setUnionpayReserved1(String unionpayReserved1) {
		this.unionpayReserved1 = unionpayReserved1;
	}

	public String getUnionpayReserved2() {
		return unionpayReserved2;
	}

	public void setUnionpayReserved2(String unionpayReserved2) {
		this.unionpayReserved2 = unionpayReserved2;
	}

	@Override
	public String getTxtLine() {
		return UnionPayFileUtils.toTxtLine(this, Arrays.asList(TITLES));
	}

}