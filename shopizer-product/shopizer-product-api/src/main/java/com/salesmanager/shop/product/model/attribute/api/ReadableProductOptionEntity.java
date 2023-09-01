package com.salesmanager.shop.product.model.attribute.api;

import com.salesmanager.shop.product.model.attribute.ProductOptionDescription;
import com.salesmanager.shop.product.model.attribute.ProductOptionEntity;

public class ReadableProductOptionEntity extends ProductOptionEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private ProductOptionDescription description;

    public ProductOptionDescription getDescription() {
        return description;
    }

    public void setDescription(ProductOptionDescription description) {
        this.description = description;
    }

}
