package com.salesmanager.shop.commons.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class UniqueEntity implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @NotNull
  private String unique;
  @NotNull
  private String merchant;

  public String getUnique() {
    return unique;
  }

  public void setUnique(String unique) {
    this.unique = unique;
  }

  public String getMerchant() {
    return merchant;
  }

  public void setMerchant(String merchant) {
    this.merchant = merchant;
  }

}
