package com.webktx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.webktx.service.PostService;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostController {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	@Autowired
	PostService postService;
	
	@GetMapping(value = "/test/{id}")
	public String t(@PathVariable Integer id) {
		return "OK";
	}
	
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Object> findById(@PathVariable Integer id){
		LOGGER.info("Get post by Id");
		return postService.findById(id);
	}

	@PostMapping(value= "",produces = "application/json")
	public ResponseEntity<Object> findAll(				
			@RequestBody String json,
			@RequestParam(value="page", required = false) String page,
			@RequestParam(value="sort", required = false) String sort,
			@RequestParam(value="order", required = false) String order
			) {
		LOGGER.info("Get all posts");
		return postService.findAll(json,sort,order,page);
	}
	
	@PreAuthorize("@customRoleService.canUpdate('Post',principal)")
	@PutMapping(value = "/edit")
	@ResponseBody
	public ResponseEntity<Object> editPost(@RequestBody String json) {
		LOGGER.info("Edit a post");
		return postService.edit(json);
	}
	
	@PreAuthorize("@customRoleService.canCreate('Post',principal)")
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Object> add(@RequestBody String json) {
		LOGGER.info("Insert a post");
		return postService.add(json);
	}
	
	@PreAuthorize("@customRoleService.canDelete('Post',principal)")
	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity<Object> deletePostById(@PathVariable Integer id){
		LOGGER.info("Delete a post");
			return postService.deletePostById(id);
	}
	
	
	/** Comment a post **/
	@GetMapping(value = "/comment", produces = "application/json")
	public ResponseEntity<Object> findCommentByPostId(@RequestParam(value="postId", required = true) Integer postId){
		LOGGER.info("Get post by Id");
		return postService.findCommentByPostId(postId);
	}
	
	@PostMapping("/comment/add")
	@ResponseBody
	public ResponseEntity<Object> addComment(@RequestBody String json) {
		return postService.addComment(json);
	}
	@ResponseBody
	@PutMapping("/comment/edit")
	public ResponseEntity<Object> editComment(@RequestBody String json) {
		return postService.editComment(json);
	}
	@ResponseBody
	@DeleteMapping("/comment/delete/{id}")
	public ResponseEntity<Object> editComment(@PathVariable Integer id) {
		return postService.deleteCommentById(id);
	}
	
}