package com.salesmanager.shop.product.model.attribute.api;


import com.salesmanager.shop.commons.model.ReadableList;

import java.util.ArrayList;
import java.util.List;

public class ReadableProductAttributeList extends ReadableList {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<ReadableProductAttributeEntity> attributes = new ArrayList<ReadableProductAttributeEntity>();

    public List<ReadableProductAttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ReadableProductAttributeEntity> attributes) {
        this.attributes = attributes;
    }

}
