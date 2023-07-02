package com.salesmanager.shop.security.service;

import com.salesmanager.shop.commons.exception.ServiceException;
import com.salesmanager.shop.commons.service.generic.SalesManagerEntityService;
import com.salesmanager.shop.security.entity.Group;
import com.salesmanager.shop.security.entity.Permission;
import com.salesmanager.shop.security.entity.PermissionCriteria;
import com.salesmanager.shop.security.entity.PermissionList;

import java.util.List;


public interface PermissionService extends SalesManagerEntityService<Integer, Permission> {

  List<Permission> getByName();

  List<Permission> listPermission() throws ServiceException;

  Permission getById(Integer permissionId);

  List<Permission> getPermissions(List<Integer> groupIds) throws ServiceException;

  void deletePermission(Permission permission) throws ServiceException;

  PermissionList listByCriteria(PermissionCriteria criteria) throws ServiceException;

  void removePermission(Permission permission, Group group) throws ServiceException;

}
