package com.salesmanager.shop.product.model;

import com.salesmanager.shop.model.entity.ReadableList;

import java.util.ArrayList;
import java.util.List;

public class ReadableProductList extends ReadableList {


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<ReadableProduct> products = new ArrayList<ReadableProduct>();

    public List<ReadableProduct> getProducts() {
        return products;
    }

    public void setProducts(List<ReadableProduct> products) {
        this.products = products;
    }

}
