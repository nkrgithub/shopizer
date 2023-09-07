package com.salesmanager.shop.commons.entity.system;

import com.salesmanager.shop.commons.entity.common.audit.AuditListener;
import com.salesmanager.shop.commons.entity.generic.SalesManagerEntity;
import com.salesmanager.shop.commons.entity.merchant.MerchantStore;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import java.io.Serializable;

@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "MERCHANT_LOG")
public class MerchantLog extends SalesManagerEntity<Long, MerchantLog> implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "MERCHANT_LOG_ID")
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "MR_LOG_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MERCHANT_ID", nullable=false)
	private MerchantStore store;

	@Column(name="MODULE", length=25, nullable=true)
	private String module;
	

	@Column(name="LOG")
	@Type(type = "org.hibernate.type.TextType")
	private String log;
	
	public MerchantLog(MerchantStore store, String log) {
		this.store = store;
		this.log = log;
	}
	
	public MerchantLog(MerchantStore store, String module, String log) {
		this.store = store;
		this.module = module;
		this.log = log;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public MerchantStore getStore() {
		return store;
	}


	public void setStore(MerchantStore store) {
		this.store = store;
	}


	public String getModule() {
		return module;
	}


	public void setModule(String module) {
		this.module = module;
	}


	public String getLog() {
		return log;
	}


	public void setLog(String log) {
		this.log = log;
	}


}