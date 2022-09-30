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
import com.webcmd.model.PostModel;
import com.webcmd.repository.IPostRepository;

@Repository
@Transactional(rollbackFor = Exception.class)
public class PostRepositoryImpl implements IPostRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(PostRepositoryImpl.class);
	@Autowired
	private DataSource dataSource;
	@Autowired
	private SessionFactory sessionFactory;


	@Override
	public PostModel findById(Integer id) {
		PostModel customPost = new PostModel();
		StringBuilder hql = new StringBuilder("FROM posts AS p");
		hql.append(" INNER JOIN p.userId as a");
		hql.append(" INNER JOIN p.categoryId as b");
		hql.append(" WHERE p.postId = :id");
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			query.setParameter("id",id);
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {		
				Object[] obj = (Object[]) it.next();
				Post post = (Post) obj[0];
				customPost.setPostId(post.getPostId());
				customPost.setTitle(post.getTitle());
				customPost.setContent(post.getContent());
				customPost.setUserName(post.getUserId().getFullName());
				customPost.setCategoryName(post.getCategoryId().getCategoryName());
				customPost.setIsPublished(post.getIsPublished());
				//customPost.setPublishDate(post.getPublishDate());
				customPost.setSmallPictureId(post.getSmallPictureId());
				customPost.setCreatedAt(post.getCreatedAt());
				customPost.setUpdatedAt(post.getUpdatedAt());
			}
		}
		catch (Exception e) {
			LOGGER.error("ERROR! An error occurred in postRepositoryImpl | findById "+ e, e);
		}
		return customPost;
	}
	@Override
	@Transactional
	public List<PostModel> findAll( String user_id, String category_id, String title, String content,Boolean is_published, String sort, String o, Integer offset, Integer limit) {
		List<PostModel> customPostList = new ArrayList<PostModel>();
		Set<Post> postSet = new LinkedHashSet<Post>();
		StringBuilder hql = new StringBuilder("FROM posts p ");
		hql.append(" INNER JOIN p.userId a");
		hql.append(" INNER JOIN p.categoryId b");
		hql.append(" WHERE p.title LIKE CONCAT('%',:title,'%')");
		hql.append(" AND p.content LIKE CONCAT('%',:content,'%')");
		hql.append(" AND a.fullName LIKE CONCAT('%',:userId,'%')");
		hql.append(" AND b.categoryName LIKE CONCAT('%',:categoryId,'%')");
		if (is_published !=null) {
			hql.append(" AND p.isPublished LIKE CONCAT('%',:isPublished,'%')");
		}
		//hql.append(" ORDER BY p." + sort + " "+ o  );
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			LOGGER.info(hql.toString());
			query.setParameter("title", title);
			query.setParameter("content", content);
			query.setParameter("userId", user_id);
			query.setParameter("categoryId", category_id);
			if (is_published !=null) {
				query.setParameter("isPublished", is_published);
			}
//			query.setParameter("o", o);
//			query.setParameter("sort", sort);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {		
				Object[] obj = (Object[]) it.next();
				Post post = (Post) obj[0];
				postSet.add(post);
			}
			for (Post post : postSet) {
				PostModel customPost = new PostModel();
				customPost.setPostId(post.getPostId());
				customPost.setTitle(post.getTitle());
				customPost.setContent(post.getContent());
				customPost.setUserName(post.getUserId().getFullName());
				customPost.setCategoryName(post.getCategoryId().getCategoryName());
				customPost.setIsPublished(post.getIsPublished());
				//customPost.setPublishDate(post.getPublishDate());
				customPost.setSmallPictureId(post.getSmallPictureId());
				customPost.setCreatedAt(post.getCreatedAt());
				customPost.setUpdatedAt(post.getUpdatedAt());
				customPostList.add(customPost);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR! An error occurred in postRepositoryImpl | findAll "+ e, e);
		}
		return customPostList;
	}
	@Override
	public Integer countAllPaging( String user_id, String category_id,String title, String content, Boolean is_published) {
		Set<Post> postSet = new LinkedHashSet<Post>();
		StringBuilder hql = new StringBuilder("FROM posts AS p ");
		hql.append(" INNER JOIN p.userId as a");
		hql.append(" INNER JOIN p.categoryId as b");
		hql.append(" WHERE p.title LIKE CONCAT('%',:title,'%')");
		hql.append(" AND p.content LIKE CONCAT('%',:content,'%')");
		hql.append(" AND a.fullName LIKE CONCAT('%',:userId,'%')");
		hql.append(" AND b.categoryName LIKE CONCAT('%',:categoryId,'%')");
		if (is_published !=null) {
			hql.append(" AND p.isPublished LIKE CONCAT('%',:isPublished,'%')");
		}
		Session session = this.sessionFactory.getCurrentSession();
		try {
			Query query = session.createQuery(hql.toString());			
			query.setParameter("title", title);
			query.setParameter("content", content);
			query.setParameter("userId", user_id);
			query.setParameter("categoryId", category_id);
			if (is_published !=null) {
				query.setParameter("isPublished", is_published);
			}
			LOGGER.info(hql.toString());
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {
				Object[] ob = (Object[]) it.next();
				postSet.add((Post) ob[0]);
			}
			
		} catch (Exception e) {
			LOGGER.error("ERROR! An error occurred in postRepositoryImpl | countAllPaging "+ e, e);
			
		}
		return postSet.size();	
	}
	//insert a post
	@Override
	public Integer insert(Post post) {
		Session session = sessionFactory.getCurrentSession();
		try {
			LOGGER.info("SUCCESSFUL! SAVE A POST....");
			session.save(post);
			session.flush();
			return 1;
		} catch (Exception e) {
			LOGGER.error("ERROR! An error occurred in postRepositoryImpl | insert "+ e, e);
			return 0;
		}
	}
	//edit
	@Override
	@Transactional
	public Integer edit(Post post) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.update(post);
			return 1;
		} catch (Exception e) {
			LOGGER.error("ERROR! An error occurred in postRepositoryImpl | edit "+ e, e);
			return 0;
		}
	}
	//delete
	@Transactional
	public Integer deletePostById(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Post post = new Post();
			post = session.find(Post.class, id);
			if(post!=null) {
				session.remove(post);
			}
			return 1;
		} catch (Exception e) {
			LOGGER.error("ERROR! An error occurred in postRepositoryImpl | deletePostById "+ e, e);
			return 0;
		}
	}
	
	
}
