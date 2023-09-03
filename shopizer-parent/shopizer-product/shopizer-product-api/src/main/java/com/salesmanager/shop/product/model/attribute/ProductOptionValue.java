package com.salesmanager.shop.product.model.attribute;


import com.salesmanager.shop.commons.model.Entity;

import java.io.Serializable;


public class ProductOptionValue extends Entity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String code;
    private String name;
    private boolean defaultValue;
    private int sortOrder;
    private String image;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
