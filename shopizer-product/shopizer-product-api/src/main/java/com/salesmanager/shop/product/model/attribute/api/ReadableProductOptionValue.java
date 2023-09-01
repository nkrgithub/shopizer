package com.salesmanager.shop.product.model.attribute.api;

import com.salesmanager.shop.product.model.attribute.ProductOptionValueDescription;

public class ReadableProductOptionValue extends ProductOptionValueEntity {

    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private String price;
    private ProductOptionValueDescription description;

    public ProductOptionValueDescription getDescription() {
        return description;
    }

    public void setDescription(ProductOptionValueDescription description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
