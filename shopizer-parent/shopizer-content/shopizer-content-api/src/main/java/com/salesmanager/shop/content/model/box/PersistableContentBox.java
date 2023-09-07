package com.salesmanager.shop.content.model.box;


import com.salesmanager.shop.content.model.common.ContentDescription;

import java.util.List;

public class PersistableContentBox extends ContentBox {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
	private List<ContentDescription> descriptions;

	public List<ContentDescription> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<ContentDescription> descriptions) {
		this.descriptions = descriptions;
	}

}
