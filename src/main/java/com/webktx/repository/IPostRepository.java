package com.webktx.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.webktx.entity.Post;
import com.webktx.model.PostModel;
public interface IPostRepository {
	
	PostModel findById(Integer id);
	Post findPostById(Integer id);
	List<PostModel> findAll( 
			@Param("title") String title, 
			@Param("content") String content, 
			@Param("category_id") String category_id, 
			@Param("user_id") String user_id,
			@Param("is_published") String is_published,
			@Param("sort") String sort,
			@Param("order") String order,
			@Param("offset") Integer offset,
			@Param("limit") Integer limit);
	Integer countAllPaging( 
			@Param("title") String title, 
			@Param("content") String content, 
			@Param("category_id") String category_id, 
			@Param("user_id") String user_id,
			@Param("is_published") String is_published);
	Integer edit (Post post);
	Integer insert (Post post);
	Integer deletePostById(Integer id);
}


