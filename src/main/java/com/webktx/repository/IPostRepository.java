package com.webktx.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.webktx.entity.Comment;
import com.webktx.entity.Post;
import com.webktx.model.CommentModel;
import com.webktx.model.PostModel;
public interface IPostRepository {
	
	PostModel findById(Integer id);
	Post findPostById(Integer id);
	List<PostModel> findAll( 
			@Param("title") String title, 
			@Param("content") String content, 
			@Param("user_id") String user_id,
			@Param("category_id") Integer category_id, 
			@Param("tag_id") Integer tag_id, 
			@Param("sort") String sort,
			@Param("order") String order,
			@Param("offset") Integer offset,
			@Param("limit") Integer limit,
			boolean canEdit);
	Integer countAllPaging( 
			@Param("title") String title, 
			@Param("content") String content, 
			@Param("user_id") String user_id,
			@Param("category_id") Integer category_id, 
			@Param("tag_id") Integer tag_id, boolean canEdit);
	Integer edit (Post post);
	Integer insert (Post post);
	Integer deletePostById(Integer id);
	List<PostModel> findRelatedPosts(Integer numberPost, Integer postId); 
	void updateView(int postId,  int viewCount);
	List<CommentModel> findCommentByPostId(Integer postId);
	Integer editComment(Comment comment);
	Integer addComment(Comment comment);
	Integer deleteCommentById(Integer id);
	Comment findCommentById(Integer id);
}


