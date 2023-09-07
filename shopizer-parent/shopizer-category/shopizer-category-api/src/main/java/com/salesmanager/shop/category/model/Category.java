package com.salesmanager.shop.category.model;


import com.salesmanager.shop.commons.model.entity.Entity;

import java.io.Serializable;


public class Category extends Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private CategoryDescription description;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public CategoryDescription getDescription() {
		return description;
	}
	public void setDescription(CategoryDescription description) {
		this.description = description;
	}
	
}
