package com.webktx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webktx.service.TagService;

@RestController
@RequestMapping("/tags")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TagController {
		
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	TagService tagService;
	@GetMapping(value= "",produces = "application/json")
	public ResponseEntity<Object> findAll() {
		LOGGER.info("Get all posts");
		return tagService.findAll();
	}
}
