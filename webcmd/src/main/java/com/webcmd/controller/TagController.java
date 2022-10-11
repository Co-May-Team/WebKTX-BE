package com.webcmd.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import com.webcmd.service.TagService;


	@RestController
	@RequestMapping("/tag")
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	public class TagController {
		private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
		@Autowired
		TagService tagService;
		
		@GetMapping(value = "/{id}", produces = "application/json")
		public ResponseEntity<Object> findById(@PathVariable Integer id){
			LOGGER.info("Get tag by Id");
			return tagService.findById(id);
		}
		@GetMapping(value= "",produces = "application/json")
		public ResponseEntity<Object> findAll(				
				@RequestParam(value="page",required = false) String page, 
				@RequestParam(value="tag_name", required = false) String tag_name,
				@RequestParam(value="sort", required = false) String sort,
				@RequestParam(value="order", required = false) String order
				) {
			LOGGER.info("Get all tags");
			return tagService.findAll(tag_name, sort,order,page);
		}
		@PostMapping("/insert")
		@ResponseBody
		public ResponseEntity<Object> insert(@RequestBody String json) {
			LOGGER.info("Insert a tag");
			return tagService.insert(json);
		}
		@PutMapping(value = "/edit")
		@ResponseBody
		public ResponseEntity<Object> editPost(@RequestBody String json) {
			LOGGER.info("Edit a tag");
			return tagService.edit(json);
		}
		@DeleteMapping(value = "/delete/{id}")
		public ResponseEntity<Object> deleteTagById(@PathVariable Integer id){
			LOGGER.info("Delete a tag");
				return  tagService.deleteTagById(id);
		}
}
	
