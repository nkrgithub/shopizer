package com.salesmanager.shop.product.model.attribute;

import java.util.ArrayList;
import java.util.List;

public class ReadableProductPropertyValue extends ProductOptionValue {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<ProductOptionValueDescription> values = new ArrayList<ProductOptionValueDescription>();

    public List<ProductOptionValueDescription> getValues() {
        return values;
    }

    public void setValues(List<ProductOptionValueDescription> values) {
        this.values = values;
    }


}
