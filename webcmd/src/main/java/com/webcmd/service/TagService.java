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
import com.webcmd.entity.Tag;
import com.webcmd.entity.Pagination;
import com.webcmd.entity.ResponseObject;
import com.webcmd.model.TagModel;
import com.webcmd.repositoryimpl.TagRepositoryImpl;

@Service
public class TagService {
	@Autowired
	TagRepositoryImpl tagRepositoryImpl;
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public ResponseEntity<Object> findById(Integer id){
		Tag tag = new Tag();
		tag = tagRepositoryImpl.findById(id);
		try {
			if (tag.getTagId() != null) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("OK", "Successfully", tag));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ResponseObject("ERROR", "Have error", ""));
			}
		} catch (Exception e) {
			LOGGER.error("An error occurred in tagService | findById", e );
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR", "Have error:" , e.getMessage()));
		}
	}
	public ResponseEntity<Object> findAll(String tag_name, String sort, String order, String page) {
		
		tag_name = tag_name == null ? "" : tag_name;
		order = order == null ? "DESC" : order;
		sort = sort == null ? "tag_id" : sort;
		page = page == null ? "1" : page.trim();
		Integer limit = CMDConstant.LIMIT;
		// Caculator offset
		int offset = (Integer.parseInt(page) - 1) * limit;
		Set<TagModel> tagModelSet = new LinkedHashSet<TagModel>();
		List<TagModel> tagModelListTMP = new ArrayList<TagModel>();
		try {
			tagModelListTMP = tagRepositoryImpl.findAll( tag_name, sort, order, offset, limit);
			for(TagModel tagtModel : tagModelListTMP) {
				tagModelSet.add(tagtModel);
				}
			Integer totalItemTag = tagRepositoryImpl.countAllPaging(tag_name);
			Pagination pagination = new Pagination();
			pagination.setLimit(limit);
			pagination.setPage(Integer.valueOf(page));
			pagination.setTotalItem(totalItemTag);
			Map<String, Object> results = new TreeMap<String, Object>();
			results.put("pagination", pagination);
			results.put("tags", tagModelSet);
			if (results.size() > 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("OK", "Successfully", results));
			} else {
				pagination.setPage(1);
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", results));
			}
		} catch (Exception e) {
			LOGGER.error("An error occurred in tagService ", e );
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR","An error occurred in tagService | findAll", e.getMessage()));
		}

	}
	
	public ResponseEntity<Object> insert(String json) {
		JsonMapper jsonMapper = new JsonMapper();
		JsonNode jsonObjectTag;
		Tag tag = new Tag();
		try {
			jsonObjectTag = jsonMapper.readTree(json);
			String tag_name = jsonObjectTag.get("tag_name") != null ? jsonObjectTag.get("tag_name").asText() : "";
		
			tag.setTagName(tag_name);
			Integer message = tagRepositoryImpl.insert(tag); 
			if ( message == 1) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("OK", "Successfully",tag));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ResponseObject("ERROR", "Can not save a tag", ""));
			}
			
		} catch (Exception e) {
			LOGGER.error("An error occurred in tagService ", e );
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR","An error occurred in tagService | insert", e.getMessage()));
		}
	}
	public ResponseEntity<Object> edit(String json) {
	Tag tag = new Tag();
	JsonMapper jsonMapper = new JsonMapper();
	JsonNode jsonObjectTag;
	try { 
		jsonObjectTag = jsonMapper.readTree(json);
		Integer id = jsonObjectTag.get("tag_id") != null ? jsonObjectTag.get("tag_id").asInt() : 0;
		String name = jsonObjectTag.get("tag_name") != null ? jsonObjectTag.get("tag_name").asText() : "";
		tag.setTagId(id);
		tag.setTagName(name);
		Integer message = tagRepositoryImpl.edit(tag);
		if (message != 0) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully" + "", tag));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseObject("Error", message + "", tag));

		}
	} catch (Exception e) {
		LOGGER.error("An error occurred in tagService ", e );
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR","An error occurred in tagService | edit", e.getMessage()));
		}	
	}
	//delete
		public ResponseEntity<Object> deleteTagById(Integer id){
			Integer updateStatus =  tagRepositoryImpl.deleteTagById(id);
			try {
				if (updateStatus.equals(1)) {
					return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", updateStatus + " ", " "));
			} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ResponseObject("Error", updateStatus + "", ""));
				}
			} catch (Exception e) {
				LOGGER.error("An error occurred in tagService ", e );
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(new ResponseObject("ERROR", "An error occurred in tagService | delete " , e.getMessage()));
				}

			}
}
