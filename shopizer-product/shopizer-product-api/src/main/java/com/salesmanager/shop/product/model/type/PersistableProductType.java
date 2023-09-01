package com.salesmanager.shop.product.model.type;

import java.util.List;

public class PersistableProductType extends ProductTypeEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private List<ProductTypeDescription> descriptions;

    public List<ProductTypeDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<ProductTypeDescription> descriptions) {
        this.descriptions = descriptions;
    }

}
