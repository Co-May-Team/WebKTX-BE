package com.webktx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webktx.service.UserService;

@Controller
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@PostMapping("/add")
	public ResponseEntity<Object> add(@RequestBody String json){
		return userService.add(json);
	}
	@PutMapping("/edit")
	public ResponseEntity<Object> edit(@RequestBody String json){
		return userService.edit(json);
	}
	@GetMapping("/get-info")
	public ResponseEntity<Object> findByUsername(@RequestParam(value="username") String username){
		return userService.findByUsername(username);
	}
}
