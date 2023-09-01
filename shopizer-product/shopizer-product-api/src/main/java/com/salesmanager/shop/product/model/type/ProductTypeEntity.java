package com.salesmanager.shop.product.model.type;

import com.salesmanager.shop.model.entity.Entity;

import java.io.Serializable;

public class ProductTypeEntity extends Entity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    boolean allowAddToCart;
    private String code;
    private boolean visible;

    public boolean isAllowAddToCart() {
        return allowAddToCart;
    }

    public void setAllowAddToCart(boolean allowAddToCart) {
        this.allowAddToCart = allowAddToCart;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }


}
