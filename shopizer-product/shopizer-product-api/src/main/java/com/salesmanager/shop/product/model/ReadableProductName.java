package com.salesmanager.shop.product.model;

import com.salesmanager.shop.product.model.product.ProductEntity;

public class ReadableProductName extends ProductEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
