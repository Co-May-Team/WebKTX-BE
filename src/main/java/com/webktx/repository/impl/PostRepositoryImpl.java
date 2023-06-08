package com.webktx.repository.impl;

//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.Date;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.webktx.entity.Comment;
import com.webktx.entity.Post;
import com.webktx.entity.Tag;
import com.webktx.entity.User;
import com.webktx.model.CategoryModel;
import com.webktx.model.CommentModel;
import com.webktx.model.PostModel;
import com.webktx.model.TagModel;
import com.webktx.model.UserModel;
import com.webktx.repository.IPostRepository;
import com.webktx.service.CustomRoleService;
import com.webktx.service.UserDetailsImpl;
import com.webktx.ultil.Ultil;

@Repository
@Transactional(rollbackFor = Exception.class)
public class PostRepositoryImpl implements IPostRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(PostRepositoryImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	UserRepositoryImpl userRepositoryImpl;

	@Override
	public PostModel findById(Integer id) {
		PostModel customPost = new PostModel();
		StringBuilder hql = new StringBuilder("FROM posts AS p");
		hql.append(" WHERE p.postId = :id");
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			query.setParameter("id", id);
			for (Iterator<?> it = query.getResultList().iterator(); it.hasNext();) {
				Object obj = (Object) it.next();
				Post post = (Post) obj;
				customPost.setPostId(post.getPostId());
				List<TagModel> tagModels = new ArrayList<>();
				for (Tag tag : post.getTags()) {
					TagModel tagModel = new TagModel();
					tagModel.setTagId(tag.getTagId());
					tagModel.setTagName(tag.getTagName());
					tagModels.add(tagModel);
				}
				customPost.setTagModels(tagModels);
				customPost.setTitle(post.getTitle());
				customPost.setContent(post.getContent());
				UserModel userInfo = userRepositoryImpl.findByUsername(post.getUser().getFullName());
				userInfo.setRole(null);
				customPost.setUserInfo(userInfo);
				CategoryModel categoryModel = new CategoryModel();
				categoryModel.setCategoryId(post.getCategory().getCategoryId());
				categoryModel.setCategoryName(post.getCategory().getCategoryName());
				customPost.setCategory(categoryModel);
				customPost.setSummary(post.getSummary());
				customPost.setPublishedAt(post.getPublishedAt().toLocalDateTime());
				customPost.setIsPublished(post.getIsPublished());
				customPost.setViewed(post.getViewed());
				try {
					customPost.setThumbnail(Ultil.converImageNameToLink(post.getSmallPictureId()));
				} catch (Exception e) {
					LOGGER.error("{}", e);
				}
				customPost.setCreatedAt(post.getCreatedAt());
				customPost.setUpdatedAt(post.getUpdatedAt());
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured in Impl findById API: ", e);
		}
		return customPost;
	}

	@Override
	@Transactional
	public List<PostModel> findAll(String title, String content, String user_id, Integer category_id, Integer tag_id,
			String sort, String order, Integer offset, Integer limit, boolean canEdit) {
		List<PostModel> customPostList = new ArrayList<PostModel>();
		Set<Post> postSet = new LinkedHashSet<Post>();
		StringBuilder hql = new StringBuilder("FROM posts p ");
		hql.append(" INNER JOIN p.tags AS t");
		hql.append(" WHERE ");
		hql.append(" p.isPublished = '1' AND ");
		if(!title.trim().equals("") && !content.trim().equals("")) {
			hql.append(" ( p.title LIKE CONCAT('%',:title,'%') OR p.content LIKE CONCAT('%',:content,'%'))");
		}else {
			hql.append(" ( p.title LIKE CONCAT('%',:title,'%') AND p.content LIKE CONCAT('%',:content,'%'))");
		}
		if(null != user_id && !user_id.equals("")) {
			hql.append(" AND p.user.userId LIKE CONCAT('%',:user_id,'%')");
		}

		if(category_id!= 0) {
			hql.append(" AND p.category.categoryId =:category_id ");
		}
		if(tag_id != 0) {
			hql.append(" AND t.tagId = :tag_id ");
		}
		hql.append(" order by p." + sort + " " + order);

		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			LOGGER.info(hql.toString());
			query.setParameter("title", title);
			query.setParameter("content", content);
			if(null != user_id && !user_id.equals("")) {
				query.setParameter("user_id", user_id);
			}

			if(category_id!= 0) {
				query.setParameter("category_id",category_id);
			}
			if(tag_id != 0) {
				query.setParameter("tag_id", tag_id);
			}
			LOGGER.info(offset.toString());
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			for (Iterator<?> it = query.getResultList().iterator(); it.hasNext();) {
				Object[] obj = (Object[]) it.next();
				Post post = (Post) obj[0];
				postSet.add(post);
			}
			for (Post post : postSet) {
				PostModel customPost = new PostModel();
				customPost = toModel(post);
				customPostList.add(customPost);
				
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured in findAll " + e, e);
		}
		return customPostList;
	}

	@Override
	public Integer countAllPaging(String title, String content, String user_id, Integer category_id, Integer tag_id, boolean canEdit) {
		Set<Post> postSet = new LinkedHashSet<Post>();
		StringBuilder hql = new StringBuilder("FROM posts AS p ");
		hql.append(" INNER JOIN p.tags AS t");
		hql.append(" WHERE ");
		hql.append(" p.isPublished = '1' AND ");
		if(!title.trim().equals("") && !content.trim().equals("")) {
			hql.append(" ( p.title LIKE CONCAT('%',:title,'%') OR p.content LIKE CONCAT('%',:content,'%'))");
		}else {
			hql.append(" ( p.title LIKE CONCAT('%',:title,'%') AND p.content LIKE CONCAT('%',:content,'%'))");
		}
		if(null != user_id && !user_id.equals("")) {
			hql.append(" AND p.user.userId LIKE CONCAT('%',:user_id,'%')");
		}
		if(category_id!= 0) {
			hql.append(" AND p.category.categoryId =:category_id ");
		}
		if(tag_id != 0) {
			hql.append(" AND t.tagId = :tag_id ");
		}
		Session session = this.sessionFactory.getCurrentSession();
		try {
			Query query = session.createQuery(hql.toString());
			query.setParameter("title", title);
			query.setParameter("content", content);
			if(null != user_id && !user_id.equals("")) {
				query.setParameter("user_id", user_id);
			}
			if(category_id!= 0) {
				query.setParameter("category_id",category_id);
			}
			if(tag_id != 0) {
				query.setParameter("tag_id", tag_id);
			}
			LOGGER.info(hql.toString());
			for (Iterator<?> it = query.getResultList().iterator(); it.hasNext();) {
				Object ob[] = (Object[]) it.next();
				postSet.add((Post) ob[0]);
			}

		} catch (Exception e) {
			LOGGER.error("Error has occured in count total Posts " + e, e);

		}

		return postSet.size();

	}
	@Override
	public List<PostModel> findAllHidden(String title, String content, String user_id, Integer category_id, Integer tag_id,
			String sort, String order, Integer offset, Integer limit, boolean canEdit) {
		List<PostModel> customPostList = new ArrayList<PostModel>();
		Set<Post> postSet = new LinkedHashSet<Post>();
		StringBuilder hql = new StringBuilder("FROM posts p ");
		hql.append(" INNER JOIN p.tags AS t");
		hql.append(" WHERE ");
		hql.append(" p.isPublished = '0' AND ");
		if(!title.trim().equals("") && !content.trim().equals("")) {
			hql.append(" ( p.title LIKE CONCAT('%',:title,'%') OR p.content LIKE CONCAT('%',:content,'%'))");
		}else {
			hql.append(" ( p.title LIKE CONCAT('%',:title,'%') AND p.content LIKE CONCAT('%',:content,'%'))");
		}
		if(null != user_id && !user_id.equals("")) {
			hql.append(" AND p.user.userId LIKE CONCAT('%',:user_id,'%')");
		}
		
		if(category_id!= 0) {
			hql.append(" AND p.category.categoryId =:category_id ");
		}
		if(tag_id != 0) {
			hql.append(" AND t.tagId = :tag_id ");
		}
		hql.append(" order by p." + sort + " " + order);
		
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			LOGGER.info(hql.toString());
			query.setParameter("title", title);
			query.setParameter("content", content);
			if(null != user_id && !user_id.equals("")) {
				query.setParameter("user_id", user_id);
			}
			
			if(category_id!= 0) {
				query.setParameter("category_id",category_id);
			}
			if(tag_id != 0) {
				query.setParameter("tag_id", tag_id);
			}
			LOGGER.info(offset.toString());
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			for (Iterator<?> it = query.getResultList().iterator(); it.hasNext();) {
				Object[] obj = (Object[]) it.next();
				Post post = (Post) obj[0];
				postSet.add(post);
			}
			for (Post post : postSet) {
				PostModel customPost = new PostModel();
				customPost = toModel(post);
				customPostList.add(customPost);
				
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured in findAll " + e, e);
		}
		return customPostList;
	}
	
	@Override
	public Integer countAllPagingHidden(String title, String content, String user_id, Integer category_id, Integer tag_id, boolean canEdit) {
		Set<Post> postSet = new LinkedHashSet<Post>();
		StringBuilder hql = new StringBuilder("FROM posts AS p ");
		hql.append(" INNER JOIN p.tags AS t");
		hql.append(" WHERE ");
		hql.append(" p.isPublished = '0' AND ");
		if(!title.trim().equals("") && !content.trim().equals("")) {
			hql.append(" ( p.title LIKE CONCAT('%',:title,'%') OR p.content LIKE CONCAT('%',:content,'%'))");
		}else {
			hql.append(" ( p.title LIKE CONCAT('%',:title,'%') AND p.content LIKE CONCAT('%',:content,'%'))");
		}
		if(null != user_id && !user_id.equals("")) {
			hql.append(" AND p.user.userId LIKE CONCAT('%',:user_id,'%')");
		}
		if(category_id!= 0) {
			hql.append(" AND p.category.categoryId =:category_id ");
		}
		if(tag_id != 0) {
			hql.append(" AND t.tagId = :tag_id ");
		}
		Session session = this.sessionFactory.getCurrentSession();
		try {
			Query query = session.createQuery(hql.toString());
			query.setParameter("title", title);
			query.setParameter("content", content);
			if(null != user_id && !user_id.equals("")) {
				query.setParameter("user_id", user_id);
			}
			if(category_id!= 0) {
				query.setParameter("category_id",category_id);
			}
			if(tag_id != 0) {
				query.setParameter("tag_id", tag_id);
			}
			LOGGER.info(hql.toString());
			for (Iterator<?> it = query.getResultList().iterator(); it.hasNext();) {
				Object ob[] = (Object[]) it.next();
				postSet.add((Post) ob[0]);
			}
			
		} catch (Exception e) {
			LOGGER.error("Error has occured in count total Posts " + e, e);
			
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
		Session session = sessionFactory.getCurrentSession();
		try {
			session.update(post);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured at edit() ", e);
			return 0;
		}
	}

	@Override
	public Integer insert(Post post) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Integer id = (Integer) session.save(post);
			return id;
		} catch (Exception e) {
			LOGGER.error("Error has occured at add() ", e);
		}
		return -1;
	}

	@Override
	public Integer deletePostById(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Post post = session.find(Post.class, id);
			session.remove(post);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured in delete() ", e);
			return 0;
		}
	}

	@Override
	public Post findPostById(Integer id) {
		Post post = null;
		StringBuilder hql = new StringBuilder("FROM posts AS p");
		hql.append(" WHERE p.postId = :id");
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			query.setParameter("id", id);
			post = (Post) query.getSingleResult();
		} catch (Exception e) {
			LOGGER.error("Error has occured in Impl findById API: ", e);
		}
		return post;
	}

	@Override
	public List<PostModel> findRelatedPosts(Integer numberPost, Integer postId) {
		List<PostModel> customPostList = new ArrayList<PostModel>();
		Set<Post> postSet = new LinkedHashSet<Post>();
		StringBuilder hql = new StringBuilder("FROM posts p ");
		hql.append(" INNER JOIN p.tags AS t");
		hql.append(" WHERE p.postId != :postId");
		hql.append(" order by p.updatedAt DESC");

		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			LOGGER.info(hql.toString());
			query.setParameter("postId", postId);
			query.setMaxResults(numberPost);
			for (Iterator<?> it = query.getResultList().iterator(); it.hasNext();) {
				Object[] obj = (Object[]) it.next();
				Post post = (Post) obj[0];
				postSet.add(post);
			}
			for (Post post : postSet) {
				PostModel customPost = new PostModel();
				customPost.setPostId(post.getPostId());
				List<TagModel> tagModels = new ArrayList<>();
				for (Tag tag : post.getTags()) {
					TagModel tagModel = new TagModel();
					tagModel.setTagId(tag.getTagId());
					tagModel.setTagName(tag.getTagName());
					tagModels.add(tagModel);
				}
				customPost.setTagModels(tagModels);
				customPost.setTitle(post.getTitle());
				customPost.setContent(post.getContent());
				UserModel userInfo = userRepositoryImpl.findByUsername(post.getUser().getFullName());
				userInfo.setRole(null);
				customPost.setUserInfo(userInfo);				CategoryModel categoryModel = new CategoryModel();
				categoryModel.setCategoryId(post.getCategory().getCategoryId());
				categoryModel.setCategoryName(post.getCategory().getCategoryName());
				customPost.setCategory(categoryModel);
				customPost.setThumbnail(Ultil.converImageNameToLink(post.getSmallPictureId()));
				customPost.setSummary(post.getSummary());
				customPost.setCreatedAt(post.getCreatedAt());
				customPost.setUpdatedAt(post.getUpdatedAt());
				customPost.setPublishedAt(post.getPublishedAt().toLocalDateTime());
				customPost.setIsPublished(post.getIsPublished());
				customPost.setViewed(post.getViewed());
				customPostList.add(customPost);
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured in findAll " + e, e);
		}
		return customPostList;
	}
	@Override
	public void updateView(int postId, int viewedCount) {
		String hql  = "UPDATE posts as s set s.viewed = :viewed where s.postId = :postId";
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql);
			query.setParameter("postId", postId);
			query.setParameter("viewed", viewedCount);
			LOGGER.info(hql);
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/** Comment a post **/
	
	@Override
	public List<CommentModel> findCommentByPostId(Integer postId) {
		List<Comment> commentList = new ArrayList<>();
		List<CommentModel> commentModelList = new ArrayList<>();
		Comment comment = null;
		StringBuilder hql = new StringBuilder("FROM comments as cm");
		hql.append(" WHERE cm.post.postId = :postId order by cm.createdAt desc");
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			query.setParameter("postId", postId);
			for (Iterator<?> it = query.getResultList().iterator(); it.hasNext();) {
				comment = new Comment();
				CommentModel commentModel = new CommentModel();
				comment = (Comment) it.next();
				commentModel = toCommentModel(comment);
				commentModelList.add(commentModel);
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured in Impl findById API: ", e);
		}
		return commentModelList;
	}
