package com.salesmanager.shop.product.model.attribute.api;

import com.salesmanager.shop.commons.model.ReadableList;

import java.util.ArrayList;
import java.util.List;

public class ReadableProductOptionList extends ReadableList {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<ReadableProductOptionEntity> options = new ArrayList<ReadableProductOptionEntity>();

    public List<ReadableProductOptionEntity> getOptions() {
        return options;
    }

    public void setOptions(List<ReadableProductOptionEntity> options) {
        this.options = options;
    }

}
