package com.webktx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.webktx.service.CategoryService;


	@RestController
	@RequestMapping("/category")
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	public class CategoryController {
		private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
		@Autowired
		CategoryService categoryService;
		
		@GetMapping(value = "/{id}", produces = "application/json")
		public ResponseEntity<Object> findById(@PathVariable Integer id){
			LOGGER.info("Get category by Id");
			return categoryService.findById(id);
		}
		
		@GetMapping(value= "",produces = "application/json")
		public ResponseEntity<Object> findAll(				
				@RequestParam(value="page",required = false) String page, 
				@RequestParam(value="category_name", required = false) String category_name,
				@RequestParam(value="sort", required = false) String sort,
				@RequestParam(value="order", required = false) String order
				) {
			LOGGER.info("Get all categories");
			return categoryService.findAll(category_name, sort,order,page);
		}
		@PostMapping("/insert")
		@ResponseBody
		public ResponseEntity<Object> insert(@RequestBody String json) {
			LOGGER.info("Insert a category");
			return categoryService.insert(json);
		}
		@PutMapping(value = "/edit")
		@ResponseBody
		public ResponseEntity<Object> editPost(@RequestBody String json) {
			LOGGER.info("Edit a category");
			return categoryService.edit(json);
		}
}
	
