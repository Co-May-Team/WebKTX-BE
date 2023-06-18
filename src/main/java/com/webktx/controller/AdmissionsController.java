package com.webktx.controller;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.webktx.entity.ResponseObject;
import com.webktx.repository.impl.StudentRepositoryImpl;
import com.webktx.service.AdmissionsService;

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

	@PostMapping(value = "/submit", produces = { "application/json; charset=UTF-8" })
	@ResponseBody
	public ResponseEntity<ResponseObject> submitForm(@RequestBody String json) {
		LOGGER.info("Submit form");
		return admissionService.submitForm(json);
	}

	@PostMapping(value = "/upload-files", produces = { "application/json; charset=UTF-8" })
	@ResponseBody
	public ResponseEntity<ResponseObject> uploadFiles(MultipartHttpServletRequest request) {
		LOGGER.info("Submit form");
		return admissionService.uploadFiles(request);
	}

	@PostMapping(value = "/gen-file", produces = { "application/json; charset=UTF-8" })
	@ResponseBody
	public byte[] generateReportFromJson(@RequestBody String json) {
		LOGGER.info("generateReportFromJson()");
		return admissionService.generateReportFromJson(json);
	}

	@GetMapping(value = "/find-all-by-year")
	public ResponseEntity<ResponseObject> findAllByYear(@RequestParam(value = "year", required = true) int year) {
		LOGGER.info("findAllAdmissionByYear()");
		return admissionService.findAllAdmissionByYear(year);
	}

	@GetMapping(value = "/{userId}")
	public ResponseEntity<ResponseObject> findByUserId(@PathVariable Integer userId) {
		LOGGER.info("findAdmissionByUserId()");
		return admissionService.findByUserId(userId);
	}

	@PutMapping(value = "/update-status")
	@ResponseBody
	public ResponseEntity<Object> updateStatusCodeStudent(@RequestBody String json) {
		LOGGER.info("update status code student");
		return admissionService.updateStatusCode(json);
	}

	@GetMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> exportExcel() throws IOException {
		return admissionService.exportExcel();
	}

}
