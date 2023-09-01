package com.salesmanager.shop.product.model.attribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PersistableProductOption extends ProductOptionEntity implements
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private List<ProductOptionDescription> descriptions = new ArrayList<ProductOptionDescription>();

    public List<ProductOptionDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<ProductOptionDescription> descriptions) {
        this.descriptions = descriptions;
    }

}
