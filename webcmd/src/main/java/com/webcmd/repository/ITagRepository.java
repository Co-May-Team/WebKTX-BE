package com.webcmd.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.webcmd.entity.Tag;
import com.webcmd.model.TagModel;

public interface ITagRepository {
	Tag findById(Integer id);
	List<TagModel> findAll( 
			@Param("tag_name") String tag_name,
			@Param("sort") String sort,
			@Param("order") String order,
			@Param("offset") Integer offset,
			@Param("limit") Integer limit);
	Integer countAllPaging(String tag_name);
	Integer edit (Tag tag);
	Integer insert (Tag tag);
	Integer deleteTagById(Integer id);
}
