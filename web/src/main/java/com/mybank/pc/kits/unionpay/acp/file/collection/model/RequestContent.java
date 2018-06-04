package com.mybank.pc.kits.unionpay.acp.file.collection.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mybank.pc.kits.unionpay.acp.file.BatchTxtLine;
import com.mybank.pc.kits.unionpay.acp.file.UnionPayFileUtils;

/**
 * 请求文件体
 * 
 * @author hkun
 *
 */
public class RequestContent implements BatchTxtLine {

	/**
	 * 商户代码 C 格式：AN15。机构接入必填；商户接入可选。
	 */
	private String merId;
	/**
	 * 商户订单号 M 格式：AN8..32。单个批次文件内不能 重复。
	 */
	private String orderId;
	/**
	 * 交易币种 C 格式：N3。境外交易必填；境内交易可选，如不填写默认为 15
	 */
	private String currencyCode;
	/**
	 * 交易金额 M 格式：N12。以分为单位。
	 */
	private String txnAmt;
	/**
	 * 账号类型 M 格式：N2。<br>
	 * 
	 * <pre>
	 * 01 银行卡
	 * 02 存折
	 * 04 对公账户
	 * </pre>
	 */
	private String accType;
	/**
	 * 账号 M 格式：N1..50。银行卡号或存折号码
	 */
	private String accNo;
	/**
	 * 姓名 M 格式：ANS1..30。支持汉字。
	 */
	private String customerNm;
	/**
	 * 产品类型 O 格式：N6。<br>
	 * 
	 * <pre>
	 * 000000 默认取值
	 * 000401 代付
	 * 000501 代收
	 * 000503 委托代收
	 * </pre>
	 */
	private String bizType;
	/**
	 * 证件类型 O 格式：N2。
	 * 
	 * <pre>
	 * 01 身份证
	 * 02 军官证
	 * 03 护照
	 * 04 回乡证
	 * 05 台胞证
	 * 06 警官证
	 * 07 士兵证
	 * 99 其他证件
	 * </pre>
	 */
	private String certifTp;
	/**
	 * 证件号码 O 格式：ANS1..20。
	 */
	private String certifId;
	/**
	 * 手机号码 O 格式：N1..20。
	 */
	private String phoneNo;
	/**
	 * CVN2 O 格式：N3。
	 */
	private String cvn2;
	/**
	 * 有效期 O 格式：YYMM。
	 */
	private String expired;
	/**
	 * 开户行代码 C 格式：AN8..11。账号类型为02时必填。
	 */
	private String issInsCode;
	/**
	 * 开户行名称 O 格式：ANS1..30。
	 */
	private String issInsName;
	/**
	 * 账单类型 O 格式：ANS2。
	 */
	private String billType;
	/**
	 * 账单号码 O 格式：AN1..64 。
	 */
	private String billNo;
	/**
	 * 附言 O 格式：ANS1..40。支持汉字
	 */
	private String postscript;
	/**
	 * 签约协议号 O 格式：ANS60 自联农行通道上送
	 */
	private String contractAgrNo;
	/**
	 * 请求方保留域1 O 格式：ANS1..30。支持汉字
	 */
	private String reqReserved1;
	/**
	 * 请求方保留域2 O 格式：ANS1..30。支持汉字
	 */
	private String reqReserved2;
	/**
	 * 单位结算卡完整账户名称 C 格式：ANS1..120 支持汉字 accType=04 时上送
	 */
	private String comDebitCardAccName;
	/**
	 * 营业执照注册号 C 格式：ANS1..20 支持汉字 accType=04 时上送
	 */
	private String businessLicenseRegNo;

	private List<String> titles;

	public RequestContent() {
		this.titles = new ArrayList<String>(Arrays.asList(BatchCollectionRequest.DEFAULT_TITLES));
	}

	public RequestContent(List<String> titles) {
		this.titles = titles;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getTxnAmt() {
		return txnAmt;
	}

	public void setTxnAmt(String txnAmt) {
		this.txnAmt = txnAmt;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getCustomerNm() {
		return customerNm;
	}

	public void setCustomerNm(String customerNm) {
		this.customerNm = customerNm;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getCertifTp() {
		return certifTp;
	}

	public void setCertifTp(String certifTp) {
		this.certifTp = certifTp;
	}

	public String getCertifId() {
		return certifId;
	}

	public void setCertifId(String certifId) {
		this.certifId = certifId;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getCvn2() {
		return cvn2;
	}

	public void setCvn2(String cvn2) {
		this.cvn2 = cvn2;
	}

	public String getExpired() {
		return expired;
	}

	public void setExpired(String expired) {
		this.expired = expired;
	}

	public String getIssInsCode() {
		return issInsCode;
	}

	public void setIssInsCode(String issInsCode) {
		this.issInsCode = issInsCode;
	}

	public String getIssInsName() {
		return issInsName;
	}

	public void setIssInsName(String issInsName) {
		this.issInsName = issInsName;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getPostscript() {
		return postscript;
	}

	public void setPostscript(String postscript) {
		this.postscript = postscript;
	}

	public String getContractAgrNo() {
		return contractAgrNo;
	}

	public void setContractAgrNo(String contractAgrNo) {
		this.contractAgrNo = contractAgrNo;
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

	public String getComDebitCardAccName() {
		return comDebitCardAccName;
	}

	public void setComDebitCardAccName(String comDebitCardAccName) {
		this.comDebitCardAccName = comDebitCardAccName;
	}

	public String getBusinessLicenseRegNo() {
		return businessLicenseRegNo;
	}

	public void setBusinessLicenseRegNo(String businessLicenseRegNo) {
		this.businessLicenseRegNo = businessLicenseRegNo;
	}

	@Override
	public String getTxtLine() {
		return UnionPayFileUtils.toTxtLine(this, titles);
	}

}
