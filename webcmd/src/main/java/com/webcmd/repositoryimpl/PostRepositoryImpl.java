package com.webcmd.repositoryimpl;

//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.List;

import javax.persistence.Query;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
		hql.append(" INNER JOIN p.user_id as a");
		hql.append(" INNER JOIN p.category_id as b");
		hql.append(" WHERE p.post_id = :id");
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			query.setParameter("id",id);
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {		
				Object[] obj = (Object[]) it.next();
				Post post = (Post) obj[0];
				customPost.setPost_id(post.getPost_id());
				customPost.setTitle(post.getTitle());
				customPost.setContent(post.getContent());
				customPost.setUser_name(post.getUser_id().getFull_name());
				customPost.setCategory_name(post.getCategory_id().getCategory_name());
				customPost.setIs_published(post.getIs_published());
				customPost.setPublish_date(post.getPublish_date());
				customPost.setSmall_picture_id(post.getSmall_picture_id());
				customPost.setCreated_at(post.getCreated_at());
				customPost.setUpdated_at(post.getUpdated_at());
			}
		}
		catch (Exception e) {
			LOGGER.error("Error has occured in Impl findById API: ",e);
		}
		return customPost;
	}
	@Override
	@Transactional
	public List<PostModel> findAll(String title, String content, String user_id, String category_id, String is_published, String sort, String order, Integer offset, Integer limit) {
		List<PostModel> customPostList = new ArrayList<PostModel>();
		Set<Post> postSet = new LinkedHashSet<Post>();
		StringBuilder hql = new StringBuilder("FROM posts p ");
		hql.append(" INNER JOIN p.user_id as a");
		hql.append(" INNER JOIN p.category_id as b");
		hql.append(" WHERE p.title LIKE CONCAT('%',:title,'%')");
		hql.append(" AND p.content LIKE CONCAT('%',:content,'%')");
		hql.append(" AND a.full_name LIKE CONCAT('%',:user_id,'%')");
		hql.append(" AND b.category_name LIKE CONCAT('%',:category_id,'%')");
		hql.append(" AND p.is_published LIKE CONCAT('%',:is_published,'%')");
		//hql.append(" order by p." + sort + " " + order );
		
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			LOGGER.info(hql.toString());
			query.setParameter("title", title);
			query.setParameter("content", content);
			query.setParameter("user_id", user_id);
			query.setParameter("category_id", category_id);
			query.setParameter("is_published", is_published);
//			query.setParameter("order", order);
//			query.setParameter("sort", sort);
			LOGGER.info(offset.toString());
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {		
				Object[] obj = (Object[]) it.next();
				Post post = (Post) obj[0];
				postSet.add(post);
			}
			for (Post post : postSet) {
				PostModel customPost = new PostModel();
				customPost.setPost_id(post.getPost_id());
				customPost.setTitle(post.getTitle());
				customPost.setContent(post.getContent());
				customPost.setUser_name(post.getUser_id().getFull_name());
				customPost.setCategory_name(post.getCategory_id().getCategory_name());
				customPost.setIs_published(post.getIs_published());
				customPost.setPublish_date(post.getPublish_date());
				customPost.setSmall_picture_id(post.getSmall_picture_id());
				customPost.setCreated_at(post.getCreated_at());
				customPost.setUpdated_at(post.getUpdated_at());
				customPostList.add(customPost);
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured in findAll "+e, e);
		}
		return customPostList;
	}
	@Override
	public Integer countAllPaging(String title, String content, String user_id, String category_id, String is_published) {
		Set<Post> postSet = new LinkedHashSet<Post>();
		StringBuilder hql = new StringBuilder("FROM posts AS p ");
		hql.append(" INNER JOIN p.user_id as a");
		hql.append(" INNER JOIN p.category_id as b");
		hql.append(" WHERE p.title LIKE CONCAT('%',:title,'%')");
		hql.append(" AND p.content LIKE CONCAT('%',:content,'%')");
		hql.append(" AND a.full_name LIKE CONCAT('%',:user_id,'%')");
		hql.append(" AND b.category_name LIKE CONCAT('%',:category_id,'%')");
		hql.append(" AND p.is_published LIKE CONCAT('%',:is_published,'%')");
		Session session = this.sessionFactory.getCurrentSession();
		try {
			Query query = session.createQuery(hql.toString());			
			query.setParameter("title", title);
			query.setParameter("content", content);
			query.setParameter("user_id", user_id);
			query.setParameter("category_id", category_id);
			query.setParameter("is_published", is_published);
			LOGGER.info(hql.toString());
			for (Iterator it = query.getResultList().iterator(); it.hasNext();) {
				Object[] ob = (Object[]) it.next();
				postSet.add((Post) ob[0]);
			}
			
		} catch (Exception e) {
			LOGGER.error("Error has occured in count total Posts " +e, e);
			
		}
		
		return postSet.size();
		
		
	}
//	//insert a post
//	@Override
//	public Integer insert(Post post) {
//		Session session = sessionFactory.getCurrentSession();
//		try {
//			LOGGER.info("SAVE TASK....");
//			session.save(post);
//			session.flush();
//			return 1;
//		} catch (Exception e) {
//			LOGGER.error("Error has occured in insert a post API "+ e, e);
//			return 0;
//		}
//	}
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
//	}
	@Override
	public Integer edit(Post post) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer insert(Post post) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer deletePostById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}
}
