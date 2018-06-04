package com.mybank.pc.task;

import com.jfinal.aop.Duang;
import com.jfinal.kit.LogKit;
import com.mybank.pc.collection.trade.CBatchQuerySrv;

public class CollectionBatchQueryTask implements Runnable {

	private CBatchQuerySrv cBatchQuerySrv = Duang.duang(CBatchQuerySrv.class);

	@Override
	public void run() {
		LogKit.info("开始调用批量代收查询接口...");
		cBatchQuerySrv.batchQuery();

	}
}
