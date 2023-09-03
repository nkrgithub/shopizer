package com.salesmanager.shop.product.model.product.variantGroup;

import com.salesmanager.shop.product.model.ReadableImage;
import com.salesmanager.shop.product.model.product.variant.ReadableProductVariant;

import java.util.ArrayList;
import java.util.List;

public class ReadableProductVariantGroup extends ProductVariantGroup {

    private static final long serialVersionUID = 1L;

    List<ReadableImage> images = new ArrayList<ReadableImage>();

    private List<ReadableProductVariant> productVariants = new ArrayList<ReadableProductVariant>();

    public List<ReadableProductVariant> getproductVariants() {
        return productVariants;
    }

    public void setproductVariants(List<ReadableProductVariant> productVariants) {
        this.productVariants = productVariants;
    }

    public List<ReadableImage> getImages() {
        return images;
    }

    public void setImages(List<ReadableImage> images) {
        this.images = images;
    }

}
