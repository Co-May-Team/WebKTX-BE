package com.webcmd.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.webcmd.constant.CMDConstant;
import com.webcmd.entity.Category;
import com.webcmd.entity.Pagination;
import com.webcmd.entity.ResponseObject;
import com.webcmd.model.CategoryModel;
import com.webcmd.repositoryimpl.CategoryRepositoryImpl;

@Service
public class CategoryService {
	@Autowired
	CategoryRepositoryImpl categoryRepositoryImpl;
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public ResponseEntity<Object> findById(Integer id){
		Category category = new Category();
		category = categoryRepositoryImpl.findById(id);
		try {
			if (category.getCategoryId() != null) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("OK", "Successfully", category));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ResponseObject("ERROR", "Have error", ""));
			}
		} catch (Exception e) {
			LOGGER.error("An error occurred in categoryService |findById ", e );
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR", "Have error:" , e.getMessage()));
		}
	}
	public ResponseEntity<Object> findAll(String category_name, String sort, String order, String page) {
		
		category_name = category_name == null ? "" : category_name;
		order = order == null ? "DESC" : order;
		sort = sort == null ? "category_id" : sort;
		page = page == null ? "1" : page.trim();
		Integer limit = CMDConstant.LIMIT;
		// Caculator offset
		int offset = (Integer.parseInt(page) - 1) * limit;
		Set<CategoryModel> categoryModelSet = new LinkedHashSet<CategoryModel>();
		List<CategoryModel> categoryModelListTMP = new ArrayList<CategoryModel>();
		try {
			categoryModelListTMP = categoryRepositoryImpl.findAll( category_name, sort, order, offset, limit);
			for(CategoryModel categorytModel : categoryModelListTMP) {
				categoryModelSet.add(categorytModel);
				}
			Integer totalItemCategory = categoryRepositoryImpl.countAllPaging(category_name);
			Pagination pagination = new Pagination();
			pagination.setLimit(limit);
			pagination.setPage(Integer.valueOf(page));
			pagination.setTotalItem(totalItemCategory);
			Map<String, Object> results = new TreeMap<String, Object>();
			results.put("pagination", pagination);
			results.put("categories", categoryModelSet);
			if (results.size() > 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("OK", "Successfully", results));
			} else {
				pagination.setPage(1);
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", results));
			}
		} catch (Exception e) {
			LOGGER.error("An error occurred in categoryService ", e );
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR","An error occurred in categoryService | findAll", e.getMessage()));
		}

	}
	
	public ResponseEntity<Object> insert(String json) {
		JsonMapper jsonMapper = new JsonMapper();
		JsonNode jsonObjectCategory;
		Category category = new Category();
		try {
			jsonObjectCategory = jsonMapper.readTree(json);
			String category_name = jsonObjectCategory.get("category_name") != null ? jsonObjectCategory.get("category_name").asText() : "";
		
			category.setCategoryName(category_name);
			Integer message = categoryRepositoryImpl.insert(category); 
			if ( message == 1) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("OK", "Successfully",category));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ResponseObject("ERROR", "Can not save a category", ""));
			}
			
		} catch (Exception e) {
			LOGGER.error("An error occurred in categoryService ", e );
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR","An error occurred in categoryService | insert", e.getMessage()));
		}
	}
	public ResponseEntity<Object> edit(String json) {
	Category category = new Category();
	JsonMapper jsonMapper = new JsonMapper();
	JsonNode jsonObjectCategory;
	try { 
		jsonObjectCategory = jsonMapper.readTree(json);
		Integer id = jsonObjectCategory.get("category_id") != null ? jsonObjectCategory.get("category_id").asInt() : 0;
		String name = jsonObjectCategory.get("category_name") != null ? jsonObjectCategory.get("category_name").asText() : "";
		category.setCategoryId(id);
		category.setCategoryName(name);
		Integer message = categoryRepositoryImpl.edit(category);
		if (message != 0) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully" + "", category));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseObject("Error", message + "", category));

		}
	} catch (Exception e) {
		LOGGER.error("An error occurred in categoryService ", e );
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR","An error occurred in categoryService | edit", e.getMessage()));
		}	
	}
	//delete
		public ResponseEntity<Object> deleteCategoryById(Integer id){
			Integer updateStatus =  categoryRepositoryImpl.deleteCategoryById(id);
			try {
				if (updateStatus.equals(1)) {
					return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", updateStatus + " ", " "));
			} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ResponseObject("Error", updateStatus + "", ""));
				}
			} catch (Exception e) {
				LOGGER.error("An error occurred in categoryService ", e );
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(new ResponseObject("ERROR", "An error occurred in categoryService | delete " , e.getMessage()));
				}

			}
}
