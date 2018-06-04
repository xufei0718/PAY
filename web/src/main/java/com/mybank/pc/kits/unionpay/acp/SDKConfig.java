/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 * 
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 * 
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28       MPI基本参数工具类
 * =============================================================================
 */
package com.mybank.pc.kits.unionpay.acp;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

import java.io.File;

/**
 * 
 * @ClassName SDKConfig
 * @Description acpsdk配置文件acp_sdk.properties配置信息类
 * @date 2016-7-22 下午4:04:55
 *       声明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码，性能，规范性等方面的保障<br>
 */
public class SDKConfig {
	/** 前台请求URL. */
	private String frontRequestUrl;
	/** 后台请求URL. */
	private String backRequestUrl;
	/** 单笔查询 */
	private String singleQueryUrl;
	/** 批量查询 */
	private String batchQueryUrl;
	/** 批量交易 */
	private String batchTransUrl;
	/** 文件传输 */
	private String fileTransUrl;
	/** 签名证书路径. */
	private String signCertPath;
	/** 签名证书密码. */
	private String signCertPwd;
	/** 签名证书类型. */
	private String signCertType;
	/** 加密公钥证书路径. */
	private String encryptCertPath;
	/** 验证签名公钥证书目录. */
	private String validateCertDir;
	/** 按照商户代码读取指定签名证书目录. */
	private String signCertDir;
	/** 磁道加密证书路径. */
	private String encryptTrackCertPath;
	/** 磁道加密公钥模数. */
	private String encryptTrackKeyModulus;
	/** 磁道加密公钥指数. */
	private String encryptTrackKeyExponent;
	/** 有卡交易. */
	private String cardRequestUrl;
	/** app交易 */
	private String appRequestUrl;
	/** 证书使用模式(单证书/多证书) */
	private String singleMode;
	/** 安全密钥(SHA256和SM3计算时使用) */
	private String secureKey;
	/** 中级证书路径 */
	private String middleCertPath;
	/** 根证书路径 */
	private String rootCertPath;
	/** 是否验证验签证书CN，除了false都验 */
	private boolean ifValidateCNName = true;
	/** 是否验证https证书，默认都不验 */
	private boolean ifValidateRemoteCert = false;
	/** signMethod，没配按01吧 */
	private String signMethod = "01";
	/** version，没配按5.0.0 */
	private String version = "5.0.0";
	/** frontUrl */
	private String frontUrl;
	/** backUrl */
	private String backUrl;

	/* 缴费相关地址 */
	private String jfFrontRequestUrl;
	private String jfBackRequestUrl;
	private String jfSingleQueryUrl;
	private String jfCardRequestUrl;
	private String jfAppRequestUrl;

	private String qrcBackTransUrl;
	private String qrcB2cIssBackTransUrl;
	private String qrcB2cMerBackTransUrl;

	/** 配置文件中的前台URL常量. */
	public static final String SDK_FRONT_URL = "acpsdk.frontTransUrl";
	/** 配置文件中的后台URL常量. */
	public static final String SDK_BACK_URL = "acpsdk.backTransUrl";
	/** 配置文件中的单笔交易查询URL常量. */
	public static final String SDK_SIGNQ_URL = "acpsdk.singleQueryUrl";
	/** 配置文件中的批量交易查询URL常量. */
	public static final String SDK_BATQ_URL = "acpsdk.batchQueryUrl";
	/** 配置文件中的批量交易URL常量. */
	public static final String SDK_BATTRANS_URL = "acpsdk.batchTransUrl";
	/** 配置文件中的文件类交易URL常量. */
	public static final String SDK_FILETRANS_URL = "acpsdk.fileTransUrl";
	/** 配置文件中的有卡交易URL常量. */
	public static final String SDK_CARD_URL = "acpsdk.cardTransUrl";
	/** 配置文件中的app交易URL常量. */
	public static final String SDK_APP_URL = "acpsdk.appTransUrl";

