package com.webktx.repository.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.webktx.entity.Option;
import com.webktx.entity.Permission;
import com.webktx.entity.Role;
import com.webktx.entity.RoleDetail;
import com.webktx.entity.User;
import com.webktx.model.OptionModel;
import com.webktx.model.PermissionModel;
import com.webktx.model.RoleDetailModel;
import com.webktx.repository.IRoleRepository;

@Repository
@Transactional
public class RoleRepositoryImpl implements IRoleRepository {

	private final static Logger LOGGER = LoggerFactory.getLogger(RoleRepositoryImpl.class);
	@Autowired
	SessionFactory sessionFactory;

	@Override
	public RoleDetailModel findRoleDetailsByRoleId(Integer roleId) {

		RoleDetailModel roleDetailModel = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			StringBuilder hql = new StringBuilder("FROM role_details as rol");
			hql.append(" INNER JOIN permissions AS per on rol.permissionId = per.permissionId");
			hql.append(" INNER JOIN options AS opt on rol.optionId = opt.optionId");
			hql.append(" INNER JOIN roles AS role on rol.roleId = role.roleId");
			hql.append(" WHERE rol.roleId = :roleId Order by rol.optionId ASC, rol.permissionId ASC");
			Query query = session.createQuery(hql.toString());
			query.setParameter("roleId", roleId);

			roleDetailModel = new RoleDetailModel();
			List<OptionModel> optionModels = new ArrayList<OptionModel>();
			List<PermissionModel> pemissionModels = new ArrayList<PermissionModel>();
			OptionModel optionModel = new OptionModel();
			Integer optionIdTem = 0;
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {

				RoleDetail roleDetailTemp = new RoleDetail();
				Permission permissionTemp = new Permission();
				Option optionTemp = new Option();
				Role role = new Role();
				Object[] obj = (Object[]) it.next();
				roleDetailTemp = (RoleDetail) obj[0];
				permissionTemp = (Permission) obj[1];
				optionTemp = (Option) obj[2];
				role = (Role) obj[3];
				if (optionIdTem == 0) {
					optionModel.setId(optionTemp.getOptionId());
					optionModel.setName(optionTemp.getOptionName());
					roleDetailModel.setId(roleDetailTemp.getRoleId());
					roleDetailModel.setName(role.getRoleName());
					PermissionModel permissionModel = new PermissionModel();
					permissionModel.setId(permissionTemp.getPermissionId());
					permissionModel.setName(permissionTemp.getPermissionName());
					pemissionModels.add(permissionModel);
				} else {
					if (optionIdTem != optionTemp.getOptionId()) {
						optionModel.setPermissions(pemissionModels);
						optionModels.add(optionModel);
						optionModel = new OptionModel();
						pemissionModels = new ArrayList<PermissionModel>();
						optionModel.setId(optionTemp.getOptionId());
						optionModel.setName(optionTemp.getOptionName());
						PermissionModel permissionModel = new PermissionModel();
						permissionModel.setId(permissionTemp.getPermissionId());
						permissionModel.setName(permissionTemp.getPermissionName());
						pemissionModels.add(permissionModel);
					} else {
						PermissionModel permissionModel = new PermissionModel();
						permissionModel.setId(permissionTemp.getPermissionId());
						permissionModel.setName(permissionTemp.getPermissionName());
						pemissionModels.add(permissionModel);
					}
				}
				optionIdTem = optionTemp.getOptionId();
			}

			roleDetailModel.setOptions(optionModels);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return roleDetailModel;
	}

	@Override
	public List<Integer> findAllRoleIdByUserId(Integer userId) {
		List<Integer> ids = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			StringBuilder hql = new StringBuilder("FROM users AS us WHERE us.userId = :userId");
			Query query = session.createQuery(hql.toString());
			query.setParameter("userId", userId);
			ids = new ArrayList<>();
			for (Iterator<?> it = query.getResultList().iterator(); it.hasNext();) {
				User user =  (User) it.next();
				ids.add(user.getRole().getRoleId());
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return ids;
	}

}
