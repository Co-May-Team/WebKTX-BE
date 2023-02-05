package com.webktx.repository.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.webktx.entity.Tag;
import com.webktx.repository.ITagRepository;
@Repository
@Transactional(rollbackFor = Exception.class)
public class TagRepositoryImpl implements ITagRepository {


	private static final Logger LOGGER = LoggerFactory.getLogger(TagRepositoryImpl.class);

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public Tag findById(Integer id) {
		String hql = "FROM tags AS t WHERE t.tagId = :id";
		Tag tag = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			query.setParameter("id",id);
			tag = (Tag) query.getSingleResult();
		} catch (Exception e) {
			LOGGER.error("Error has occured in Impl findById (tag API): ",e);
		}
		return tag;
	}

	@Override
	public List<Tag> findAll() {
		String hql = "FROM tags ";
		List<Tag> tags = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			tags = new ArrayList<>();
			for (Iterator<?> it = query.getResultList().iterator(); it.hasNext();) {
				Tag tag = (Tag) it.next();
				tags.add(tag);
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured in Impl findById (tag API): ",e);
		}
		return tags;
	}

}
