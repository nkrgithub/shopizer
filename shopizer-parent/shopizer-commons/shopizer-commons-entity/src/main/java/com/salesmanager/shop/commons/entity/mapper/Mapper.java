package com.salesmanager.shop.commons.entity.mapper;


import com.salesmanager.shop.commons.entity.merchant.MerchantStore;
import com.salesmanager.shop.commons.entity.reference.language.Language;

public interface Mapper<S, T> {

  T convert(S source, MerchantStore store, Language language);
  T merge(S source, T destination, MerchantStore store, Language language);
  

}
