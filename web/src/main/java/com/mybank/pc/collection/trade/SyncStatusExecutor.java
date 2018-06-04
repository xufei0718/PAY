package com.mybank.pc.collection.trade;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.jfinal.aop.Duang;
import com.mybank.pc.collection.model.UnionpayBatchCollection;
import com.mybank.pc.collection.model.UnionpayCollection;

public class SyncStatusExecutor {

	private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

	private static CTradeSrv cCTradeSrv = Duang.duang(CTradeSrv.class);
	private static CBatchQuerySrv cBatchQuerySrv = Duang.duang(CBatchQuerySrv.class);

	public static ScheduledFuture<UnionpayCollection> scheduleInProcessSingleQuery(String orderId, long delay,
			TimeUnit unit) {
		return schedule(new InProcessSingleQuery(orderId, 0, delay), delay, unit);
	}

	public static ScheduledFuture<UnionpayCollection> scheduleInProcessSingleQuery(String orderId, long delay,
			int maxAllowQueryCount, TimeUnit unit) {
		return schedule(new InProcessSingleQuery(orderId, 0, delay, maxAllowQueryCount), delay, unit);
	}

	public static ScheduledFuture<UnionpayCollection> scheduleNotClearSingleQuery(String orderId, long delay,
			TimeUnit unit) {
		return schedule(new NotClearSingleQuery(orderId), delay, unit);
	}

	public static ScheduledFuture<UnionpayBatchCollection> scheduleNotClearBatchQuery(String batchId, String merId,
			String batchNo, String txnTime, long delay, TimeUnit unit) {
		return schedule(new NotClearBatchQuery(batchId, merId, batchNo, txnTime), delay, unit);
	}

	public static <T> ScheduledFuture<T> schedule(Callable<T> callable, long delay, TimeUnit unit) {
		return scheduledExecutorService.schedule(callable, delay, unit);
	}

	public static class InProcessSingleQuery implements Callable<UnionpayCollection> {

		private int maxAllowQueryCount;
		private int preQueryCount;
		private long delay;
		private String orderId;

		public InProcessSingleQuery(String orderId, int preQueryCount, long delay) {
			this.orderId = orderId;
			this.preQueryCount = preQueryCount;
			this.delay = delay;
			this.maxAllowQueryCount = 3;
		}

		public InProcessSingleQuery(String orderId, int preQueryCount, long delay, int maxAllowQueryCount) {
			this(orderId, preQueryCount, delay);
			this.maxAllowQueryCount = maxAllowQueryCount;
		}

		@Override
		public UnionpayCollection call() {
			UnionpayCollection unionpayCollection = UnionpayCollection.findByOrderId(orderId);
			if (allowSync(unionpayCollection)) {
				try {
					cCTradeSrv.syncOrderStatus(unionpayCollection);
				} catch (Exception e) {
					e.printStackTrace();
				}
				unionpayCollection = UnionpayCollection.findByOrderId(orderId);
				if (allowSync(unionpayCollection)) {
					int currentQueryCount = preQueryCount + 1;
					Integer queryResultCount = unionpayCollection.getQueryResultCount();
					if (currentQueryCount > maxAllowQueryCount
							|| (queryResultCount != null && queryResultCount > maxAllowQueryCount)) {
						return unionpayCollection;
					}
					long currentDelay = delay * 2;
					InProcessSingleQuery nextTask = new InProcessSingleQuery(orderId, currentQueryCount, currentDelay);
					SyncStatusExecutor.schedule(nextTask, currentDelay, TimeUnit.SECONDS);
				}
			}
			return unionpayCollection;
		}

		private static boolean allowSync(UnionpayCollection unionpayCollection) {
			if (unionpayCollection == null) {
				return false;
			}
			String respCode = unionpayCollection.getRespCode();
			String finalCode = unionpayCollection.getFinalCode();
			return "00".equals(respCode) && "1".equals(finalCode);
		}

	}

	public static class NotClearSingleQuery implements Callable<UnionpayCollection> {

		private static int maxAllowQueryCount = 2;

		private int preQueryCount;
		private String orderId;

		public NotClearSingleQuery(String orderId) {
			this.orderId = orderId;
			this.preQueryCount = 0;
		}

		private NotClearSingleQuery(String orderId, int preQueryCount) {
			this.orderId = orderId;
			this.preQueryCount = preQueryCount;
		}

		@Override
		public UnionpayCollection call() {
			UnionpayCollection unionpayCollection = UnionpayCollection.findByOrderId(orderId);
			if (allowSync(unionpayCollection)) {
				try {
					cCTradeSrv.syncOrderStatus(unionpayCollection);
				} catch (Exception e) {
					e.printStackTrace();
				}
				unionpayCollection = UnionpayCollection.findByOrderId(orderId);
				if (allowSync(unionpayCollection)) {
					int currentQueryCount = preQueryCount + 1;
					if (currentQueryCount == maxAllowQueryCount) {
						return unionpayCollection;
					}

					long currentDelay = UnionpayCollection.TIMEOUT_MINUTE;
					NotClearSingleQuery nextTask = new NotClearSingleQuery(orderId, currentQueryCount);
					SyncStatusExecutor.schedule(nextTask, currentDelay, TimeUnit.MINUTES);
				}
			}
			return unionpayCollection;
		}

		private static boolean allowSync(UnionpayCollection unionpayCollection) {
			if (unionpayCollection == null) {
				return false;
			}
			return "3".equals(unionpayCollection.getFinalCode());
		}

	}

	public static class NotClearBatchQuery implements Callable<UnionpayBatchCollection> {

		private static int maxAllowQueryCount = 2;

		private int preQueryCount;
		private String batchId;
		private String merId;
		private String batchNo;
		private String txnTime;

		public NotClearBatchQuery(String batchId, String merId, String batchNo, String txnTime) {
			this.batchId = batchId;
			this.merId = merId;
			this.batchNo = batchNo;
			this.txnTime = txnTime;
			this.preQueryCount = 0;
		}

		private NotClearBatchQuery(String batchId, String merId, String batchNo, String txnTime, int preQueryCount) {
			this.batchId = batchId;
			this.merId = merId;
			this.batchNo = batchNo;
			this.txnTime = txnTime;
			this.preQueryCount = preQueryCount;
		}

		@Override
		public UnionpayBatchCollection call() {
			UnionpayBatchCollection unionpayBatchCollection = UnionpayBatchCollection.findByIdOrBizColumn(batchId,
					merId, batchNo, txnTime);
			if (allowSync(unionpayBatchCollection)) {
				try {
					cBatchQuerySrv.batchQuery(unionpayBatchCollection);
				} catch (Exception e) {
					e.printStackTrace();
				}
				unionpayBatchCollection = UnionpayBatchCollection.findByIdOrBizColumn(batchId, merId, batchNo, txnTime);
				if (allowSync(unionpayBatchCollection)) {
					int currentQueryCount = preQueryCount + 1;
					if (currentQueryCount == maxAllowQueryCount) {
						return unionpayBatchCollection;
					}

					long currentDelay = UnionpayBatchCollection.TIMEOUT_MINUTE;
					NotClearBatchQuery nextTask = new NotClearBatchQuery(batchId, merId, batchNo, txnTime,
							currentQueryCount);
					SyncStatusExecutor.schedule(nextTask, currentDelay, TimeUnit.MINUTES);
				}
			}
			return unionpayBatchCollection;
		}

		private static boolean allowSync(UnionpayBatchCollection unionpayBatchCollection) {
			if (unionpayBatchCollection == null) {
				return false;
			}
			return "4".equals(unionpayBatchCollection.getFinalCode());
		}

	}

}
