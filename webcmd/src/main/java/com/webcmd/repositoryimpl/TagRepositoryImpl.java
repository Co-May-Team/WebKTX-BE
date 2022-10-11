package com.webcmd.repositoryimpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.webcmd.entity.Post;
import com.webcmd.entity.Tag;
import com.webcmd.model.TagModel;
import com.webcmd.repository.ITagRepository;
@Repository
@Transactional(rollbackFor = Exception.class)
public class TagRepositoryImpl implements ITagRepository{
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DataSource dataSource;
	@Autowired
	private SessionFactory sessionFactory;


	@Override
	public  Tag findById(Integer id) {
		Tag customTag = new  Tag();
		Set<Post> p = new LinkedHashSet<>();;
		StringBuilder hql = new StringBuilder("FROM  tags AS t");
		hql.append(" JOIN t.tagId p ");
		hql.append(" WHERE t.tagId = :id");
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			query.setParameter("id",id);
			LOGGER.info(hql.toString());
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {		
				Object[] obj = (Object[]) it.next();
				Tag tag = (Tag) obj[0];
				customTag.setTagId(tag.getTagId());
				customTag.setTagName(tag.getTagName());
				customTag.setCreatedAt(tag.getCreatedAt());
				customTag.setUpdatedAt(tag.getUpdatedAt());
//				customTag.setListPosts(p);
			}
		}
		catch (Exception e) {
			LOGGER.error("ERROR! An error occurred in  TagRepositoryImpl | findById "+ e, e);
		}
		return customTag;
	}
	@Override
	@Transactional
	public List<TagModel> findAll(String tag_name, String sort, String order, Integer offset, Integer limit) {
		List<TagModel> customTagList = new ArrayList<TagModel>();
		Set<Tag> tagSet = new LinkedHashSet<Tag>();
		StringBuilder hql = new StringBuilder("FROM tags c ");
		hql.append(" WHERE c.tagName LIKE CONCAT('%',:tagName,'%')");
		//hql.append(" order by p." + sort + " " + order );
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			LOGGER.info(hql.toString());
			query.setParameter("tagName", tag_name);
//			query.setParameter("order", order);
//			query.setParameter("sort", sort);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {		
				Object obj = (Object) it.next();
				Tag tag = (Tag) obj;
				tagSet.add(tag);
			}
			for (Tag tag : tagSet) {
				TagModel customTag = new TagModel();
				customTag.setTagId(tag.getTagId());
				customTag.setTagName(tag.getTagName());
				customTag.setCreatedAt(tag.getCreatedAt());
				customTag.setUpdatedAt(tag.getUpdatedAt());
				customTagList.add(customTag);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR! An error occurred in  TagRepositoryImpl | findAll "+ e, e);
		}
		return customTagList;
	}
	//count 
	@Override
	public Integer countAllPaging( String tag_name) {
		Set<Tag> tagSet = new LinkedHashSet<Tag>();
		StringBuilder hql = new StringBuilder("FROM tags c ");
		hql.append(" WHERE c.tagName LIKE CONCAT('%',:tagName,'%')");
		Session session = this.sessionFactory.getCurrentSession();
		try {
			Query query = session.createQuery(hql.toString());			
			query.setParameter("tagName", tag_name);
			LOGGER.info(hql.toString());
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {
				Object ob = (Object) it.next();
				tagSet.add((Tag) ob);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR! An error occurred in  TagRepositoryImpl | count total "+ e, e);	
		}
		
		return tagSet.size();		
	}
	//insert 
	@Override
	public Integer insert(Tag tag) {
			try {
				LOGGER.info("SAVE CATEGORY....");
				Session session = sessionFactory.getCurrentSession();
				session.save(tag);
				session.flush();
				return 1;
			} catch (Exception e) {
				LOGGER.error("ERROR! An error occurred in  TagRepositoryImpl | insert "+ e, e);
				return 0;
			}
		}
	//edit
	@Override
	@Transactional
	public Integer edit(Tag tag) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.update(tag);
			return 1;
		} catch (Exception e) {
			LOGGER.error("ERROR! An error occurred in  TagRepositoryImpl | edit tag "+ e, e);
			return 0;
		}
	}
	//delete
		@Transactional
		public Integer deleteTagById(Integer id) {
			Session session = sessionFactory.getCurrentSession();
			try {
				 Tag  Tag = new  Tag();
				 Tag = session.find(Tag.class, id);
				if( Tag!=null) {
					session.remove( Tag);
				}
				return 1;
			} catch (Exception e) {
				LOGGER.error("ERROR! An error occurred in  TagRepositoryImpl | delete TagById "+ e, e);
				return 0;
			}
		}
	}
