package com.salesmanager.shop.product.model.inventory;

import com.salesmanager.shop.model.store.ReadableMerchantStore;
import com.salesmanager.shop.product.model.ReadableProductPrice;

import java.util.ArrayList;
import java.util.List;

public class ReadableInventory extends InventoryEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String creationDate;

    private ReadableMerchantStore store;
    private String sku;
    private List<ReadableProductPrice> prices = new ArrayList<ReadableProductPrice>();
    private String price;

    public ReadableMerchantStore getStore() {
        return store;
    }

    public void setStore(ReadableMerchantStore store) {
        this.store = store;
    }

    public List<ReadableProductPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<ReadableProductPrice> prices) {
        this.prices = prices;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}
