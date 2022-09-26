package com.webcmd.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.webcmd.entity.Category;
import com.webcmd.model.CategoryModel;

public interface ICategoryRepository {
	CategoryModel findById(Integer id);
	List<CategoryModel> findAll( 
			@Param("category_name") String category_name,
			@Param("sort") String sort,
			@Param("order") String order,
			@Param("offset") Integer offset,
			@Param("limit") Integer limit);
	Integer countAllPaging(String category_name);
	Integer edit (Category category);
	Integer insert (Category category);
	Integer deleteCategoryById(Integer id);
}
