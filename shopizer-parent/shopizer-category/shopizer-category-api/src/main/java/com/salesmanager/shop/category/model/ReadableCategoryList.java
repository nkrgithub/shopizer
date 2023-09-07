package com.salesmanager.shop.category.model;


import com.salesmanager.shop.commons.model.entity.ReadableList;

import java.util.ArrayList;
import java.util.List;

public class ReadableCategoryList extends ReadableList {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private List<ReadableCategory> categories = new ArrayList<ReadableCategory>();
  public List<ReadableCategory> getCategories() {
    return categories;
  }
  public void setCategories(List<ReadableCategory> categories) {
    this.categories = categories;
  }

}
