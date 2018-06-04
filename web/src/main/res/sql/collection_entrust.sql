#sql("findCollectionEntrustPage")
	SELECT DISTINCT ce.* FROM collection_entrust ce INNER JOIN merchant_cust mc on ce.certifId = mc.cardID WHERE 1=1
		 #if(merID)
	    	AND mc.merID = #para(merID)
	    #end
	    #if(search)
	        AND (instr(customerNm, #para(search) )>0 OR instr(certifId, #para(search) )>0 OR instr(accNo, #para(search) )>0 OR instr(phoneNo, #para(search) )>0) 
	    #end
	    #if(bTime)
	    	AND DATE(cat) >= #para(bTime)
	    #end
	    #if(eTime)
	    	AND DATE(cat) <= #para(eTime)
	    #end
	    #if(txnType)
	    	AND txnType = #para(txnType)
	    #end
	    #if(txnSubType)
	    	AND txnSubType = #para(txnSubType)
	    #end
	  ORDER BY mat DESC,cat DESC
#end
#sql("findUnionpayEntrustPage")
	SELECT * FROM unionpay_entrust  WHERE 1=1
	    #if(search)
	        AND (instr(customerNm, #para(search))>0 OR instr(certifId, #para(search))>0 OR instr(accNo, #para(search))>0 OR instr(phoneNo, #para(search))>0)
	    #end
	    #if(bTime)
	    	AND DATE(cat) >= #para(bTime)
	    #end
	    #if(eTime)
	    	AND DATE(cat) <= #para(eTime)
	    #end
	    #if(txnType)
	    	AND txnType = #para(txnType)
	    #end
	    #if(txnSubType)
	    	AND txnSubType = #para(txnSubType)
	    #end
	    #if(merchantID)
	    	AND merchantID = #para(merchantID)
	    #end
	  ORDER BY mat DESC,cat DESC
#end
#sql("findOne")
	SELECT * FROM collection_entrust ce WHERE 1=1
	    #if(customerNm)
	        AND customerNm = #para(customerNm)
	    #end
	    #if(certifId)
	        AND certifId = #para(certifId)
	    #end
	    #if(accNo)
	        AND accNo = #para(accNo)
	    #end
	    #if(merId)
	        AND merId = #para(merId)
	    #end
#end
#sql("findUnionpayOne")
	SELECT ue.* FROM unionpay_entrust ue WHERE 1=1
		#if(version)
	        AND version = #para(version)
	    #end
	    #if(txnType)
	        AND txnType = #para(txnType)
	    #end
	    #if(txnSubType)
	        AND txnSubType = #para(txnSubType)
	    #end
	    #if(accNo)
	        AND accNo = #para(accNo)
	    #end
	    #if(customerNm)
	        AND customerNm = #para(customerNm)
	    #end
	    #if(certifId)
	        AND certifId = #para(certifId)
	    #end
	    #if(phoneNo)
	        AND phoneNo = #para(phoneNo)
	    #end
	    #if(merId)
	        AND merId = #para(merId)
	    #end
	    #if(finalCode)
	        AND finalCode = #para(finalCode)
	    #end
	    ORDER BY mat DESC
       LIMIT 1
#end