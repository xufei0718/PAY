package com.mybank.pc.collection.entrust;

import com.jfinal.aop.Duang;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.mybank.pc.Consts;
import com.mybank.pc.collection.model.CollectionEntrust;
import com.mybank.pc.collection.model.UnionpayEntrust;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.core.CoreException;
import com.mybank.pc.kits.CookieKit;
import com.mybank.pc.merchant.model.MerchantInfo;

public class CEntrustCtr extends CoreController {

	private CEntrustSrv cEntrustSrv = Duang.duang(CEntrustSrv.class);

	@ActionKey("/coll/entrust/list")
	public void list() {
		Page<CollectionEntrust> page;
		MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);

		String serach = getPara("search");

		Kv kv = Kv.create();
		kv.set("search", serach);
		if (merInfo != null) {
			kv.set("merID", merInfo.getId());
		}

		SqlPara sqlPara = Db.getSqlPara("collection_entrust.findCollectionEntrustPage", kv);
		page = CollectionEntrust.dao.paginate(getPN(), getPS(), sqlPara);

		renderJson(page);
	}

	@ActionKey("/coll/entrust/trade/list")
	public void tradeList() {
		Page<UnionpayEntrust> page;
		MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);

		String serach = getPara("search");
		String bTime = getPara("bTime");
		String eTime = getPara("eTime");
		String txnType = getPara("txnType");

		// 建立委托
		if ("0".equals(txnType)) {
			txnType = "72";
		} else if ("1".equals(txnType)) {// 解除委托
			txnType = "74";
		}

		Kv kv = Kv.create();
		kv.set("search", serach).set("bTime", bTime).set("eTime", eTime).set("txnType", txnType);
		if (merInfo != null) {
			kv.set("merchantID", merInfo.getId());
		}

		SqlPara sqlPara = Db.getSqlPara("collection_entrust.findUnionpayEntrustPage", kv);
		page = UnionpayEntrust.dao.paginate(getPN(), getPS(), sqlPara);

		renderJson(page);
	}

	@ActionKey("/coll/entrust/establish")
	public void establish() {
		MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
		String operID = CookieKit.get(this, Consts.USER_ACCESS_TOKEN);

		String merCode = getPara("merCode");

		String accNo = getPara("accNo");
		String certifTp = getPara("certifTp");
		String certifId = getPara("certifId");
		String customerNm = getPara("customerNm");
		String phoneNo = getPara("phoneNo");
		String cvn2 = getPara("cvn2");
		String expired = getPara("expired");

		try {
			Kv kv = Kv.create();
			kv.set("accNo", accNo).set("certifTp", certifTp).set("certifId", certifId).set("customerNm", customerNm)
					.set("phoneNo", phoneNo).set("cvn2", cvn2 == null ? "" : cvn2)
					.set("expired", expired == null ? "" : expired).set("operID", operID);
			if (merInfo != null) {
				kv.set("merchantID", merInfo.getId());
			}

			boolean isSuccess = true;
			Kv respKv = null;
			if (merCode.equals("all")) {
				try {
					Kv[] resultKvs = cEntrustSrv.establishAll(kv);
					for (int i = 0; i < resultKvs.length; ++i) {
						isSuccess = isSuccess && resultKvs[i].getBoolean("isSuccess");
					}
				} catch (Exception e) {
					isSuccess = false;
				}
			} else {
				try {
					respKv = cEntrustSrv.establish(kv.set("merCode", merCode));
					isSuccess = respKv.getBoolean("isSuccess");
				} catch (Exception e) {
					isSuccess = false;
				}
			}

			if (isSuccess) {
				renderSuccessJSON("交易成功");
			} else {
				renderFailJSON("交易失败");
			}

		} catch (Exception e) {
			throw new CoreException("交易失败");
		}
	}

	@ActionKey("/coll/entrust/terminate")
	public void terminate() {
		MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);

		String merCode = getPara("merCode");
		String accNo = getPara("accNo");

		try {
			Kv kv = Kv.create();
			kv.set("accNo", accNo);
			if (merInfo != null) {
				kv.set("merchantID", merInfo.getId());
			}

			String userId = CookieKit.get(this, Consts.USER_ACCESS_TOKEN);

			boolean isSuccess = false;
			if (merCode.equals("all")) {
				try {
					isSuccess = cEntrustSrv.terminate(kv.set("merCode", "0"), userId);
				} catch (Exception e) {
					isSuccess = false;
				}
				try {
					isSuccess = cEntrustSrv.terminate(kv.set("merCode", "1"), userId) && isSuccess;
				} catch (Exception e) {
					isSuccess = false;
				}
			} else {
				try {
					isSuccess = cEntrustSrv.terminate(kv.set("merCode", merCode), userId);
				} catch (Exception e) {
					isSuccess = false;
				}
			}

			if (isSuccess) {
				renderSuccessJSON("交易成功");
			} else {
				renderFailJSON("交易失败");
			}

		} catch (Exception e) {
			throw new CoreException("交易失败");
		}
	}
}
