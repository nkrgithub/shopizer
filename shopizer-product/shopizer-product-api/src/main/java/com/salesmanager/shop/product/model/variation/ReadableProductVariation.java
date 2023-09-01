package com.salesmanager.shop.product.model.variation;

import com.salesmanager.shop.product.model.attribute.ReadableProductOption;
import com.salesmanager.shop.product.model.attribute.ReadableProductOptionValue;

public class ReadableProductVariation extends ProductVariationEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    ReadableProductOption option = null;
    ReadableProductOptionValue optionValue = null;

    public ReadableProductOption getOption() {
        return option;
    }

    public void setOption(ReadableProductOption option) {
        this.option = option;
    }

    public ReadableProductOptionValue getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(ReadableProductOptionValue optionValue) {
        this.optionValue = optionValue;
    }

}
