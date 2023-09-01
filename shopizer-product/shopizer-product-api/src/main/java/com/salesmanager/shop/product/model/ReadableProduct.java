package com.salesmanager.shop.product.model;

import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.model.catalog.manufacturer.ReadableManufacturer;
import com.salesmanager.shop.product.model.attribute.ReadableProductAttribute;
import com.salesmanager.shop.product.model.attribute.ReadableProductOption;
import com.salesmanager.shop.product.model.attribute.ReadableProductProperty;
import com.salesmanager.shop.product.model.product.ProductEntity;
import com.salesmanager.shop.product.model.product.variant.ReadableProductVariant;
import com.salesmanager.shop.product.model.type.ReadableProductType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReadableProduct extends ProductEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private ProductDescription description;
    private ReadableProductPrice productPrice;
    private String finalPrice = "0";
    private String originalPrice = null;
    private boolean discounted = false;
    private ReadableImage image;
    private List<ReadableImage> images = new ArrayList<ReadableImage>();
    private ReadableManufacturer manufacturer;
    private List<ReadableProductAttribute> attributes = new ArrayList<ReadableProductAttribute>();
    private List<ReadableProductOption> options = new ArrayList<ReadableProductOption>();
    private List<ReadableProductVariant> variants = new ArrayList<ReadableProductVariant>();
    private List<ReadableProductProperty> properties = new ArrayList<ReadableProductProperty>();
    private List<ReadableCategory> categories = new ArrayList<ReadableCategory>();
    private ReadableProductType type;
    private boolean canBePurchased = false;

    // RENTAL
    private RentalOwner owner;

    public ProductDescription getDescription() {
        return description;
    }

    public void setDescription(ProductDescription description) {
        this.description = description;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public boolean isDiscounted() {
        return discounted;
    }

    public void setDiscounted(boolean discounted) {
        this.discounted = discounted;
    }

    public List<ReadableImage> getImages() {
        return images;
    }

    public void setImages(List<ReadableImage> images) {
        this.images = images;
    }

    public ReadableImage getImage() {
        return image;
    }

    public void setImage(ReadableImage image) {
        this.image = image;
    }

    public List<ReadableProductAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ReadableProductAttribute> attributes) {
        this.attributes = attributes;
    }

    public ReadableManufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(ReadableManufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public boolean isCanBePurchased() {
        return canBePurchased;
    }

    public void setCanBePurchased(boolean canBePurchased) {
        this.canBePurchased = canBePurchased;
    }

    public RentalOwner getOwner() {
        return owner;
    }

    public void setOwner(RentalOwner owner) {
        this.owner = owner;
    }

    public List<ReadableCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<ReadableCategory> categories) {
        this.categories = categories;
    }

    public List<ReadableProductOption> getOptions() {
        return options;
    }

    public void setOptions(List<ReadableProductOption> options) {
        this.options = options;
    }

    public ReadableProductType getType() {
        return type;
    }

    public void setType(ReadableProductType type) {
        this.type = type;
    }

    public ReadableProductPrice getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(ReadableProductPrice productPrice) {
        this.productPrice = productPrice;
    }

    public List<ReadableProductProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<ReadableProductProperty> properties) {
        this.properties = properties;
    }

    public List<ReadableProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ReadableProductVariant> variants) {
        this.variants = variants;
    }


}
