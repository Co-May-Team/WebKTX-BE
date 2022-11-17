package com.webktx.repository.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
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

import com.webktx.entity.Category;
import com.webktx.model.CategoryModel;
import com.webktx.repository.ICategoryRepository;
@Repository
@Transactional(rollbackFor = Exception.class)
public class CategoryRepositoryImpl implements ICategoryRepository{
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SessionFactory sessionFactory;


	@Override
	public CategoryModel findById(Integer id) {
		CategoryModel customCategory = new CategoryModel();
		StringBuilder hql = new StringBuilder("FROM categories AS c");
		hql.append(" WHERE c.categoryId = :id");
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			query.setParameter("id",id);
			LOGGER.info(hql.toString());
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {		
				Object obj = (Object) it.next();
				Category category = (Category) obj;
				customCategory.setCategoryId(category.getCategoryId());
				customCategory.setCategoryName(category.getCategoryName());
				customCategory.setCreatedAt(category.getCreatedAt());
				customCategory.setUpdatedAt(category.getUpdatedAt());
			}
		}
		catch (Exception e) {
			LOGGER.error("Error has occured in Impl findById API: "+e,e);
		}
		return customCategory;
	}
	@Override
	@Transactional
	public List<CategoryModel> findAll(String category_name, String sort, String order, Integer offset, Integer limit) {
		List<CategoryModel> customCategoryList = new ArrayList<CategoryModel>();
		Set<Category> categorySet = new LinkedHashSet<Category>();
		StringBuilder hql = new StringBuilder("FROM categories c ");
		hql.append(" WHERE c.category_name LIKE CONCAT('%',:category_name,'%')");
		//hql.append(" order by p." + sort + " " + order );
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			LOGGER.info(hql.toString());
			query.setParameter("category_name", category_name);
//			query.setParameter("order", order);
//			query.setParameter("sort", sort);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {		
				Object obj = (Object) it.next();
				Category category = (Category) obj;
				categorySet.add(category);
			}
			for (Category category : categorySet) {
				CategoryModel customCategory = new CategoryModel();
				customCategory.setCategoryId(category.getCategoryId());
				customCategory.setCategoryName(category.getCategoryName());
				customCategory.setCreatedAt(category.getCreatedAt());
				customCategory.setUpdatedAt(category.getUpdatedAt());
				customCategoryList.add(customCategory);
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured in findAll Category"+e, e);
		}
		return customCategoryList;
	}
	@Override
	public Integer countAllPaging( String category_name) {
		Set<Category> categorySet = new LinkedHashSet<Category>();
		StringBuilder hql = new StringBuilder("FROM categories c ");
		hql.append(" WHERE c.category_name LIKE CONCAT('%',:category_name,'%')");
		Session session = this.sessionFactory.getCurrentSession();
		try {
			Query query = session.createQuery(hql.toString());			
			query.setParameter("category_name", category_name);
			LOGGER.info(hql.toString());
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {
				Object ob = (Object) it.next();
				categorySet.add((Category) ob);
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured in count total Categorys " +e, e);	
		}
		
		return categorySet.size();		
	}
	//insert a category
	@Override
	public Integer insert(Category category) {
			try {
				LOGGER.info("SAVE CATEGORY....");
				Session session = sessionFactory.getCurrentSession();
				session.save(category);
				session.flush();
				return 1;
			} catch (Exception e) {
				LOGGER.error("Error has occured in Category Impl: "+ e, e);
				return -1;
			}
		}
	@Override
	@Transactional
	public Integer edit(Category category) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.update(category);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error in Category Impl: "+e, e);
			return -1;
		}
	}
	@Override
	public Integer deleteCategoryById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}
//	//edit a post
//	@Override
//	@Transactional
//	public Integer edit(Post post) {
//		Session session = sessionFactory.getCurrentSession();
//		try {
//			session.update(post);
//			return 1;
//		} catch (Exception e) {
//			LOGGER.error("Error has occured in edit a post API "+e, e);
//			return 0;
//		}
//	}
//	//delete
//	@Transactional
//	public Integer deletePostById(Integer id) {
//		Session session = sessionFactory.getCurrentSession();
//		try {
//			Post post = new Post();
//			post = session.find(Post.class, id);
//			if(post!=null) {
//				session.remove(post);
//			}
//			return 1;
//		} catch (Exception e) {
//			LOGGER.error("Error has occured in delete post API "+e, e);
//			return 0;
//		}
	}