	/** 以下缴费产品使用，其余产品用不到，无视即可 */
	// 前台请求地址
	public static final String JF_SDK_FRONT_TRANS_URL = "acpsdk.jfFrontTransUrl";
	// 后台请求地址
	public static final String JF_SDK_BACK_TRANS_URL = "acpsdk.jfBackTransUrl";
	// 单笔查询请求地址
	public static final String JF_SDK_SINGLE_QUERY_URL = "acpsdk.jfSingleQueryUrl";
	// 有卡交易地址
	public static final String JF_SDK_CARD_TRANS_URL = "acpsdk.jfCardTransUrl";
	// App交易地址
	public static final String JF_SDK_APP_TRANS_URL = "acpsdk.jfAppTransUrl";
	// 人到人
	public static final String QRC_BACK_TRANS_URL = "acpsdk.qrcBackTransUrl";
	// 人到人
	public static final String QRC_B2C_ISS_BACK_TRANS_URL = "acpsdk.qrcB2cIssBackTransUrl";
	// 人到人
	public static final String QRC_B2C_MER_BACK_TRANS_URL = "acpsdk.qrcB2cMerBackTransUrl";

	/** 配置文件中签名证书路径常量. */
	public static final String SDK_SIGNCERT_PATH = "acpsdk.signCert.path";
	/** 配置文件中签名证书密码常量. */
	public static final String SDK_SIGNCERT_PWD = "acpsdk.signCert.pwd";
	/** 配置文件中签名证书类型常量. */
	public static final String SDK_SIGNCERT_TYPE = "acpsdk.signCert.type";
	/** 配置文件中密码加密证书路径常量. */
	public static final String SDK_ENCRYPTCERT_PATH = "acpsdk.encryptCert.path";
	/** 配置文件中磁道加密证书路径常量. */
	public static final String SDK_ENCRYPTTRACKCERT_PATH = "acpsdk.encryptTrackCert.path";
	/** 配置文件中磁道加密公钥模数常量. */
	public static final String SDK_ENCRYPTTRACKKEY_MODULUS = "acpsdk.encryptTrackKey.modulus";
	/** 配置文件中磁道加密公钥指数常量. */
	public static final String SDK_ENCRYPTTRACKKEY_EXPONENT = "acpsdk.encryptTrackKey.exponent";
	/** 配置文件中验证签名证书目录常量. */
	public static final String SDK_VALIDATECERT_DIR = "acpsdk.validateCert.dir";

	/** 配置文件中是否加密cvn2常量. */
	public static final String SDK_CVN_ENC = "acpsdk.cvn2.enc";
	/** 配置文件中是否加密cvn2有效期常量. */
	public static final String SDK_DATE_ENC = "acpsdk.date.enc";
	/** 配置文件中是否加密卡号常量. */
	public static final String SDK_PAN_ENC = "acpsdk.pan.enc";
	/** 配置文件中证书使用模式 */
	public static final String SDK_SINGLEMODE = "acpsdk.singleMode";
	/** 配置文件中安全密钥 */
	public static final String SDK_SECURITYKEY = "acpsdk.secureKey";
	/** 配置文件中根证书路径常量 */
	public static final String SDK_ROOTCERT_PATH = "acpsdk.rootCert.path";
	/** 配置文件中根证书路径常量 */
	public static final String SDK_MIDDLECERT_PATH = "acpsdk.middleCert.path";
	/** 配置是否需要验证验签证书CN，除了false之外的值都当true处理 */
	public static final String SDK_IF_VALIDATE_CN_NAME = "acpsdk.ifValidateCNName";
	/** 配置是否需要验证https证书，除了true之外的值都当false处理 */
	public static final String SDK_IF_VALIDATE_REMOTE_CERT = "acpsdk.ifValidateRemoteCert";
	/** signmethod */
	public static final String SDK_SIGN_METHOD = "acpsdk.signMethod";
	/** version */
	public static final String SDK_VERSION = "acpsdk.version";
	/** 后台通知地址 */
	public static final String SDK_BACKURL = "acpsdk.backUrl";
	/** 前台通知地址 */
	public static final String SDK_FRONTURL = "acpsdk.frontUrl";

	public static final String CERT_FOOLDER = PathKit.getRootClassPath() + File.separator + "certs" + File.separator;

	/** 属性文件对象. */
	private Prop properties;
	private String propFileName;

