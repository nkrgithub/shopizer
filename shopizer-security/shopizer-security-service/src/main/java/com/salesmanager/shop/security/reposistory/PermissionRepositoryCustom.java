package com.salesmanager.shop.security.reposistory;

import com.salesmanager.shop.security.entity.PermissionCriteria;
import com.salesmanager.shop.security.entity.PermissionList;


public interface PermissionRepositoryCustom {

	PermissionList listByCriteria(PermissionCriteria criteria);

}
