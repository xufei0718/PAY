package com.mybank.pc.exception;

import com.mybank.pc.collection.model.CollectionTrade;
import com.mybank.pc.collection.model.UnionpayCollection;

public class TradeRuntimeException extends BaseCollectionRuntimeException {

	private static final long serialVersionUID = 1L;

	private CollectionTrade collectionTrade;

	private UnionpayCollection unionpayCollection;

	public TradeRuntimeException() {
		super();
	}

	public TradeRuntimeException(String message) {
		super(message);
	}

	public TradeRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public TradeRuntimeException(Throwable cause) {
		super(cause);
	}

	protected TradeRuntimeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CollectionTrade getCollectionTrade() {
		return collectionTrade;
	}

	public void setCollectionTrade(CollectionTrade collectionTrade) {
		this.collectionTrade = collectionTrade;
	}

	public UnionpayCollection getUnionpayCollection() {
		return unionpayCollection;
	}

	public void setUnionpayCollection(UnionpayCollection unionpayCollection) {
		this.unionpayCollection = unionpayCollection;
	}

}
