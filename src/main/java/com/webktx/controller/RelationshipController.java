package com.webktx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.webktx.entity.ResponseObject;
import com.webktx.repository.impl.RelationshipRepositopryImpl;
import com.webktx.service.RelationshipService;

@Controller
@RequestMapping("/relationships")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RelationshipController {
	@Autowired
	RelationshipService relationshipService;
	@GetMapping(value = "")
	public ResponseEntity<ResponseObject> findAll(){
		return relationshipService.findAll();
	}
}
