package com.salesmanager.shop.commons.entity.catalog.marketplace;

import com.salesmanager.shop.commons.entity.common.audit.AuditSection;
import com.salesmanager.shop.commons.entity.common.audit.Auditable;
import com.salesmanager.shop.commons.entity.generic.SalesManagerEntity;
import com.salesmanager.shop.commons.entity.merchant.MerchantStore;

import javax.persistence.Embedded;
import java.util.ArrayList;
import java.util.List;

/**
 * A catalog is used to classify products of a given merchant
 * to be displayed in a specific marketplace
 * @author c.samson
 *
 */
public class Catalog extends SalesManagerEntity<Long, Catalog> implements Auditable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private MerchantStore store;
	
	private String code;
	
	private List<CatalogDescription> descriptions = new ArrayList<CatalogDescription>();
	
	@Embedded
	private AuditSection auditSection = new AuditSection();

	@Override
	public AuditSection getAuditSection() {
		return auditSection;
	}

	@Override
	public void setAuditSection(AuditSection audit) {
		this.auditSection = auditSection;	
	}


	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	public MerchantStore getStore() {
		return store;
	}

	public void setStore(MerchantStore store) {
		this.store = store;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<CatalogDescription> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<CatalogDescription> descriptions) {
		this.descriptions = descriptions;
	}

}
