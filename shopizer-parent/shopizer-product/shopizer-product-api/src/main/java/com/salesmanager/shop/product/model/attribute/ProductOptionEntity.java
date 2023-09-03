package com.salesmanager.shop.product.model.attribute;

import java.io.Serializable;

public class ProductOptionEntity extends ProductPropertyOption implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int order;

    private String type;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
