package com.salesmanager.shop.product.model.attribute.api;

import com.salesmanager.shop.product.model.attribute.ProductOptionValue;

import java.io.Serializable;

public class ProductOptionValueEntity extends ProductOptionValue implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int order;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


}
