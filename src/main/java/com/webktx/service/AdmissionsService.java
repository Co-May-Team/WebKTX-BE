package com.webktx.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.webktx.entity.ResponseObject;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Service
public class AdmissionsService {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private Environment env;
	@Autowired
	private ResourceLoader resourceLoader;

	public byte[] generateReportFromJson(String json) throws Exception {
		JSONObject jsonObject = new JSONObject(json);
		String templatePath = "";
		templatePath = env.getProperty("report.templatepath");

		// Load .jrxml file from resources
		Resource jrxmlFileResource = resourceLoader.getResource("classpath:/templates/DonXinVaoKTX.jrxml");

		InputStream jrxmlInput = jrxmlFileResource.getInputStream();
		JasperDesign jasperDesign = JRXmlLoader.load(jrxmlInput);

		// Compile .jrxml to .jasper
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

		Map<String, Object> data = new HashMap<>();
		Iterator<String> keys = jsonObject.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			Object value = jsonObject.get(key);
			if (value instanceof JSONArray) {
				// nếu giá trị là một mảng JSON, chuyển đổi thành danh sách
				value = ((JSONArray) value).toList();
			} else if (value instanceof JSONObject) {
				// nếu giá trị là một đối tượng JSON, chuyển đổi thành Map
				value = ((JSONObject) value).toMap();
			}
			data.put(key, value);
		}

		// Create JsonDataSource from json string
		JRDataSource dataSource = new JsonDataSource(new ByteArrayInputStream(json.getBytes()));

		// Set parameters
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("data", data);

		// Fill JasperPrint with data
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		JRPdfExporter exporter = new JRPdfExporter();
//		JRDocxExporter exporter = new JRDocxExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfOutputStream));
		exporter.exportReport();

		// Set headers for the response
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDisposition(ContentDisposition.builder("attachment").filename("DonXetTuyenVaoKtx.pdf").build());

		// Return the response with the DOCX file bytes and headers
		return pdfOutputStream.toByteArray();
	}
}
