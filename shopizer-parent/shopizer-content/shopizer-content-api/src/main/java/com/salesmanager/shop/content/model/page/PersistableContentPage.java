package com.salesmanager.shop.content.model.page;


import com.salesmanager.shop.content.model.common.ContentDescription;

import java.util.List;

public class PersistableContentPage extends ContentPage {

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
