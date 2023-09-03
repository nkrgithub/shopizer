package com.salesmanager.shop.commons.entity.catalog.product.inventory;

import com.salesmanager.shop.commons.entity.catalog.product.price.FinalPrice;

import java.io.Serializable;

public class ProductInventory implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String sku;
	private long quantity;
	private FinalPrice price;
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public FinalPrice getPrice() {
		return price;
	}
	public void setPrice(FinalPrice price) {
		this.price = price;
	}

}