//	@Override
//	public Integer edit(Post post) {
//		Session session = sessionFactory.getCurrentSession();
//		try {
//			session.update(post);
//			return 1;
//		} catch (Exception e) {
//			LOGGER.error("Error has occured at edit() ", e);
//			return 0;
//		}
//	}
//
//	@Override
//	public Integer insert(Post post) {
//		Session session = sessionFactory.getCurrentSession();
//		try {
//			Integer id = (Integer) session.save(post);
//			return id;
//		} catch (Exception e) {
//			LOGGER.error("Error has occured at add() ", e);
//		}
//		return -1;
//	}


	@Override
	public Integer addComment(Comment comment) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Integer id = (Integer) session.save(comment);
			return id;
		} catch (Exception e) {
			LOGGER.error("Error has occured at add() ", e);
		}
		return -1;
	}
	@Override
	public Integer editComment(Comment comment) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.update(comment);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured at edit() ", e);
			return -1;
		}
	}
	@Override
	public Integer deleteCommentById(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Comment comment = session.find(Comment.class, id);
			session.remove(comment);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured in delete() ", e);
			return 0;
		}
	}
	@Override
	public Comment findCommentById(Integer id) {
		Comment comment = null;
		StringBuilder hql = new StringBuilder("FROM comments as cm");
		hql.append(" WHERE cm.id = :id");
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			query.setParameter("id", id);
			comment = (Comment) query.getSingleResult();
		} catch (Exception e) {
			LOGGER.error("Error has occured in Impl findById API: ", e);
		}
		return comment;
	}

	public CommentModel toCommentModel(Comment comment) {
		UserModel userModel = new UserModel();
		CommentModel commentModel = new CommentModel();
		
		User user = comment.getUser();
		userModel.setId(user.getUserId());
		userModel.setFullName(user.getFullName());
		userModel.setAvatar(user.getAvatar());
		userModel.setGoogleAccount(user.isGoogleAccount());
		
		commentModel.setId(comment.getId());
		commentModel.setCommentText(comment.getCommentText());
		commentModel.setUser(userModel);
		commentModel.setParentId(comment.getParentId());
		return commentModel;
	}
	public PostModel toModel(Post post) {
		PostModel customPost = new PostModel();
		customPost.setPostId(post.getPostId());
		List<TagModel> tagModels = new ArrayList<>();
		for (Tag tag : post.getTags()) {
			TagModel tagModel = new TagModel();
			tagModel.setTagId(tag.getTagId());
			tagModel.setTagName(tag.getTagName());
			tagModels.add(tagModel);
		}
		customPost.setTagModels(tagModels);
		customPost.setTitle(post.getTitle());
		customPost.setContent(post.getContent());
		UserModel userInfo = userRepositoryImpl.findByUsername(post.getUser().getFullName());
		userInfo.setRole(null);
		customPost.setUserInfo(userInfo);				
		CategoryModel categoryModel = new CategoryModel();
		categoryModel.setCategoryId(post.getCategory().getCategoryId());
		categoryModel.setCategoryName(post.getCategory().getCategoryName());
		customPost.setCategory(categoryModel);
		customPost.setThumbnail(Ultil.converImageNameToLink(post.getSmallPictureId()));
		customPost.setSummary(post.getSummary());
		customPost.setCreatedAt(post.getCreatedAt());
		customPost.setUpdatedAt(post.getUpdatedAt());
		customPost.setPublishedAt(post.getPublishedAt().toLocalDateTime());
		customPost.setIsPublished(post.getIsPublished());
		customPost.setViewed(post.getViewed());
		return customPost;
	}
}
