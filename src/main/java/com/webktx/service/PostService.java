package com.webktx.service;

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
import com.webktx.entity.User;
import com.webktx.model.CategoryModel;
import com.webktx.model.PostModel;
import com.webktx.repository.impl.CategoryRepositoryImpl;
import com.webktx.repository.impl.PostRepositoryImpl;
import com.webktx.repository.impl.RoleRepositoryImpl;
import com.webktx.repository.impl.UserRepositoryImpl;

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

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	public ResponseEntity<Object> findById(Integer id) {
		PostModel post = new PostModel();
		post = postRepositoryImpl.findById(id);
		try {
			if (post.getPostId() != null) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", post));
			} else {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("ERROR", "Have error", ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR", "Have error:", e.getMessage()));
		}
	}

	public ResponseEntity<Object> findAll(String title, String content, String user_id, String category_id,
			String is_published, String sort, String order, String page) {
		title = title == null ? "" : title.trim();
		content = content == null ? "" : content.trim();
		category_id = category_id == null ? "" : category_id.trim();
		user_id = user_id == null ? "" : user_id.trim();
		is_published = is_published == null ? "1" : is_published;
		order = order == null ? "DESC" : order;
		sort = sort == null ? "post_id" : sort;
		page = page == null ? "1" : page.trim();
		Integer limit = Constant.LIMIT;
		// Caculator offset
		int offset = (Integer.parseInt(page) - 1) * limit;
		Set<PostModel> postModelSet = new LinkedHashSet<PostModel>();
		List<PostModel> postModelListTMP = new ArrayList<PostModel>();
		try {
			postModelListTMP = postRepositoryImpl.findAll(title, content, user_id, category_id, is_published, sort,
					order, offset, limit);
			for (PostModel postModel : postModelListTMP) {
				postModelSet.add(postModel);
			}
			Integer totalItemPost = postRepositoryImpl.countAllPaging(title, content, user_id, category_id,
					is_published);
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
			Boolean isPulished = jsonObjectPost.get("isPulished") != null ? jsonObjectPost.get("isPulished").asBoolean()
					: true;
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
			post.setUser(user);
			

			Integer message = postRepositoryImpl.edit(post);
			if (message != 0) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully" + "", toModel(post)));
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
		JsonNode jsonObjectPost;
		Post post = new Post();
		try {
			jsonObjectPost = jsonMapper.readTree(json);
			UserDetailsImpl userDetail = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			
			String title = jsonObjectPost.get("title") != null ? jsonObjectPost.get("title").asText() : "";
			String content = jsonObjectPost.get("content") != null ? jsonObjectPost.get("content").asText() : "";
			String summary = jsonObjectPost.get("summary") != null ? jsonObjectPost.get("summary").asText() : "";
			;
			Integer categoryId = jsonObjectPost.get("summary") != null ? jsonObjectPost.get("category").asInt() : 2;
			Boolean isPulished = jsonObjectPost.get("isPulished") != null ? jsonObjectPost.get("isPulished").asBoolean()
					: true;
			String thumbnail = (jsonObjectPost.get("thumbnail") != null) ? jsonObjectPost.get("thumbnail").asText() : "";
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
			post.setIsPublished(isPulished);
			post.setUser(user);
			if(thumbnail.equals("")) {
				thumbnail = Constant.AVATAR;
			}
			post.setSmallPictureId(thumbnail);

			Integer message = postRepositoryImpl.insert(post);
			if (message != 0) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", toModel(post)));
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
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("Error", updateStatus + "", ""));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR", "Have error delete service: ", e.getMessage()));
		}
	}
	 public PostModel toModel(Post post) {
		 PostModel postModel = new PostModel();
		 postModel.setTitle(post.getTitle());
		 postModel.setSummary(post.getSummary());
		 postModel.setCategoryName(post.getCategory().getCategoryName());
		 postModel.setContent(post.getContent());
		 return postModel;
	 }

}
