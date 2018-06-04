package com.mybank.pc.collection.model;

import com.mybank.pc.collection.model.base.BaseCollectionCleartotle;
import com.mybank.pc.kits.DateKit;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class CollectionCleartotle extends BaseCollectionCleartotle<CollectionCleartotle> {
	public static final CollectionCleartotle dao = new CollectionCleartotle().dao();
	public String getProfit(){
		return getBigDecimal("profit")==null?"0":getBigDecimal("profit").toString();
	}

	public String getCleartotleTimeTxt(){
		return DateKit.dateToStr(getCleartotleTime(),DateKit.yyyy_MM_dd);
	}


}