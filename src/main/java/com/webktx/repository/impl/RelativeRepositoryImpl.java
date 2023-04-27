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

import com.webktx.entity.Person;
import com.webktx.entity.Post;
import com.webktx.entity.Relative;
import com.webktx.repository.IRelativeRepository;
@Repository
@Transactional(rollbackFor = Exception.class)
public class RelativeRepositoryImpl implements IRelativeRepository{
	private static final Logger LOGGER = LoggerFactory.getLogger(RelativeRepositoryImpl.class);

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public Integer add(Relative relative) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Integer id = (Integer) session.save(relative);
			return id;
		} catch (Exception e) {
			LOGGER.error("Error has occured at add() ", e);
		}
		return -1;
	}

	@Override
	public Integer edit(Relative relative) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.update(relative);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured at edit() ", e);
			return 0;
		}
	}

	@Override
	public Integer deleteById(Integer relativeId) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Relative relative = session.find(Relative.class, relativeId);
			session.remove(relative);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured in delete() ", e);
			return 0;
		}
	}
	@Override
	public List<Relative> findByUserId(Integer userId) {
		List<Relative> relativeList = new ArrayList<>();
		try {
			Session session = sessionFactory.getCurrentSession();
			String hql = "FROM relatives rl WHERE rl.user.userId = :userId";
			Query query = session.createQuery(hql);
			query.setParameter("userId", userId);
			relativeList = query.getResultList();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return relativeList;
	}
}
