package com.salesmanager.shop.category.repository;


import com.salesmanager.shop.category.entity.Category;
import com.salesmanager.shop.commons.entity.merchant.MerchantStore;

import java.util.List;

public interface CategoryRepositoryCustom {

    List<Object[]> countProductsByCategories(MerchantStore store, List<Long> categoryIds);

    List<Category> listByStoreAndParent(MerchantStore store, Category category);

    List<Category> listByProduct(MerchantStore store, Long product);

}
