package com.webktx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webktx.service.AdmissionsService;

@Controller
@RestController
@RequestMapping("/admission")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdmissionsController {
	@Autowired 
	AdmissionsService admissionService;
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@PostMapping(value= "",produces = "application/json")
	public byte[] generateReportFromJson(				
			@RequestBody String json
			) {
		LOGGER.info("Get all posts");
		try {
			return admissionService.generateReportFromJson(json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
