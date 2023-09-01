package com.salesmanager.shop.product.model.attribute;

import com.salesmanager.shop.product.model.attribute.api.ProductAttributeEntity;

import java.io.Serializable;
import java.math.BigDecimal;

public class PersistableProductAttribute extends ProductAttributeEntity
        implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private BigDecimal productAttributeWeight;
    private BigDecimal productAttributePrice;
    private Long productId;
    private ProductPropertyOption option;
    private PersistableProductOptionValue optionValue;

    public ProductPropertyOption getOption() {
        return option;
    }

    public void setOption(ProductPropertyOption option) {
        this.option = option;
    }

    public BigDecimal getProductAttributeWeight() {
        return productAttributeWeight;
    }

    public void setProductAttributeWeight(BigDecimal productAttributeWeight) {
        this.productAttributeWeight = productAttributeWeight;
    }

    public BigDecimal getProductAttributePrice() {
        return productAttributePrice;
    }

    public void setProductAttributePrice(BigDecimal productAttributePrice) {
        this.productAttributePrice = productAttributePrice;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public PersistableProductOptionValue getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(PersistableProductOptionValue optionValue) {
        this.optionValue = optionValue;
    }

}
