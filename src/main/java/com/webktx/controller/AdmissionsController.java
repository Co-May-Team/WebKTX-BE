package com.webktx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.webktx.entity.ResponseObject;
import com.webktx.repository.impl.StudentRepositoryImpl;
import com.webktx.service.AdmissionsService;
import com.webktx.service.UserDetailsImpl;

@Controller
@RestController
@RequestMapping("/admission")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdmissionsController {
	@Autowired 
	AdmissionsService admissionService;
	@Autowired
	StudentRepositoryImpl studentRepositoryImpl;
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@PostMapping(value= "/submit",produces = {"application/json; charset=UTF-8"})
	@ResponseBody
	public ResponseEntity<ResponseObject> submitForm(				
			@RequestBody String json
			) {
		LOGGER.info("Submit form");
		try {
			return admissionService.submitForm(json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@PostMapping(value= "/upload-files",produces = {"application/json; charset=UTF-8"})
	@ResponseBody
	public ResponseEntity<ResponseObject> uploadFiles (MultipartHttpServletRequest request) {
		LOGGER.info("Submit form");
		try {
			return admissionService.uploadFiles(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@PostMapping(value= "/gen-file",produces = {"application/json; charset=UTF-8"})
	@ResponseBody
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
