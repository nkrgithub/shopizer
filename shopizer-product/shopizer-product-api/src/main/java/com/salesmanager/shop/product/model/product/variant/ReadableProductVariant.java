package com.salesmanager.shop.product.model.product.variant;

import com.salesmanager.shop.product.model.ReadableImage;
import com.salesmanager.shop.product.model.inventory.ReadableInventory;
import com.salesmanager.shop.product.model.variation.ReadableProductVariation;

import java.util.ArrayList;
import java.util.List;

public class ReadableProductVariant extends ProductVariant {

    private static final long serialVersionUID = 1L;

    private ReadableProductVariation variation;
    private ReadableProductVariation variationValue;
    private String code;
    private List<ReadableImage> images = new ArrayList<ReadableImage>();
    private List<ReadableInventory> inventory = new ArrayList<ReadableInventory>();

    public ReadableProductVariation getVariation() {
        return variation;
    }

    public void setVariation(ReadableProductVariation variation) {
        this.variation = variation;
    }

    public ReadableProductVariation getVariationValue() {
        return variationValue;
    }

    public void setVariationValue(ReadableProductVariation variationValue) {
        this.variationValue = variationValue;
    }

    public List<ReadableImage> getImages() {
        return images;
    }

    public void setImages(List<ReadableImage> images) {
        this.images = images;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ReadableInventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<ReadableInventory> inventory) {
        this.inventory = inventory;
    }

}
