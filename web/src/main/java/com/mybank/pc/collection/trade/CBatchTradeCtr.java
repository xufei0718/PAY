package com.mybank.pc.collection.trade;

import com.jfinal.core.ActionKey;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.mybank.pc.collection.model.UnionpayBatchCollection;
import com.mybank.pc.core.CoreController;

public class CBatchTradeCtr extends CoreController {

	@ActionKey("/coll/batch/list")
	public void list() {
		Page<UnionpayBatchCollection> page;

		String finalCode = getPara("finalCode");
		String bTime = getPara("bTime");
		String eTime = getPara("eTime");

		Kv kv = Kv.create();
		kv.set("finalCode", finalCode).set("bTime", bTime).set("eTime", eTime);

		SqlPara sqlPara = Db.getSqlPara("collection_batch.findUnionpayBatchCollectionHeadColumn", kv);
		page = UnionpayBatchCollection.dao.paginate(getPN(), getPS(), sqlPara);

		renderJson(page);
	}

}
