package com.salesmanager.shop.product.model.attribute;

import com.salesmanager.shop.product.model.attribute.api.ProductOptionValueEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PersistableProductOptionValue extends ProductOptionValueEntity
        implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private List<ProductOptionValueDescription> descriptions = new ArrayList<ProductOptionValueDescription>();

    public List<ProductOptionValueDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<ProductOptionValueDescription> descriptions) {
        this.descriptions = descriptions;
    }

}