	public SDKConfig(String propFileName) {
		this.propFileName = propFileName;
		this.properties = PropKit.use(propFileName);
		loadProperties(properties);
	}

	/**
	 * 根据传入的 {@link #load(java.util.Properties)}对象设置配置参数
	 * 
	 * @param pro
	 */
	public void loadProperties(Prop pro) {
		LogKit.info("开始从属性文件中加载配置项");
		String value = null;

		value = pro.get(SDK_SIGNCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.signCertPath = CERT_FOOLDER + value.trim();
			LogKit.info("配置项：私钥签名证书路径==>" + SDK_SIGNCERT_PATH + "==>" + value + " 已加载");
		}
		value = pro.get(SDK_SIGNCERT_PWD);
		if (!SDKUtil.isEmpty(value)) {
			this.signCertPwd = value.trim();
			LogKit.info("配置项：私钥签名证书密码==>" + SDK_SIGNCERT_PWD + " 已加载");
		}
		value = pro.get(SDK_SIGNCERT_TYPE);
		if (!SDKUtil.isEmpty(value)) {
			this.signCertType = value.trim();
			LogKit.info("配置项：私钥签名证书类型==>" + SDK_SIGNCERT_TYPE + "==>" + value + " 已加载");
		}
		value = pro.get(SDK_ENCRYPTCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptCertPath = CERT_FOOLDER + value.trim();
			LogKit.info("配置项：敏感信息加密证书==>" + SDK_ENCRYPTCERT_PATH + "==>" + value + " 已加载");
		}
		value = pro.get(SDK_VALIDATECERT_DIR);
		if (!SDKUtil.isEmpty(value)) {
			this.validateCertDir = CERT_FOOLDER + value.trim();
			LogKit.info("配置项：验证签名证书路径(这里配置的是目录，不要指定到公钥文件)==>" + SDK_VALIDATECERT_DIR + "==>" + value + " 已加载");
		}
		value = pro.get(SDK_FRONT_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.frontRequestUrl = value.trim();
		}
		value = pro.get(SDK_BACK_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.backRequestUrl = value.trim();
		}
		value = pro.get(SDK_BATQ_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.batchQueryUrl = value.trim();
		}
		value = pro.get(SDK_BATTRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.batchTransUrl = value.trim();
		}
		value = pro.get(SDK_FILETRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.fileTransUrl = value.trim();
		}
		value = pro.get(SDK_SIGNQ_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.singleQueryUrl = value.trim();
		}
		value = pro.get(SDK_CARD_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.cardRequestUrl = value.trim();
		}
		value = pro.get(SDK_APP_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.appRequestUrl = value.trim();
		}
		value = pro.get(SDK_ENCRYPTTRACKCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackCertPath = CERT_FOOLDER + value.trim();
		}

		value = pro.get(SDK_SECURITYKEY);
		if (!SDKUtil.isEmpty(value)) {
			this.secureKey = value.trim();
		}
		value = pro.get(SDK_ROOTCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.rootCertPath = CERT_FOOLDER + value.trim();
		}
		value = pro.get(SDK_MIDDLECERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.middleCertPath = CERT_FOOLDER + value.trim();
		}

		/** 缴费部分 **/
		value = pro.get(JF_SDK_FRONT_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfFrontRequestUrl = value.trim();
		}

		value = pro.get(JF_SDK_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfBackRequestUrl = value.trim();
		}

		value = pro.get(JF_SDK_SINGLE_QUERY_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfSingleQueryUrl = value.trim();
		}

		value = pro.get(JF_SDK_CARD_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfCardRequestUrl = value.trim();
		}

		value = pro.get(JF_SDK_APP_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfAppRequestUrl = value.trim();
		}

		value = pro.get(QRC_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.qrcBackTransUrl = value.trim();
		}

		value = pro.get(QRC_B2C_ISS_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.qrcB2cIssBackTransUrl = value.trim();
		}

		value = pro.get(QRC_B2C_MER_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.qrcB2cMerBackTransUrl = value.trim();
		}

		value = pro.get(SDK_ENCRYPTTRACKKEY_EXPONENT);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackKeyExponent = value.trim();
		}

