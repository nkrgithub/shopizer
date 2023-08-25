package com.salesmanager.core.business.repositories.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.salesmanager.core.model.user.User;

public interface PageableUserRepository extends PagingAndSortingRepository<User, Long> {
	
	  @Query(value = "select distinct u from User as u left join fetch u.groups ug left join fetch ug.permissions ugp left join fetch u.defaultLanguage ud join fetch u.merchantStore um where um.code=?1 and (u.adminEmail like %?2% or ?2 is null)",
	    countQuery = "select count(distinct u) from User as u join u.groups ug join ug.permissions ugp join u.merchantStore um where um.code=?1 and (u.adminEmail like %?2% or ?2 is null)")
	  Page<User> listByStore(String store, String email, Pageable pageable);
	
	  @Query(value = "select distinct u from User as u left join fetch u.groups ug left join fetch ug.permissions ugp left join fetch u.defaultLanguage ud join fetch u.merchantStore um where (u.adminEmail like %?1% or ?1 is null)",
		countQuery = "select count(distinct u) from User as u join u.groups ug join ug.permissions ugp join u.merchantStore um where (u.adminEmail like %?1% or ?1 is null)")
	  Page<User> listAll(String email, Pageable pageable);
	  
	  @Query(value = "select distinct u from User as u " +
			  "left join fetch u.groups ug " +
			  "left join fetch ug.permissions ugp " +
			  "left join fetch u.defaultLanguage ud " +
			  "join fetch u.merchantStore um where um.id in ?1 and (u.adminEmail like %?2% or ?2 is null)",
		countQuery = "select count(distinct u) from User as u join u.groups ug join ug.permissions ugp join u.merchantStore um where um.id in ?1 and (u.adminEmail like %?2% or ?2 is null)")
	  Page<User> listByStoreIds(List<Integer> stores, String email, Pageable pageable);
	

}
