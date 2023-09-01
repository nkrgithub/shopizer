package com.salesmanager.shop.product.model.variation;

import com.salesmanager.shop.model.entity.Entity;

public class ProductVariationEntity extends Entity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String code;//sku
    private String date;
    private int sortOrder;
    private boolean defaultValue = false;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
