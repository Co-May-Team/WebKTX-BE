package com.webktx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webktx.service.CollegeService;

@Controller
@RestController
@RequestMapping("/admission")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CollegeController {
	
	@Autowired
	CollegeService collegeService;
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@GetMapping(value = "", produces = "application/json; charset=UTF-8")
	public ResponseEntity<Object> findAll(){
		LOGGER.info("Get all colleges");
		return collegeService.findAll();
	}
}
