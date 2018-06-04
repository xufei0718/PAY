#sql("findMerchantCustOne")
	SELECT * FROM merchant_cust WHERE 1=1
		#if(merID)
	        AND merID = #para(merID)
	    #end
	    #if(merNo)
	        AND merNo = #para(merNo)
	    #end
	        AND custName = #para(custName)
	        AND cardID = #para(cardID)
	        AND mobileBank = #para(mobileBank)
	        AND bankcardNo = #para(bankcardNo)
		ORDER BY
			mat DESC,cat DESC
#end


