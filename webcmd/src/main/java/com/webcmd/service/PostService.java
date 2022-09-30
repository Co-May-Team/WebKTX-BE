package com.webcmd.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import com.webcmd.entity.ResponseObject;
import com.webcmd.entity.User;
import com.webcmd.repositoryimpl.PostRepositoryImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.webcmd.constant.CMDConstant;
import com.webcmd.entity.Category;
import com.webcmd.entity.Pagination;
import com.webcmd.entity.Post;
import com.webcmd.model.PostModel;

@Service
public class PostService {
	@Autowired
	PostRepositoryImpl postRepositoryImpl;
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public ResponseEntity<Object> findById(Integer id){
		PostModel post = new PostModel();
		post = postRepositoryImpl.findById(id);
		try {
			if (post.getPostId() != null) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("OK", "Successfully", post));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ResponseObject("ERROR", "Have error ", ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR", "An error occurred in postService | findById " , e.getMessage()));
		}
	}
	public ResponseEntity<Object> findAll( String user_id, String category_id,String title, String content, Boolean is_published, String sort, String o, String page) {
		title = title == null ? "" : title.trim();
		content = content == null ? "" : content.trim();
		category_id = category_id == null ? "" : category_id.trim();
		user_id = user_id == null ? "" : user_id.trim();
		is_published = is_published == null ? null : is_published.booleanValue();
		o = o == null ? "desc" : o;
		sort = sort == null ? "postId" : sort;
		page = page == null ? "1" : page.trim();
		Integer limit = CMDConstant.LIMIT;
		// Caculator offset
		int offset = (Integer.parseInt(page) - 1) * limit;
		Set<PostModel> postModelSet = new LinkedHashSet<PostModel>();
		List<PostModel> postModelListTMP = new ArrayList<PostModel>();
		try {
			postModelListTMP = postRepositoryImpl.findAll( user_id, category_id, title, content,is_published, sort, o, offset, limit);
			for(PostModel postModel : postModelListTMP) {
				postModelSet.add(postModel);
				}
			Integer totalItemPost = postRepositoryImpl.countAllPaging( user_id, category_id, title, content,is_published);
			Pagination pagination = new Pagination();
			pagination.setLimit(limit);
			pagination.setPage(Integer.valueOf(page));
			pagination.setTotalItem(totalItemPost);
			Map<String, Object> results = new TreeMap<String, Object>();
			results.put("pagination", pagination);
			results.put("posts", postModelSet);
			if (results.size() > 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("OK", "Successfully", results));
			} else {
				pagination.setPage(1);
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", results));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR:" + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR","An error occurred in postService | findAll ", e.getMessage()));
		}

	}
	public ResponseEntity<Object> edit(String json) {
		Post post = new Post();
		Category cat = new Category();
		User user = new User();
		JsonMapper jsonMapper = new JsonMapper();
		JsonNode jsonObjectPost;
		try { 
			jsonObjectPost = jsonMapper.readTree(json);
			
			Integer id = jsonObjectPost.get("post_id").asInt();
			String title = jsonObjectPost.get("title") != null ? jsonObjectPost.get("title").asText() : "";
			String content = jsonObjectPost.get("content") != null ? jsonObjectPost.get("content").asText() : "";
			String smallPictureId = jsonObjectPost.get("small_picture_id") != null ? jsonObjectPost.get("small_picture_id").asText() : "";
			Boolean isPublished = jsonObjectPost.get("is_published") != null ? jsonObjectPost.get("is_published").asBoolean() : false;
			Integer categoryId = jsonObjectPost.get("category_id") != null ? jsonObjectPost.get("category_id").asInt() : 0;
			Integer userId = jsonObjectPost.get("user_id") != null ? jsonObjectPost.get("user_id").asInt() : 0;			
			post.setPostId(id);
			post.setTitle(title);
			post.setContent(content);
			post.setSmallPictureId(smallPictureId);
			post.setIsPublished(isPublished);
			cat.setCategoryId(categoryId);
			user.setUserId(userId);
			post.setCategoryId(cat);
			post.setUserId(user);
			Integer message = postRepositoryImpl.edit(post);
			if (message != 0) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully" + "", post));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ResponseObject("Error", message + "", post));

			}
		} catch (Exception e) {
			LOGGER.error("An error occurred in postService | edit ", e );
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error","An error occurred in postService | edit " ,e.getMessage()));
		}
	}
	//insert
	public ResponseEntity<Object> insert(String json) {
		JsonMapper jsonMapper = new JsonMapper();
		JsonNode jsonObjectPost;
		Post post = new Post();
		Category cat = new Category();
		User user = new User();
		try {
			jsonObjectPost = jsonMapper.readTree(json);
			String title = jsonObjectPost.get("title") != null ? jsonObjectPost.get("title").asText() : "";
			String content = jsonObjectPost.get("content") != null ? jsonObjectPost.get("content").asText() : "";
			String smallPictureId = jsonObjectPost.get("small_picture_id") != null ? jsonObjectPost.get("small_picture_id").asText() : "";
			Boolean isPublished = jsonObjectPost.get("is_published") != null ? jsonObjectPost.get("is_published").asBoolean() : false;
			Integer categoryId = jsonObjectPost.get("category_id") != null ? jsonObjectPost.get("category_id").asInt() : 0;
			Integer userId = jsonObjectPost.get("user_id") != null ? jsonObjectPost.get("user_id").asInt() : 0;
			post.setTitle(title);
			post.setContent(content);
			post.setSmallPictureId(smallPictureId);
			post.setIsPublished(isPublished);
			cat.setCategoryId(categoryId);
			user.setUserId(userId);
			post.setCategoryId(cat);
			post.setUserId(user);
			Integer message = postRepositoryImpl.insert(post); 
			if ( message != 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("OK", "Successfully", post));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ResponseObject("ERROR", "Can not save a post", ""));
			}
			
		} catch (Exception e) {
			LOGGER.error("An error occurred in postService | insert ", e );
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR", "An error occurred in postService | insert ", e.getMessage()));
		}
	}
	//delete
	public ResponseEntity<Object> deletePostById(Integer id){
		Integer updateStatus = postRepositoryImpl.deletePostById(id);
		try {
			if (updateStatus.equals(1)) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", updateStatus + " ", " "));
		} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseObject("Error", updateStatus + "", ""));
			}
		} catch (Exception e) {
			LOGGER.error("An error occurred in postService | delete ", e );
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR", "An error occurred in postService | deletePostById " , e.getMessage()));
			}
		}

}
