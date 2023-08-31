package com.salesmanager.shop.product.service;

import com.salesmanager.core.model.tax.taxclass.TaxClass;
import com.salesmanager.shop.commons.entity.language.Language;
import com.salesmanager.shop.commons.entity.merchant.MerchantStore;
import com.salesmanager.shop.commons.exception.ServiceException;
import com.salesmanager.shop.commons.service.generic.SalesManagerEntityService;
import com.salesmanager.shop.product.entity.Product;
import com.salesmanager.shop.product.entity.ProductCriteria;
import com.salesmanager.shop.product.entity.ProductList;
import com.salesmanager.shop.product.entity.description.ProductDescription;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Locale;
import java.util.Optional;


public interface ProductService extends SalesManagerEntityService<Long, Product> {

    Optional<Product> retrieveById(Long id, MerchantStore store);

    void addProductDescription(Product product, ProductDescription description) throws ServiceException;

    ProductDescription getProductDescription(Product product, Language language);

    Product getProductForLocale(long productId, Language language, Locale locale) throws ServiceException;

    List<Product> getProductsForLocale(Category category, Language language, Locale locale) throws ServiceException;

    List<Product> getProducts(List<Long> categoryIds) throws ServiceException;

    List<Product> getProductsByIds(List<Long> productIds) throws ServiceException;

    /**
     * The method to be used
     *
     * @param product
     * @return
     * @throws ServiceException
     */
    Product saveProduct(Product product) throws ServiceException;

    /**
     * Get a product with only MerchantStore object
     *
     * @param productId
     * @return
     */
    Product getProductWithOnlyMerchantStoreById(Long productId);

    ProductList listByStore(MerchantStore store, Language language, ProductCriteria criteria);

    boolean exists(String sku, MerchantStore store);


    /**
     * List using Page interface in order to unify all page requests (since 2.16.0)
     *
     * @param store
     * @param language
     * @param criteria
     * @param page
     * @param count
     * @return
     */
    Page<Product> listByStore(MerchantStore store, Language language,
                              ProductCriteria criteria, int page, int count);

    List<Product> listByStore(MerchantStore store);

    List<Product> listByTaxClass(TaxClass taxClass);

    List<Product> getProducts(List<Long> categoryIds, Language language)
            throws ServiceException;

    Product getBySeUrl(MerchantStore store, String seUrl, Locale locale);

    /**
     * Product and or product variant
     *
     * @param productCode
     * @param merchant
     * @return
     */
    Product getBySku(String productCode, MerchantStore merchant, Language language) throws ServiceException;


    Product getBySku(String productCode, MerchantStore merchant) throws ServiceException;

    /**
     * Find a product for a specific merchant
     *
     * @param id
     * @param merchant
     * @return
     */
    Product findOne(Long id, MerchantStore merchant);


}

