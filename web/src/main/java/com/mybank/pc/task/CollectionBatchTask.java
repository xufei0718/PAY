package com.mybank.pc.task;

import com.jfinal.aop.Duang;
import com.jfinal.kit.LogKit;
import com.mybank.pc.collection.trade.CBatchTradeSrv;

public class CollectionBatchTask implements Runnable {

	private CBatchTradeSrv cBatchTradeSrv = Duang.duang(CBatchTradeSrv.class);

	@Override
	public void run() {
		LogKit.info("开始调用批量代收...");
		cBatchTradeSrv.sendBatchOrder();
	}
}
