package com.salesmanager.shop.user.reposistory;


import com.salesmanager.shop.user.entity.PermissionCriteria;
import com.salesmanager.shop.user.entity.PermissionList;

public interface PermissionRepositoryCustom {

	PermissionList listByCriteria(PermissionCriteria criteria);

}
