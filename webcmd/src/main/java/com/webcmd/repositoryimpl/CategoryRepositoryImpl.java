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

import com.webcmd.entity.Category;
import com.webcmd.model.CategoryModel;
import com.webcmd.repository.ICategoryRepository;
@Repository
@Transactional(rollbackFor = Exception.class)
public class CategoryRepositoryImpl implements ICategoryRepository{
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DataSource dataSource;
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
			LOGGER.error("ERROR! An error occurred in  CategoryRepositoryImpl | findById "+ e, e);
		}
		return customCategory;
	}
	@Override
	@Transactional
	public List<CategoryModel> findAll(String category_name, String sort, String order, Integer offset, Integer limit) {
		List<CategoryModel> customCategoryList = new ArrayList<CategoryModel>();
		Set<Category> categorySet = new LinkedHashSet<Category>();
		StringBuilder hql = new StringBuilder("FROM categories c ");
		hql.append(" WHERE c.categoryName LIKE CONCAT('%',:categoryName,'%')");
		//hql.append(" order by p." + sort + " " + order );
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			LOGGER.info(hql.toString());
			query.setParameter("categoryName", category_name);
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
			LOGGER.error("ERROR! An error occurred in  CategoryRepositoryImpl | findAll "+ e, e);
		}
		return customCategoryList;
	}
	//count 
	@Override
	public Integer countAllPaging( String category_name) {
		Set<Category> categorySet = new LinkedHashSet<Category>();
		StringBuilder hql = new StringBuilder("FROM categories c ");
		hql.append(" WHERE c.categoryName LIKE CONCAT('%',:categoryName,'%')");
		Session session = this.sessionFactory.getCurrentSession();
		try {
			Query query = session.createQuery(hql.toString());			
			query.setParameter("categoryName", category_name);
			LOGGER.info(hql.toString());
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {
				Object ob = (Object) it.next();
				categorySet.add((Category) ob);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR! An error occurred in  CategoryRepositoryImpl | count total "+ e, e);	
		}
		
		return categorySet.size();		
	}
	//insert 
	@Override
	public Integer insert(Category category) {
			try {
				LOGGER.info("SAVE CATEGORY....");
				Session session = sessionFactory.getCurrentSession();
				session.save(category);
				session.flush();
				return 1;
			} catch (Exception e) {
				LOGGER.error("ERROR! An error occurred in  CategoryRepositoryImpl | insert "+ e, e);
				return 0;
			}
		}
	//edit
	@Override
	@Transactional
	public Integer edit(Category category) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.update(category);
			return 1;
		} catch (Exception e) {
			LOGGER.error("ERROR! An error occurred in  CategoryRepositoryImpl | edit category "+ e, e);
			return 0;
		}
	}
	//delete
		@Transactional
		public Integer deleteCategoryById(Integer id) {
			Session session = sessionFactory.getCurrentSession();
			try {
				 Category  Category = new  Category();
				 Category = session.find(Category.class, id);
				if( Category!=null) {
					session.remove( Category);
				}
				return 1;
			} catch (Exception e) {
				LOGGER.error("ERROR! An error occurred in  CategoryRepositoryImpl | delete CategoryById "+ e, e);
				return 0;
			}
		}
	}
