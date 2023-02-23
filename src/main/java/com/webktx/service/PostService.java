package com.webktx.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.webktx.constant.Constant;
import com.webktx.entity.Category;
import com.webktx.entity.Pagination;
import com.webktx.entity.Post;
import com.webktx.entity.ResponseObject;
import com.webktx.entity.Tag;
import com.webktx.entity.User;
import com.webktx.model.CategoryModel;
import com.webktx.model.PostModel;
import com.webktx.model.TagModel;
import com.webktx.repository.impl.CategoryRepositoryImpl;
import com.webktx.repository.impl.PostRepositoryImpl;
import com.webktx.repository.impl.RoleRepositoryImpl;
import com.webktx.repository.impl.TagRepositoryImpl;
import com.webktx.repository.impl.UserRepositoryImpl;
import com.webktx.ultil.Ultil;

@Service
public class PostService {
	@Autowired
	PostRepositoryImpl postRepositoryImpl;

	@Autowired
	RoleRepositoryImpl roleRepositoryImpl;

	@Autowired
	CategoryRepositoryImpl categoryRepositoryImpl;

	@Autowired
	UserRepositoryImpl userRepositoryImpl;

	@Autowired
	APIService apiService;
	
	@Autowired
	TagRepositoryImpl tagRepositoryImpl;
	
