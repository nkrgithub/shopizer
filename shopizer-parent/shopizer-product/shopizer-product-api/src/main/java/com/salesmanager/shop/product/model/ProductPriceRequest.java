package com.salesmanager.shop.product.model;

import com.salesmanager.shop.product.model.attribute.ProductAttribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductPriceRequest implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private List<ProductAttribute> options = new ArrayList<ProductAttribute>();
    private String sku;//product instance sku

    public List<ProductAttribute> getOptions() {
        return options;
    }

    public void setOptions(List<ProductAttribute> options) {
        this.options = options;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

}
