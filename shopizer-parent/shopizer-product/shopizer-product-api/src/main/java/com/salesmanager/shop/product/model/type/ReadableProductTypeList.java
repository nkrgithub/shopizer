package com.salesmanager.shop.product.model.type;

import com.salesmanager.shop.model.entity.ReadableList;

import java.util.ArrayList;
import java.util.List;

public class ReadableProductTypeList extends ReadableList {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    List<ReadableProductType> list = new ArrayList<ReadableProductType>();

    public List<ReadableProductType> getList() {
        return list;
    }

    public void setList(List<ReadableProductType> list) {
        this.list = list;
    }

}