	@Autowired
	Ultil ultil;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	public ResponseEntity<Object> findById(Integer id) {
		PostModel post = new PostModel();
		List<PostModel> relatedPost =  null;
		try {
			post = postRepositoryImpl.findById(id);	
			relatedPost = postRepositoryImpl.findRelatedPosts(10,id);
			Map<String, Object> results = new TreeMap<String, Object>();
			results.put("posts", post);
			results.put("relatedPost", relatedPost);
			if (results.size()>0) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", results));
			} else {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Have error", ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR", "Have error:", e.getMessage()));
		}
	}

	public ResponseEntity<Object> findAll(String json, String sort, String order, String page) {
		JsonNode jsonObject = null;
		JsonMapper jsonMapper = new JsonMapper();
		

		order = order == null ? "DESC" : order;
		sort = sort == null ? "postId" : sort;
		page = (page == null || page == "") ? "1" : page.trim();
		Integer limit = Constant.LIMIT;
		// Caculator offset
		int offset = (Integer.parseInt(page) - 1) * limit;
		Set<PostModel> postModelSet = new LinkedHashSet<PostModel>();
		List<PostModel> postModelListTMP = new ArrayList<PostModel>();
		try {
			jsonObject = jsonMapper.readTree(json);
			
			String title = ((jsonObject.get("title") == null) || (jsonObject.get("title").asText() == "")) ? ""
					: jsonObject.get("title").asText();
			String content = ((jsonObject.get("content") == null) || (jsonObject.get("content").asText() == "")) ? ""
					: jsonObject.get("content").asText();
			Integer category_id = jsonObject.get("category_id") == null  ? 0 : jsonObject.get("category_id").asInt();
			Integer tag_id = jsonObject.get("tag_id") == null  ? 0 : jsonObject.get("tag_id").asInt();
			String user_id = ((jsonObject.get("user_id") == null) || (jsonObject.get("user_id").asText() == "")) ? ""
					: jsonObject.get("user_id").asText();
			postModelListTMP = postRepositoryImpl.findAll(title, content, user_id, category_id,tag_id, sort,
					order, offset, limit);
			for (PostModel postModel : postModelListTMP) {
				postModelSet.add(postModel);
			}
			Integer totalItemPost = postRepositoryImpl.countAllPaging(title, content, user_id, category_id,tag_id);
			Pagination pagination = new Pagination();
			pagination.setLimit(limit);
			pagination.setPage(Integer.valueOf(page));
			pagination.setTotalItem(totalItemPost);
			Map<String, Object> results = new TreeMap<String, Object>();
			results.put("pagination", pagination);
			results.put("posts", postModelSet);

			if (results.size() > 0) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", results));
			} else {
				pagination.setPage(1);
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", results));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR:" + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", e.getMessage(), ""));
		}

	}

	public ResponseEntity<Object> edit(String json) {
		Post post = null;
		Set<Integer> tagIds = new LinkedHashSet();
		List<Tag> tags = new ArrayList<>();
		JsonMapper jsonMapper = new JsonMapper();
		JsonNode jsonObjectPost;
		UserDetailsImpl userDetail = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		try {
			jsonObjectPost = jsonMapper.readTree(json);
			Integer id = jsonObjectPost.get("postId").asInt();
			String title = jsonObjectPost.get("title") != null ? jsonObjectPost.get("title").asText() : "";
			String content = jsonObjectPost.get("content") != null ? jsonObjectPost.get("content").asText() : "";
			String summary = jsonObjectPost.get("summary") != null ? jsonObjectPost.get("summary").asText() : "";
			Integer categoryId = jsonObjectPost.get("summary") != null ? jsonObjectPost.get("category").asInt() : 2;
			Boolean isPulished = jsonObjectPost.get("isPulished") != null ? jsonObjectPost.get("isPulished").asBoolean() : true;
			for (JsonNode jsonNode : jsonObjectPost.get("tagIds")) {
				tagIds.add(jsonNode.asInt());
			}
			String thumbnail = (jsonObjectPost.get("thumbnail") != null
					&& !jsonObjectPost.get("thumbnail").asText().equals("")) ? jsonObjectPost.get("thumbnail").asText()
							: "";
			StringBuilder baseURL = new StringBuilder();
			baseURL.append(Constant.SERVER_IP).append("/api/get-image/");
			if(!thumbnail.equals("") && thumbnail.contains(baseURL)) {
				// Extract image name from link
				thumbnail = thumbnail.replace(baseURL, "");
			}
			CategoryModel categoryModel = categoryRepositoryImpl.findById(categoryId);
			Category category = new Category();
			category.setCategoryId(categoryModel.getCategoryId());
			category.setCategoryName(categoryModel.getCategoryName());
			Integer modifiedBy = userDetail.getId();
			User user = userRepositoryImpl.findById(modifiedBy);
			post = postRepositoryImpl.findPostById(id);
			post.setTitle(title);
			post.setContent(content);
			post.setSummary(summary);
			post.setIsPublished(isPulished);
			post.setCategory(category);			
			post.setTags(tags);
			post.setUser(user);
			post.setSmallPictureId(thumbnail);
			for(Integer tagId: tagIds) {
				Tag tag = tagRepositoryImpl.findById(tagId);
				if(tag != null) {
					tags.add(tag);
				}
			}
			post.setTags(tags);

			Integer message = postRepositoryImpl.edit(post);
			if (message != 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("OK", "Successfully" + "", toModel(post)));
			} else {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("Error", message + "", toModel(post)));

			}
		} catch (Exception e) {
			LOGGER.error("Error has occured in edit()", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
		}
	}

	public ResponseEntity<Object> add(String json) {
		JsonMapper jsonMapper = new JsonMapper();
		Set<Integer> tagIds = new LinkedHashSet<Integer>();
		List<Tag> tags = new ArrayList<>();
		JsonNode jsonObjectPost;
		Post post = new Post();
		PostModel postModel = null;
		LocalDateTime localDateTime = null;
		try {
			jsonObjectPost = jsonMapper.readTree(json);
			UserDetailsImpl userDetail = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();

			String title = jsonObjectPost.get("title") != null ? jsonObjectPost.get("title").asText() : "";
			String content = jsonObjectPost.get("content") != null ? jsonObjectPost.get("content").asText() : "";
			String summary = jsonObjectPost.get("summary") != null ? jsonObjectPost.get("summary").asText() : "";
			for (JsonNode jsonNode : jsonObjectPost.get("tagIds")) {
				tagIds.add(jsonNode.asInt());
			}
			Integer categoryId = jsonObjectPost.get("summary") != null ? jsonObjectPost.get("category").asInt() : 2;
			Boolean isPulished = jsonObjectPost.get("isPublished") != null ? jsonObjectPost.get("isPublished").asBoolean()
					: true;
			String pulishedAt = jsonObjectPost.get("publishedAt") != null ? jsonObjectPost.get("publishedAt").asText() : "";
			if(isPulished) {
				if(!pulishedAt.equals("")) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
					localDateTime = LocalDateTime.parse(pulishedAt, formatter);
				}else {
					localDateTime = LocalDateTime.now();
				}
			}
		    Timestamp timestamp = Timestamp.valueOf(localDateTime);
			String thumbnail = (jsonObjectPost.get("thumbnail") != null
					&& !jsonObjectPost.get("thumbnail").asText().equals("")) ? jsonObjectPost.get("thumbnail").asText()
							: "";
			StringBuilder baseURL = new StringBuilder();
			baseURL.append(Constant.SERVER_IP).append("/api/get-image/");
			if(!thumbnail.equals("") && thumbnail.contains(baseURL)) {
				// Extract image name from link
				thumbnail = thumbnail.replace(baseURL, "");
			}
			if(summary == "") {
				summary = content.substring(0, 255);
				for(int i = summary.length() - 1; i >0 ; i--) {
					if(summary.toCharArray()[i] == ' ') {
						summary = summary.substring(0,i);
						break;
					}
				}
			}
			CategoryModel categoryModel = categoryRepositoryImpl.findById(categoryId);
			Category category = new Category();
			category.setCategoryId(categoryModel.getCategoryId());
			category.setCategoryName(categoryModel.getCategoryName());
			Integer createBy = userDetail.getId();
			User user = userRepositoryImpl.findById(createBy);
			post.setTitle(title);
			post.setContent(content);
			post.setSummary(summary);
			post.setCategory(category);
			post.setPublishedAt(timestamp);
			post.setIsPublished(isPulished);
			for(Integer tagId: tagIds) {
				Tag tag = tagRepositoryImpl.findById(tagId);
				if(tag != null) {
					tags.add(tag);
				}
			}
			post.setTags(tags);
			post.setUser(user);
			if (thumbnail.equals("")) {
				thumbnail = Constant.AVATAR;
			}
			post.setSmallPictureId(thumbnail);

			Integer message = postRepositoryImpl.insert(post);
			postModel = postRepositoryImpl.findById(message);
			if (message != 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("OK", "Successfully", postModel));
			} else {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("ERROR", "Can not save a post", ""));
			}

		} catch (Exception e) {
			LOGGER.debug("ERROR", e);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR", "Have error insert service", e.getMessage()));
		}
	}

	public ResponseEntity<Object> deletePostById(Integer id) {
		Integer updateStatus = postRepositoryImpl.deletePostById(id);
		try {
			if (updateStatus.equals(1)) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", updateStatus + " ", " "));
			} else {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Error", updateStatus + "", ""));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR", "Have error delete service: ", e.getMessage()));
		}
	}

	public PostModel toModel(Post post) {
		List<TagModel> tagModels = new ArrayList<>();
		PostModel postModel = new PostModel();
		postModel.setPostId(post.getPostId());
		postModel.setTitle(post.getTitle());
		postModel.setSummary(post.getSummary());
		CategoryModel categoryModel = new CategoryModel();
		categoryModel.setCategoryId(post.getCategory().getCategoryId());
		categoryModel.setCategoryName(post.getCategory().getCategoryName());
		postModel.setCategory(categoryModel);
		postModel.setContent(post.getContent());
		postModel.setPublishedAt(post.getPublishedAt().toLocalDateTime());
		for(Tag tag : post.getTags()) {
			TagModel tagModel = new TagModel();
			tagModel.setTagId(tag.getTagId());
			tagModel.setTagName(tag.getTagName());
			tagModels.add(tagModel);
		}
		postModel.setTagModels(tagModels);
		postModel.setCreatedAt(post.getCreatedAt());
		postModel.setUpdatedAt(post.getUpdatedAt());
		postModel.setThumbnail(ultil.converImageNameToLink(post.getSmallPictureId()));

		return postModel;
	}

}
