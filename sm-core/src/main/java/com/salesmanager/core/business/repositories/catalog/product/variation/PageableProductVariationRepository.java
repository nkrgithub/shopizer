package com.salesmanager.core.business.repositories.catalog.product.variation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.salesmanager.core.model.catalog.product.variation.ProductVariation;

public interface PageableProductVariationRepository extends PagingAndSortingRepository<ProductVariation, Long> {

	
	@Query(value = "select distinct p from ProductVariation p join fetch p.merchantStore pm "
			+ "left join fetch p.productOption po left join fetch po.descriptions "
			+ "left join fetch p.productOptionValue pp left join fetch pp.descriptions "
			+ "where pm.id = ?1 and (p.code like %?2% or ?2 is null)",
		    countQuery = "select count(p) from ProductVariation p join p.merchantStore pm where pm.id = ?1 and (p.code like %?2% or ?2 is null)")
	Page<ProductVariation> list(int merchantStoreId, String code, Pageable pageable);


}
