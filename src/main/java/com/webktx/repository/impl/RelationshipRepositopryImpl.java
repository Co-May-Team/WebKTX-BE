package com.webktx.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.webktx.entity.Relationship;
import com.webktx.repository.IRelationshipRepository;

@Repository
@Transactional
public class RelationshipRepositopryImpl implements IRelationshipRepository{
	@Autowired
	SessionFactory sessionFactory;
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public List<Relationship> findAll() {
		String hql = "FROM relationships";
		List<Relationship> relationShipList = new ArrayList<>();
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql);
			relationShipList = query.getResultList();
		} catch (Exception e) {
			LOGGER.debug(e.getStackTrace().toString());
		}
		return relationShipList;
	}

}
