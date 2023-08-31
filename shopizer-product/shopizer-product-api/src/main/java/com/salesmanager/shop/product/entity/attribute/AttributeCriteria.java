package com.salesmanager.shop.product.entity.attribute;

import java.io.Serializable;

public class AttributeCriteria implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String attributeCode;
    private String attributeValue;

    public String getAttributeCode() {
        return attributeCode;
    }

    public void setAttributeCode(String attributeCode) {
        this.attributeCode = attributeCode;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

}
