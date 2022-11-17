package com.webktx.repository;

import java.util.List;

import com.webktx.model.RoleDetailModel;

public interface IRoleRepository {
	RoleDetailModel findRoleDetailsByRoleId(Integer roleId);
	List<Integer> findAllRoleIdByUserId(Integer userId);
}
