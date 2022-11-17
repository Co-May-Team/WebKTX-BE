package com.webktx.repository.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.webktx.entity.Permission;
import com.webktx.repository.IPermissionRepository;

@Repository
@Transactional(rollbackFor = Exception.class)
public class PermissionRepositoryImpl implements IPermissionRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionRepositoryImpl.class);

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public List<Permission> findAll() {
		List<Permission> permissions = null;
		StringBuilder hql = new StringBuilder("FROM permissions p ");
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			LOGGER.info(hql.toString());
			permissions = new ArrayList<>();
			for (Iterator<?> it = query.getResultList().iterator(); it.hasNext();) {		
				Object obj = (Object) it.next();
				Permission category = (Permission) obj;
				permissions.add(category);
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured in findAll Category"+e, e);
		}
		return permissions;
	}

}
