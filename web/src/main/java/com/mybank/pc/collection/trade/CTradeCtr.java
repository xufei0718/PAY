package com.mybank.pc.collection.trade;

import java.util.ArrayList;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jfinal.aop.Duang;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.User;
import com.mybank.pc.collection.model.CollectionTrade;
import com.mybank.pc.collection.model.UnionpayCollection;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.exception.ValidateCTRException;
import com.mybank.pc.kits.CookieKit;
import com.mybank.pc.merchant.model.MerchantCust;
import com.mybank.pc.merchant.model.MerchantInfo;

public class CTradeCtr extends CoreController {

	private static final String ADMIN_LIST_POWER = "#p/coll/trade/admin/list";

	private CTradeSrv cCTradeSrv = Duang.duang(CTradeSrv.class);

	@ActionKey("/coll/trade/list")
	public void list() {
		MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
		Set<String> resSet = getAttr(Consts.CURR_USER_RESES);
		Page<CollectionTrade> page;
		boolean isAdmin = false;

		String finalCode = getPara("finalCode");
		String bTime = getPara("bTime");
		String eTime = getPara("eTime");
		String merSearchKey = getPara("merSearchKey");
		String serach = getPara("search");
		String clearStatus = getPara("clearStatus");
		if (resSet != null && resSet.contains(ADMIN_LIST_POWER)) {
			isAdmin = true;
		}

		if (!isAdmin && merInfo == null) {
			page = new Page<CollectionTrade>(new ArrayList<CollectionTrade>(), getPN(), getPS(), 0, 0);
		} else {
			Kv kv = Kv.by("search", serach).set("finalCode", finalCode).set("bTime", bTime).set("eTime", eTime)
					.set("clearStatus", clearStatus);
			if (isAdmin) {
				kv.set("merSearchKey", merSearchKey);
			}
			if (!isAdmin && merInfo != null) {
				kv.set("merchantID", merInfo.getId());
			}
			page = CollectionTrade.findPage(getPN(), getPS(), kv);
		}

		renderJson(JSON.toJSONString(Kv.by("isAdmin", isAdmin).set("pageInfo", page),
				SerializerFeature.DisableCircularReferenceDetect));
	}

