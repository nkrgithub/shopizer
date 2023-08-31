package com.salesmanager.shop.commons.model;

import java.util.List;

public class ReadableEntityList<T> extends ReadableList {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<T> items;

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

}
