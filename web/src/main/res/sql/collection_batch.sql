#define ubcHeadColumn()
	id, txnType, txnSubType, bizType, channelType, accessType, merId, batchNo, txnTime, totalQty, totalAmt, respCode, respMsg, resultCode, resultMsg, finalCode, successAmt, successQty, status, sysQueryId, queryResultCount, nextQueryTime, cat, mat
#end

#sql("findUnionpayBatchCollectionHeadColumn")
	SELECT #@ubcHeadColumn?() FROM unionpay_batch_collection WHERE 1=1
		#if(id)
	        AND id = #para(id)
	    #end
		#if(txnType)
	        AND txnType = #para(txnType)
	    #end
		#if(txnSubType)
	        AND txnSubType = #para(txnSubType)
	    #end
		#if(merId)
	        AND merId = #para(merId)
	    #end
		#if(batchNo)
	        AND batchNo = #para(batchNo)
	    #end
	    #if(txnTime)
	        AND txnTime = #para(txnTime)
	    #end
		#if(finalCode)
	        AND finalCode = #para(finalCode)
	    #end
	    #if(bTime)
	    	AND DATE(cat) >= #para(bTime)
	    #end
	    #if(eTime)
	    	AND DATE(cat) <= #para(eTime)
	    #end
		ORDER BY
			mat DESC,cat DESC
#end
#sql("findUnionpayBatchCollection")
	SELECT * FROM unionpay_batch_collection WHERE 1=1
		#if(id)
	        AND id = #para(id)
	    #end
		#if(txnType)
	        AND txnType = #para(txnType)
	    #end
		#if(txnSubType)
	        AND txnSubType = #para(txnSubType)
	    #end
		#if(merId)
	        AND merId = #para(merId)
	    #end
		#if(batchNo)
	        AND batchNo = #para(batchNo)
	    #end
	    #if(txnTime)
	        AND txnTime = #para(txnTime)
	    #end
		#if(finalCode)
	        AND finalCode = #para(finalCode)
	    #end
	    #if(bTime)
	    	AND DATE(cat) >= #para(bTime)
	    #end
	    #if(eTime)
	    	AND DATE(cat) <= #para(eTime)
	    #end
		ORDER BY
			cat DESC
#end
#sql("findBatchNo")
	SELECT * FROM unionpay_batch_collection_batchno  WHERE 1=1
		AND txnDate = LEFT (#para(txnTime), 8)
		AND merId = #para(merId)
#end
#sql("updateBatchNo")
	UPDATE unionpay_batch_collection_batchno  SET batchNo = #para(newBatchNo) WHERE 1=1 
		AND txnDate = LEFT (#para(txnTime), 8)
		AND merId = #para(merId)
	    AND batchNo = #para(batchNo)
#end
#sql("updateToBeSentUnionpayCollectionBatchNo")
	UPDATE unionpay_collection  SET batchNo = #para(batchNo) , txnTime = #para(txnTime) , status = '1' , mat = #para(mat) WHERE 
	    txnType = '21' AND txnSubType = '02' AND status = '0' AND finalCode = '1'
	    #if(merId)
	    	AND merId = #para(merId)
	    #end
	    ORDER BY cat 
	    LIMIT 2000
#end
#sql("findToBeSentUnionpayCollectionByBatchNo")
	SELECT * FROM unionpay_collection  WHERE 
	    txnType = '21' AND txnSubType = '02' AND status = '1'
	    #if(txnTime)
	    	AND txnTime = #para(txnTime)
	    #end
	    #if(batchNo)
	    	AND batchNo = #para(batchNo)
	    #end
	    #if(merId)
	    	AND merId = #para(merId)
	    #end
#end
#sql("updateNeedQueryBatchCollectionPrepareOne")
	UPDATE unionpay_batch_collection  ubc SET sysQueryId = #para(sysQueryId) , status = '1' , mat = #para(mat) WHERE 
	    respCode = '00' AND finalCode = '1' 
	    AND (
	    	status = '0'
	    	OR status IS NULL
	    )
		AND (
			ubc.queryResultCount IS NULL
			OR ubc.queryResultCount <= 10
		)
		#if(txnTime)
	    	AND ubc.txnTime = #para(txnTime)
	    #end
	    #if(batchNo)
	    	AND ubc.batchNo = #para(batchNo)
	    #end
	    #if(merId)
	    	AND ubc.merId = #para(merId)
	    #end
#end
#sql("updateNeedQueryBatchCollectionPrepare")
	UPDATE unionpay_batch_collection  ubc SET sysQueryId = #para(sysQueryId) , status = '1' , mat = #para(mat) WHERE 
	    respCode = '00' AND finalCode = '1' 
	    AND (
	    	status = '0'
	    	OR status IS NULL
	    )
		AND (
			ubc.queryResultCount IS NULL
			OR ubc.queryResultCount <= 10
		)
		AND round(
			(
				UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(IFNULL(nextQueryTime,cat))
			) / 60
		) > IF(ISNULL(nextQueryTime), 120, 0)
#end
#sql("findNeedQueryBatchCollectionBySysQueryId")
	SELECT ubc.* FROM unionpay_batch_collection ubc WHERE
		sysQueryId = #para(sysQueryId)
#end
#sql("findUnionpayBatchCollectionQuery")
	SELECT * FROM unionpay_batch_collection_query  WHERE 1=1
	    #if(txnTime)
	    	AND txnTime = #para(txnTime)
	    #end
	    #if(batchNo)
	    	AND batchNo = #para(batchNo)
	    #end
	    #if(merId)
	    	AND merId = #para(merId)
	    #end
	    ORDER BY
			cat DESC
#end