	@ActionKey("/coll/trade/export/detail")
	public void exportTradeDetail() {
		MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
		User currUser = currUser();
		Set<String> resSet = getAttr(Consts.CURR_USER_RESES);
		boolean isAdmin = false;

		String finalCode = getPara("finalCode");
		String bTime = getPara("bTime");
		String eTime = getPara("eTime");
		String merSearchKey = getPara("merSearchKey");
		String serach = getPara("search");
		String clearStatus = getPara("clearStatus");
		if (resSet != null && resSet.contains(ADMIN_LIST_POWER)) {
			isAdmin = true;
		}

		if (!isAdmin && merInfo == null) {
			renderFailJSON("没有查询到要导出的数据");
		} else {
			Kv kv = Kv.by("search", serach).set("finalCode", finalCode).set("bTime", bTime).set("eTime", eTime)
					.set("clearStatus", clearStatus);
			if (isAdmin) {
				kv.set("merSearchKey", merSearchKey);
			}
			if (!isAdmin && merInfo != null) {
				kv.set("merchantID", merInfo.getId());
			}

			try {
				Kv result = cCTradeSrv.exportTradeDetailExcel(kv, isAdmin, currUser);
				if (result.containsKey("errorMsg")) {
					renderFailJSON(result.getStr("errorMsg"));
				} else {
					renderSuccessJSON("文件导出成功", result.getStr("fileName"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				renderFailJSON("文件导出失败");
			}
		}
	}

	@ActionKey("/coll/trade/getMerCustPage")
	public void merCustPage() {
		MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
		Page<MerchantCust> page;

		String serach = getPara("search");

		Kv kv = Kv.create();
		kv.set("search", serach).set("merchantID", merInfo == null ? "" : merInfo.getId());

		SqlPara sqlPara = Db.getSqlPara("collection_trade.findMerchantCustListPage", kv);
		page = MerchantCust.dao.paginate(getPN(), getPS(), sqlPara);
		renderJson(page);
	}

	@ActionKey("/coll/trade/getMerCust")
	public void merCust() {
		MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
		if (merInfo == null || merInfo.getId() == null) {
			renderJson(new ArrayList<MerchantCust>());
		} else {
			String serach = getPara("search");

			Kv kv = Kv.create();
			kv.set("search", serach).set("merchantID", merInfo.getId());

			SqlPara sqlPara = Db.getSqlPara("collection_trade.findMerchantCustListPage", kv);
			renderJson(JSON.toJSONString(MerchantCust.dao.find(sqlPara),
					SerializerFeature.DisableCircularReferenceDetect));
		}
	}

	@ActionKey("/coll/trade/fee")
	public void fee() {
		MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
		String operID = CookieKit.get(this, Consts.USER_ACCESS_TOKEN);

		String bussType = getPara("bussType");
		String accNo = getPara("accNo");
		String txnAmt = getPara("txnAmt");
		String custID = getPara("custID");
		String merchantID = merInfo == null ? getPara("merchantID") : String.valueOf(merInfo.getId());

		Kv kv = Kv.create();
		kv.set("bussType", bussType).set("accNo", accNo).set("txnAmt", txnAmt).set("custID", custID)
				.set("merchantID", merchantID).set("operID", operID);

		Kv initiateRequest = null;
		try {
			initiateRequest = cCTradeSrv.validateAndBuildInitiateRequest(kv);
		} catch (Exception e) {
			e.printStackTrace();
		}

		renderJson(initiateRequest);
	}

	@ActionKey("/coll/trade/initiate")
	public void initiate() {
		MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
		String operID = CookieKit.get(this, Consts.USER_ACCESS_TOKEN);

		String bussType = getPara("bussType");
		String accNo = getPara("accNo");
		String txnAmt = getPara("txnAmt");
		String custID = getPara("custID");
		String merchantID = merInfo == null ? getPara("merchantID") : String.valueOf(merInfo.getId());

		Kv kv = Kv.create();
		kv.set("bussType", bussType).set("accNo", accNo).set("txnAmt", txnAmt).set("custID", custID)
				.set("merchantID", merchantID).set("operID", operID);

		boolean isSuccess = false;
		String errorMsg = null;
		try {
			Kv result = cCTradeSrv.initiate(kv);
			isSuccess = result.getBoolean("isSuccess");
			if (!isSuccess) {
				UnionpayCollection unionpayCollection = (UnionpayCollection) result.get("unionpayCollection");
				if (unionpayCollection != null && "2".equals(unionpayCollection.getFinalCode())) {
					errorMsg = unionpayCollection.getRespMsg();
				}
			}
		} catch (ValidateCTRException ve) {
			ve.printStackTrace();
			isSuccess = false;
			errorMsg = "发起交易失败，" + ve.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;
			errorMsg = "发起交易失败，系统内部错误";
		}

		if (isSuccess) {
			renderSuccessJSON("发起交易成功");
		} else {
			renderFailJSON(errorMsg);
		}
	}

	@ActionKey("/coll/trade/syncOrderStatus")
	public void syncOrderStatus() throws Exception {
		String tradeNo = getPara("tradeNo");
		UnionpayCollection unionpayCollection = null;
		boolean isThrowException = false;
		try {
			unionpayCollection = UnionpayCollection.findByTradeNo(tradeNo);
			cCTradeSrv.syncOrderStatus(unionpayCollection);
		} catch (Exception e) {
			e.printStackTrace();
			isThrowException = true;
		} finally {
			CollectionTrade updatedCollectionTrade = null;
			if (isThrowException || unionpayCollection == null
					|| (updatedCollectionTrade = CollectionTrade.findByTradeNo(unionpayCollection)) == null) {
				renderJson(new CollectionTrade());
			}
			renderJson(updatedCollectionTrade.findAndSetMerInfo());
		}
	}

}
