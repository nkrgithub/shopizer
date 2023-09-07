package com.salesmanager.shop.category.model.product.attribute;


import com.salesmanager.shop.commons.model.entity.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReadableProductVariant extends Entity implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  //option name
  private String name;
  private String code;
  private List<ReadableProductVariantValue> options = new ArrayList<ReadableProductVariantValue>();

  public List<ReadableProductVariantValue> getOptions() {
    return options;
  }

  public void setOptions(List<ReadableProductVariantValue> options) {
    this.options = options;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

public String getCode() {
	return code;
}

public void setCode(String code) {
	this.code = code;
}



}
