package com.mybank.pc.kits;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.CardBin;
import com.mybank.pc.exception.ValidateCTRException;
import com.mybank.pc.kits.unionpay.acp.SDK;
import com.mybank.pc.merchant.model.MerchantFee;

/**
 * 手续费工具类
 * 
 * @author hkun
 *
 */
public class FeeKit {

	private static int[] lenPriorityQueue = new int[] { 9, 6, 11, 8, 10, 7, 5, 4, 12, 3, 2 };

	private static final BigDecimal REALTIME_TYPE_0_MAX_FEE = new BigDecimal(1800);

	public static CardBin getCardBin(String card) {
		CardBin result = null;
		if (StringUtils.isNotBlank(card)) {
			card = card.trim();
			String cacheName = Consts.CACHE_NAMES.cardBin.name();
			for (int i = 0, cardLen = card.length(); i < lenPriorityQueue.length; ++i) {
				if (cardLen < lenPriorityQueue[i]) {
					continue;
				}
				if ((result = CacheKit.get(cacheName, card.substring(0, lenPriorityQueue[i]))) != null) {
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 按圆计算手续费
	 * 
	 * @param txnAmt
	 * @param cardBin
	 * @param merId
	 *            系统商户号
	 * @return
	 */
	public static BigDecimal getBankFeeByYuan(BigDecimal txnAmt, CardBin cardBin, String merId) {
		BigDecimal multiple = new BigDecimal(100);
		BigDecimal fee = getBankFeeByCent(txnAmt.multiply(multiple), cardBin, merId).divide(multiple);
		fee = fee.setScale(2, BigDecimal.ROUND_HALF_UP);
		return fee;
	}

	/**
	 * 
	 * 按圆计算商户手续费
	 * 
	 * @param amount
	 * @param merID
	 *            商户ID
	 * @param tradeType
	 *            1加急 2标准
	 * @return
	 */
	public static BigDecimal getMerchantFee(BigDecimal amount, int merID, String tradeType) {
		BigDecimal fee = new BigDecimal(0);
		MerchantFee merchantFee = findMerchantFee(amount, merID, tradeType);
		if (merchantFee == null) {
			throw new ValidateCTRException("符合金额[" + amount.toPlainString() + "]的手续费设置不存在!!");
		}

		String feeType = merchantFee.getFeeType();
		if (feeType.equals("1")) {// 定额
			fee = merchantFee.getAmount();
		} else if (feeType.equals("2")) {// 比例
			fee = amount.multiply(merchantFee.getRatio());
		}
		fee = fee.setScale(2, BigDecimal.ROUND_HALF_UP);
		return fee;
	}

	/**
	 * @param amount
	 * @param merID
	 *            商户ID
	 * @param tradeType
	 *            1加急 2标准
	 * @return
	 */
	public static MerchantFee findMerchantFee(BigDecimal amount, int merID, String tradeType) {
		SqlPara sqlPara = Db.getSqlPara("collection_trade.findMerchantFee",
				Kv.create().set("amount", amount).set("merID", merID).set("tradeType", tradeType));
		return MerchantFee.dao.findFirst(sqlPara);
	}

	/**
	 * 按分计算手续费
	 * 
	 * @param txnAmt
	 * @param cardBin
	 * @param merId
	 *            系统商户号
	 * @return
	 */
	public static BigDecimal getBankFeeByCent(BigDecimal txnAmt, CardBin cardBin, String merId) {
		BigDecimal fee = new BigDecimal(0);

		// 实时
		if (SDK.getSDK(SDK.MER_CODE_REALTIME_CH).getMerId().equals(merId)) {
			String cardType = cardBin.getCardType();
			// 借记卡
			if (cardType.equals("0")) {
				// 0.5% 18元封顶
				fee = txnAmt.multiply(new BigDecimal("0.005"));
				if (fee.compareTo(REALTIME_TYPE_0_MAX_FEE) > 0) {
					fee = new BigDecimal(REALTIME_TYPE_0_MAX_FEE.intValue());
				}
			} else if (cardType.equals("1")) {// 贷记卡
				fee = txnAmt.multiply(new BigDecimal("0.0055"));
			}
		} else if (SDK.getSDK(SDK.MER_CODE_BATCH_CH).getMerId().equals(merId)) {
			if (txnAmt.compareTo(new BigDecimal(100000)) < 0) {
				// <1000 0.1%
				fee = txnAmt.divide(new BigDecimal(1000));
			} else if (txnAmt.compareTo(new BigDecimal(500000)) <= 0) {
				// 1000~5000 1.2元
				fee = new BigDecimal(120);
			} else {
				fee = new BigDecimal(170);
			}
			if (fee.longValue() < 70) {
				fee = new BigDecimal(70);
			}
		} else if (SDK.getSDK(SDK.MER_CODE_REALTIME_YS_4).getMerId().equals(merId)) {
			fee = new BigDecimal(400);
		} else if (SDK.getSDK(SDK.MER_CODE_REALTIME_YS_2).getMerId().equals(merId)) {
			fee = new BigDecimal(200);
		} else if (SDK.getSDK(SDK.MER_CODE_TEST).getMerId().equals(merId)) {
			fee = new BigDecimal(0);
		} else {
			throw new ValidateCTRException("获取银行手续费失败，不支持的卡Bin[" + cardBin.getBankName() + " " + cardBin.getCardName()
					+ " " + cardBin.getCBin() + "]");
		}

		return fee;
	}

}
