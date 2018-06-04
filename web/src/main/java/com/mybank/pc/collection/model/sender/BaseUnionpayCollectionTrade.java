package com.mybank.pc.collection.model.sender;

public interface BaseUnionpayCollectionTrade {

	public BaseUnionpayCollectionTrade accept(SenderBuilder senderBuilder);

	public void setSendProxy(SendProxy sendProxy);

}
