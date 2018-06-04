package com.mybank.pc.kits.unionpay.acp.file.collection.model;

import java.util.List;

import com.mybank.pc.kits.unionpay.acp.file.BatchTxtLine;
import com.mybank.pc.kits.unionpay.acp.file.UnionPayFileUtils;

/**
 * 结果文件体
 * 
 * @author hkun
 *
 */
public class ResponseContent implements BatchTxtLine {

	/**
	 * 应答码 M 格式：N2
	 */
	private String respCode;

	/**
	 * 应答码描述 M 格式：ANS1..512
	 */
	private String respMsg;

	/**
	 * 交易查询流水号 C 格式：N21。<br>
	 * 成功交易必返；部分失败交易（如路由失败或权限校验失败等） 为空。用于联机交易状态查询。
	 */
	private String queryId;

	/**
	 * 系统跟踪号 C 格式：N6。<br>
	 * 成功交易必返；部分失败交 易（如路由失败或权限校验失败等）为空。
	 */
	private String sysTraNo;

	/**
	 * 系统交易时间 C 格式：MMDDhhmmss。<br>
	 * 成功交易必返；部分失败交易（如路由失败或权限校验失 败等）为空。
	 */
	private String sysTm;

	/**
	 * 清算日期 C 格式：MMDD
	 */
	private String settleDate;

	/**
	 * 银联保留域1 O 格式：ANS1..30。支持汉字
	 */
	private String reserved1;

	/**
	 * 银联保留域2 O 格式：ANS1..30。支持汉字
	 */
	private String reserved2;

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

	public ResponseContent() {

	}

	public ResponseContent(List<String> titles) {
		this();
		this.titles = titles;
	}

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

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public String getSysTraNo() {
		return sysTraNo;
	}

	public void setSysTraNo(String sysTraNo) {
		this.sysTraNo = sysTraNo;
	}

	public String getSysTm() {
		return sysTm;
	}

	public void setSysTm(String sysTm) {
		this.sysTm = sysTm;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public String getReserved1() {
		return reserved1;
	}

	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}

	public String getReserved2() {
		return reserved2;
	}

	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
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

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	@Override
	public String getTxtLine() {
		return UnionPayFileUtils.toTxtLine(this, titles);
	}

}
