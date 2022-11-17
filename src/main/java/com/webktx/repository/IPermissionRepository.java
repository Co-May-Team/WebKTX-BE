package com.webktx.repository;

import java.util.List;

import com.webktx.entity.Permission;

public interface IPermissionRepository {
	List<Permission> findAll();
}
