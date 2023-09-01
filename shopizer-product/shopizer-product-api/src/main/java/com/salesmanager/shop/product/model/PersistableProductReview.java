package com.salesmanager.shop.product.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class PersistableProductReview extends ProductReviewEntity implements
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @NotNull
    private Long customerId;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }


}