		value = pro.get(SDK_ENCRYPTTRACKKEY_MODULUS);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackKeyModulus = value.trim();
		}

		value = pro.get(SDK_IF_VALIDATE_CN_NAME);
		if (!SDKUtil.isEmpty(value)) {
			if (SDKConstants.FALSE_STRING.equals(value.trim()))
				this.ifValidateCNName = false;
		}

		value = pro.get(SDK_IF_VALIDATE_REMOTE_CERT);
		if (!SDKUtil.isEmpty(value)) {
			if (SDKConstants.TRUE_STRING.equals(value.trim()))
				this.ifValidateRemoteCert = true;
		}

		value = pro.get(SDK_SIGN_METHOD);
		if (!SDKUtil.isEmpty(value)) {
			this.signMethod = value.trim();
		}

		value = pro.get(SDK_SIGN_METHOD);
		if (!SDKUtil.isEmpty(value)) {
			this.signMethod = value.trim();
		}
		value = pro.get(SDK_VERSION);
		if (!SDKUtil.isEmpty(value)) {
			this.version = value.trim();
		}
		value = pro.get(SDK_FRONTURL);
		if (!SDKUtil.isEmpty(value)) {
			this.frontUrl = value.trim();
		}
		value = pro.get(SDK_BACKURL);
		if (!SDKUtil.isEmpty(value)) {
			this.backUrl = value.trim();
		}
	}

	public String getFrontRequestUrl() {
		return frontRequestUrl;
	}

	public void setFrontRequestUrl(String frontRequestUrl) {
		this.frontRequestUrl = frontRequestUrl;
	}

	public String getBackRequestUrl() {
		return backRequestUrl;
	}

	public void setBackRequestUrl(String backRequestUrl) {
		this.backRequestUrl = backRequestUrl;
	}

	public String getSignCertPath() {
		return signCertPath;
	}

	public void setSignCertPath(String signCertPath) {
		this.signCertPath = signCertPath;
	}

	public String getSignCertPwd() {
		return signCertPwd;
	}

	public void setSignCertPwd(String signCertPwd) {
		this.signCertPwd = signCertPwd;
	}

	public String getSignCertType() {
		return signCertType;
	}

	public void setSignCertType(String signCertType) {
		this.signCertType = signCertType;
	}

	public String getEncryptCertPath() {
		return encryptCertPath;
	}

	public void setEncryptCertPath(String encryptCertPath) {
		this.encryptCertPath = encryptCertPath;
	}

	public String getValidateCertDir() {
		return validateCertDir;
	}

	public void setValidateCertDir(String validateCertDir) {
		this.validateCertDir = validateCertDir;
	}

	public String getSingleQueryUrl() {
		return singleQueryUrl;
	}

	public void setSingleQueryUrl(String singleQueryUrl) {
		this.singleQueryUrl = singleQueryUrl;
	}

	public String getBatchQueryUrl() {
		return batchQueryUrl;
	}

	public void setBatchQueryUrl(String batchQueryUrl) {
		this.batchQueryUrl = batchQueryUrl;
	}

	public String getBatchTransUrl() {
		return batchTransUrl;
	}

	public void setBatchTransUrl(String batchTransUrl) {
		this.batchTransUrl = batchTransUrl;
	}

	public String getFileTransUrl() {
		return fileTransUrl;
	}

	public void setFileTransUrl(String fileTransUrl) {
		this.fileTransUrl = fileTransUrl;
	}

	public String getSignCertDir() {
		return signCertDir;
	}

	public void setSignCertDir(String signCertDir) {
		this.signCertDir = signCertDir;
	}

	public String getCardRequestUrl() {
		return cardRequestUrl;
	}

	public void setCardRequestUrl(String cardRequestUrl) {
		this.cardRequestUrl = cardRequestUrl;
	}

	public String getAppRequestUrl() {
		return appRequestUrl;
	}

	public void setAppRequestUrl(String appRequestUrl) {
		this.appRequestUrl = appRequestUrl;
	}

	public String getEncryptTrackCertPath() {
		return encryptTrackCertPath;
	}

	public void setEncryptTrackCertPath(String encryptTrackCertPath) {
		this.encryptTrackCertPath = encryptTrackCertPath;
	}

	public String getJfFrontRequestUrl() {
		return jfFrontRequestUrl;
	}

	public void setJfFrontRequestUrl(String jfFrontRequestUrl) {
		this.jfFrontRequestUrl = jfFrontRequestUrl;
	}

	public String getJfBackRequestUrl() {
		return jfBackRequestUrl;
	}

	public void setJfBackRequestUrl(String jfBackRequestUrl) {
		this.jfBackRequestUrl = jfBackRequestUrl;
	}

	public String getJfSingleQueryUrl() {
		return jfSingleQueryUrl;
	}

	public void setJfSingleQueryUrl(String jfSingleQueryUrl) {
		this.jfSingleQueryUrl = jfSingleQueryUrl;
	}

	public String getJfCardRequestUrl() {
		return jfCardRequestUrl;
	}

	public void setJfCardRequestUrl(String jfCardRequestUrl) {
		this.jfCardRequestUrl = jfCardRequestUrl;
	}

	public String getJfAppRequestUrl() {
		return jfAppRequestUrl;
	}

	public void setJfAppRequestUrl(String jfAppRequestUrl) {
		this.jfAppRequestUrl = jfAppRequestUrl;
	}

	public String getSingleMode() {
		return singleMode;
	}

	public void setSingleMode(String singleMode) {
		this.singleMode = singleMode;
	}

	public String getEncryptTrackKeyExponent() {
		return encryptTrackKeyExponent;
	}

	public void setEncryptTrackKeyExponent(String encryptTrackKeyExponent) {
		this.encryptTrackKeyExponent = encryptTrackKeyExponent;
	}

	public String getEncryptTrackKeyModulus() {
		return encryptTrackKeyModulus;
	}

	public void setEncryptTrackKeyModulus(String encryptTrackKeyModulus) {
		this.encryptTrackKeyModulus = encryptTrackKeyModulus;
	}

	public String getSecureKey() {
		return secureKey;
	}

	public void setSecureKey(String securityKey) {
		this.secureKey = securityKey;
	}

	public String getMiddleCertPath() {
		return middleCertPath;
	}

	public void setMiddleCertPath(String middleCertPath) {
		this.middleCertPath = middleCertPath;
	}

	public boolean isIfValidateCNName() {
		return ifValidateCNName;
	}

	public void setIfValidateCNName(boolean ifValidateCNName) {
		this.ifValidateCNName = ifValidateCNName;
	}

	public boolean isIfValidateRemoteCert() {
		return ifValidateRemoteCert;
	}

	public void setIfValidateRemoteCert(boolean ifValidateRemoteCert) {
		this.ifValidateRemoteCert = ifValidateRemoteCert;
	}

	public String getSignMethod() {
		return signMethod;
	}

	public void setSignMethod(String signMethod) {
		this.signMethod = signMethod;
	}

	public String getQrcBackTransUrl() {
		return qrcBackTransUrl;
	}

	public void setQrcBackTransUrl(String qrcBackTransUrl) {
		this.qrcBackTransUrl = qrcBackTransUrl;
	}

	public String getQrcB2cIssBackTransUrl() {
		return qrcB2cIssBackTransUrl;
	}

	public void setQrcB2cIssBackTransUrl(String qrcB2cIssBackTransUrl) {
		this.qrcB2cIssBackTransUrl = qrcB2cIssBackTransUrl;
	}

	public String getQrcB2cMerBackTransUrl() {
		return qrcB2cMerBackTransUrl;
	}

	public void setQrcB2cMerBackTransUrl(String qrcB2cMerBackTransUrl) {
		this.qrcB2cMerBackTransUrl = qrcB2cMerBackTransUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFrontUrl() {
		return frontUrl;
	}

	public void setFrontUrl(String frontUrl) {
		this.frontUrl = frontUrl;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	public String getRootCertPath() {
		return rootCertPath;
	}

	public void setRootCertPath(String rootCertPath) {
		this.rootCertPath = rootCertPath;
	}

	public String getPropFileName() {
		return propFileName;
	}

}
