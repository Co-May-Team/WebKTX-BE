package com.webcmd.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.webcmd.entity.Post;
import com.webcmd.entity.PostTag;
import com.webcmd.model.PostModel;
public interface IPostRepository {
	
	PostModel findById(Integer id);
	List<PostModel> findAll( 
			@Param("title") String title, 
			@Param("content") String content, 
			@Param("category_id") String category_id, 
			@Param("user_id") String user_id,
			@Param("is_published") Boolean is_published,
			@Param("sort") String sort,
			@Param("o") String o,
			@Param("offset") Integer offset,
			@Param("limit") Integer limit);
	Integer countAllPaging( 
			@Param("title") String title, 
			@Param("content") String content, 
			@Param("category_id") String category_id, 
			@Param("user_id") String user_id,
			@Param("is_published") Boolean is_published);
	Integer edit (Post post);
	Integer insert (Post post);
	Integer insertTags (PostTag posttag);
	Integer deletePostById(Integer id);
}


