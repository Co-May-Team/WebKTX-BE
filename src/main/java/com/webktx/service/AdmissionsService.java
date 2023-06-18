package com.webktx.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.http.entity.StringEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.webktx.constant.Constant;
import com.webktx.entity.Person;
import com.webktx.entity.Relative;
import com.webktx.entity.ResponseObject;
import com.webktx.entity.Status;
import com.webktx.entity.Student;
import com.webktx.entity.User;
import com.webktx.model.AdmissionModel;
import com.webktx.model.CollegeModel;
import com.webktx.model.ImageModel;
import com.webktx.model.PersonModel;
import com.webktx.model.RelativeModel;
import com.webktx.model.StudentModel;
import com.webktx.repository.impl.CollegeRepositoryImpl;
import com.webktx.repository.impl.PersonRepositoryImpl;
import com.webktx.repository.impl.RelativeRepositoryImpl;
import com.webktx.repository.impl.StudentRepositoryImpl;
import com.webktx.ultil.Ultil;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import com.webktx.service.AdmissionsService.ValueFilesUpload;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdmissionsService {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private Environment env;
	@Autowired
	private ResourceLoader resourceLoader;
	@Autowired
	PersonRepositoryImpl personRepositoryImpl;
	@Autowired
	RelativeRepositoryImpl relativeRepositoryImpl;
	@Autowired
	StudentRepositoryImpl studentRepositoryImpl;
	
	

	public ResponseEntity<ResponseObject> submitForm(String json) {
		UserDetailsImpl userDetail = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		try {
			if (personRepositoryImpl.isExistWithUserId(userDetail.getId())) {
				return this.edit(json);
			} else {
				return this.add(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject("ERROR", "Have error when submit form", ""));
	}

	public ResponseEntity<ResponseObject> add(String json) {
		UserDetailsImpl userDetail = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		JsonMapper jsonMapper = new JsonMapper();
		JsonNode jsonObject;
		JsonNode personalObject;
		JsonNode familyObject;
		JsonNode relativesObject;
		JsonNode studentObject;
		List<Relative> relativeList = new ArrayList<>();
		User user = new User();
		user.setUserId(userDetail.getId());
		Status status = new Status();
		status.setCode("CT");
		LocalDateTime localDateTime = null;
		String dormStudentCode = "";
		try {
			jsonObject = jsonMapper.readTree(json);
			personalObject = jsonObject.get("personalInfo");
			familyObject = jsonObject.get("familyInfo");
			relativesObject = familyObject.get("relatives");
			studentObject = jsonObject.get("studentInfo");
			// ---Person-info-start---
			String fullname = ((personalObject.get("fullName") == null)
					|| (personalObject.get("fullName").asText() == "")) ? "" : personalObject.get("fullName").asText();
			String dateOfBirth = ((personalObject.get("dateOfBirth") == null)
					|| (personalObject.get("dateOfBirth").asText() == "")) ? ""
							: personalObject.get("dateOfBirth").asText();
			String gender = ((personalObject.get("gender") == null) || (personalObject.get("gender").toString() == ""))
					? ""
					: personalObject.get("gender").toString();
			String phoneNumber = ((personalObject.get("phoneNumber") == null)
					|| (personalObject.get("phoneNumber").asText() == "")) ? ""
							: personalObject.get("phoneNumber").asText();
			String email = ((personalObject.get("email") == null) || (personalObject.get("email").asText() == "")) ? ""
					: personalObject.get("email").asText();
			String ethnic = ((personalObject.get("ethnic") == null) || (personalObject.get("ethnic").toString() == ""))
					? ""
					: personalObject.get("ethnic").toString();
			String religion = ((personalObject.get("religion") == null)
					|| (personalObject.get("religion").toString() == "")) ? "" : personalObject.get("religion").toString();
			String hometown = ((personalObject.get("hometown") == null)
					|| (personalObject.get("hometown").toString() == "")) ? ""
							: personalObject.get("hometown").toString();
			String provinceAddress = ((personalObject.get("provinceAddress") == null)
					|| (personalObject.get("provinceAddress").toString() == "")) ? ""
							: personalObject.get("provinceAddress").toString();
			String districtAddress = ((personalObject.get("districtAddress") == null)
					|| (personalObject.get("districtAddress").toString() == "")) ? ""
							: personalObject.get("districtAddress").toString();
			String wardAddress = ((personalObject.get("wardAddress") == null)
					|| (personalObject.get("wardAddress").toString() == "")) ? ""
							: personalObject.get("wardAddress").toString();
			String detailAddress = ((personalObject.get("detailAddress") == null)
					|| (personalObject.get("detailAddress").asText() == "")) ? ""
							: personalObject.get("detailAddress").asText();
			String idNumber = ((personalObject.get("idNumber") == null)
					|| (personalObject.get("idNumber").asText() == "")) ? "" : personalObject.get("idNumber").asText();
			String idIssueDate = ((personalObject.get("idIssueDate") == null)
					|| (personalObject.get("idIssueDate").asText() == "")) ? ""
							: personalObject.get("idIssueDate").asText();
			String idIssuePlace = ((personalObject.get("idIssuePlace") == null)
					|| (personalObject.get("idIssuePlace").asText() == "")) ? ""
							: personalObject.get("idIssuePlace").asText();
			if (personRepositoryImpl.checkExistingCitizenId(idNumber,userDetail.getId())) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Số định danh đã tồn tại", ""));
			}
			if (personRepositoryImpl.checkExistingEmail(email,userDetail.getId())) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Email đã tồn tại", ""));
			}
			if (personRepositoryImpl.checkExistingPhoneNumber(phoneNumber,userDetail.getId())) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Số điện thoại đã tồn tại", ""));
			}
			Person person = new Person();
			person.setFullname(fullname);
			person.setDob(dateOfBirth);
			person.setGender(gender);
			person.setPhoneNumber(phoneNumber);
			person.setEmail(email);
			person.setEthnic(ethnic);
			person.setReligion(religion);
			person.setHometown(hometown);
			person.setProvinceAddress(provinceAddress);
			person.setDistrictAddress(districtAddress);
			person.setWardAddress(wardAddress);
			person.setDetailAddress(detailAddress);
			person.setCitizenId(idNumber);
			person.setIdIssueDate(idIssueDate);
			person.setIdIssuePlace(idIssuePlace);
			person.setUser(user);
			localDateTime = LocalDateTime.now();
			person.setCreatedAt(Timestamp.valueOf(localDateTime));
			person.setUpdatedAt(Timestamp.valueOf(localDateTime));
			dormStudentCode = calculationDormStudentCode(personalObject.get("gender").get("value").asText());
			// ---Person-info-end---

			// ---Family-info-start---

			for (JsonNode relative : relativesObject) {
				String relativeRelationship = ((relative.get("relationship") == null)
						|| (relative.get("relationship").toString() == "")) ? ""
								: relative.get("relationship").toString();
				String relativeStatus = ((relative.get("status") == null) || (relative.get("status").toString() == ""))
						? ""
						: relative.get("status").toString();
				String relativeFullname = ((relative.get("fullName") == null)
						|| (relative.get("fullName").asText() == "")) ? "" : relative.get("fullName").asText();
				String relativeYearOfBirth = ((relative.get("yearOfBirth") == null)
						|| (relative.get("yearOfBirth").asText() == "")) ? "" : relative.get("yearOfBirth").asText();
				String relativePhoneNumber = ((relative.get("phoneNumber") == null)
						|| (relative.get("phoneNumber").asText() == "")) ? "" : relative.get("phoneNumber").asText();
				String relativeProvinceAddress = ((relative.get("provinceAddress") == null)
						|| (relative.get("provinceAddress").toString() == "")) ? ""
								: relative.get("provinceAddress").toString();
				String relativeDistrictAddress = ((relative.get("districtAddress") == null)
						|| (relative.get("districtAddress").toString() == "")) ? ""
								: relative.get("districtAddress").toString();
				String relativeWardAddress = ((relative.get("wardAddress") == null)
						|| (relative.get("wardAddress").toString() == "")) ? ""
								: relative.get("wardAddress").toString();
				String relativeDetailAddress = ((relative.get("detailAddress") == null)
						|| (relative.get("detailAddress").asText() == "")) ? ""
								: relative.get("detailAddress").asText();
				String relativeCurrentJob = ((relative.get("currentJob") == null)
						|| (relative.get("currentJob").asText() == "")) ? "" : relative.get("currentJob").asText();
				String relativePlaceOfWork = ((relative.get("placeOfWork") == null)
						|| (relative.get("placeOfWork").asText() == "")) ? "" : relative.get("placeOfWork").asText();
				String relativePhoneNumberOfCompany = ((relative.get("phoneNumberOfCompany") == null)
						|| (relative.get("phoneNumberOfCompany").asText() == "")) ? "" : relative.get("phoneNumberOfCompany").asText();
				String relativeIncome = ((relative.get("income") == null) || (relative.get("income").asText() == ""))
						? ""
						: relative.get("income").asText();
				
				Relative relativeInsert = new Relative();
				relativeInsert.setRelationship(relativeRelationship);
				relativeInsert.setStatus(relativeStatus);
				relativeInsert.setFullname(relativeFullname);
				relativeInsert.setYearOfBirth(relativeYearOfBirth);
				relativeInsert.setPhoneNumber(relativePhoneNumber);
				relativeInsert.setProvinceAddress(relativeProvinceAddress);
				relativeInsert.setDistrictAddress(relativeDistrictAddress);
				relativeInsert.setWardAddress(relativeWardAddress);
				relativeInsert.setDetailAddress(relativeDetailAddress);
				relativeInsert.setCurrentJob(relativeCurrentJob);
				relativeInsert.setPlaceOfWork(relativePlaceOfWork);
				relativeInsert.setPhoneNumberOfCompany(relativePhoneNumberOfCompany);
				relativeInsert.setIncome(relativeIncome);
				relativeInsert.setUser(user);
				relativeList.add(relativeInsert);

			}
			String familyBackground = ((familyObject.get("familyBackground") == null)
					|| (familyObject.get("familyBackground").asText() == "")) ? ""
							: familyObject.get("familyBackground").asText();
			// ---Family-info-end---

			// ---Student-info-start---
			String studentStudentType = ((studentObject.get("studentType") == null)
					|| (studentObject.get("studentType").toString() == "")) ? ""
							: studentObject.get("studentType").toString();
			String studentUniversityName = ((studentObject.get("universityName") == null)
					|| (studentObject.get("universityName").toString() == "")) ? ""
							: studentObject.get("universityName").toString();
			String studentMajor = ((studentObject.get("major") == null) || (studentObject.get("major").asText() == ""))
					? ""
					: studentObject.get("major").asText();
			String studentClassCode = ((studentObject.get("classCode") == null)
					|| (studentObject.get("classCode").asText() == "")) ? "" : studentObject.get("classCode").asText();
			String studentStudentCode = ((studentObject.get("studentCode") == null)
					|| (studentObject.get("studentCode").asText() == "")) ? ""
							: studentObject.get("studentCode").asText();
			String studentHighSchoolGraduationExamScore = ((studentObject.get("highSchoolGraduationExamScore") == null)
					|| (studentObject.get("highSchoolGraduationExamScore").asText() == "")) ? ""
							: studentObject.get("highSchoolGraduationExamScore").asText();
			String studentDgnlScore = ((studentObject.get("dgnlScore") == null)
					|| (studentObject.get("dgnlScore").asText() == "")) ? "" : studentObject.get("dgnlScore").asText();
			String studentAdmissionViaDirectMethod = ((studentObject.get("admissionViaDirectMethod") == null)
					|| (studentObject.get("admissionViaDirectMethod").asText() == "")) ? ""
							: studentObject.get("admissionViaDirectMethod").asText();
			String studentAchievements = ((studentObject.get("achievements") == null)
					|| (studentObject.get("achievements").asText() == "")) ? ""
							: studentObject.get("achievements").asText();
			String studentDream = ((studentObject.get("dream") == null) || (studentObject.get("dream").asText() == ""))
					? ""
					: studentObject.get("dream").asText();
			String highSchoolType = ((studentObject.get("highSchoolType") == null)
					|| (studentObject.get("highSchoolType").toString() == "")) ? ""
							: studentObject.get("highSchoolType").toString();
			Student student = new Student();
			student.setStudentType(studentStudentType);
			student.setUniversityName(studentUniversityName);
			student.setMajor(studentMajor);
			student.setClassCode(studentClassCode);
			student.setStudentCode(studentStudentCode);
			student.setHighSchoolType(highSchoolType);
			student.setHighschoolGraduationExamScore(studentHighSchoolGraduationExamScore);
			student.setDgnlScore(studentDgnlScore);
			student.setAdmissionViaDirectMethod(studentAdmissionViaDirectMethod);
			student.setAchievements(studentAchievements);
			student.setDream(studentDream);
			student.setFamilyBackground(familyBackground);
			student.setUser(user);
			student.setStatus(status);
			student.setStudentCodeDorm(dormStudentCode);
			// ---Student-info-end---

			/* Insert data */
			if (personRepositoryImpl.add(person) < 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("ERROR", "Have error when insert person", ""));
			}
			if (studentRepositoryImpl.add(student) < 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("ERROR", "Have error when insert student", ""));
			}
			for (Relative r : relativeList) {
				if (relativeRepositoryImpl.add(r) < 0) {
					return ResponseEntity.status(HttpStatus.OK)
							.body(new ResponseObject("ERROR", "Have error when insert relative", ""));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured in submit admission form", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", ""));
	}

	public ResponseEntity<ResponseObject> edit(String json) {
		UserDetailsImpl userDetail = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		JsonMapper jsonMapper = new JsonMapper();
		JsonNode jsonObject;
		JsonNode personalObject;
		JsonNode familyObject;
		JsonNode relativesObject;
		JsonNode studentObject;
		User user = new User();
		user.setUserId(userDetail.getId());
		Person person = personRepositoryImpl.findByUserId(userDetail.getId());
		Student student = studentRepositoryImpl.findByUserId(userDetail.getId());
		List<Relative> relativeList = new ArrayList<>();
		LocalDateTime localDateTime = null;
		boolean deleteRelative = relativeRepositoryImpl.deleteAllByUserId(userDetail.getId());
		if(deleteRelative == false) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(new ResponseObject("ERROR", "Have error when update relative", ""));
		}
		try {
			jsonObject = jsonMapper.readTree(json);
			personalObject = jsonObject.get("personalInfo");
			familyObject = jsonObject.get("familyInfo");
			relativesObject = familyObject.get("relatives");
			studentObject = jsonObject.get("studentInfo");
			// ---Person-info-start---
			String fullname = ((personalObject.get("fullName") == null)
					|| (personalObject.get("fullName").asText() == "")) ? "" : personalObject.get("fullName").asText();
			String dateOfBirth = ((personalObject.get("dateOfBirth") == null)
					|| (personalObject.get("dateOfBirth").asText() == "")) ? ""
							: personalObject.get("dateOfBirth").asText();
			String gender = ((personalObject.get("gender") == null) || (personalObject.get("gender").toString() == ""))
					? ""
					: personalObject.get("gender").toString();
			String phoneNumber = ((personalObject.get("phoneNumber") == null)
					|| (personalObject.get("phoneNumber").asText() == "")) ? ""
							: personalObject.get("phoneNumber").asText();
			String email = ((personalObject.get("email") == null) 
					|| (personalObject.get("email").asText() == "")) ? ""
							: personalObject.get("email").asText();
			String ethnic = ((personalObject.get("ethnic") == null) 
					|| (personalObject.get("ethnic").toString() == "")) ? "" 
							: personalObject.get("ethnic").toString();
			String religion = ((personalObject.get("religion") == null)
					|| (personalObject.get("religion").toString() == "")) ? ""
							: personalObject.get("religion").toString();
			String hometown = ((personalObject.get("hometown") == null)
					|| (personalObject.get("hometown").toString() == "")) ? ""
							: personalObject.get("hometown").toString();
			String provinceAddress = ((personalObject.get("provinceAddress") == null)
					|| (personalObject.get("provinceAddress").toString() == "")) ? ""
							: personalObject.get("provinceAddress").toString();
			String districtAddress = ((personalObject.get("districtAddress") == null)
					|| (personalObject.get("districtAddress").toString() == "")) ? ""
							: personalObject.get("districtAddress").toString();
			String wardAddress = ((personalObject.get("wardAddress") == null)
					|| (personalObject.get("wardAddress").toString() == "")) ? ""
							: personalObject.get("wardAddress").toString();
			String detailAddress = ((personalObject.get("detailAddress") == null)
					|| (personalObject.get("detailAddress").asText() == "")) ? ""
							: personalObject.get("detailAddress").asText();
			String idNumber = ((personalObject.get("idNumber") == null)
					|| (personalObject.get("idNumber").asText() == "")) ? "" : personalObject.get("idNumber").asText();
			String idIssueDate = ((personalObject.get("idIssueDate") == null)
					|| (personalObject.get("idIssueDate").asText() == "")) ? ""
							: personalObject.get("idIssueDate").asText();
			String idIssuePlace = ((personalObject.get("idIssuePlace") == null)
					|| (personalObject.get("idIssuePlace").asText() == "")) ? ""
							: personalObject.get("idIssuePlace").asText();
			if (personRepositoryImpl.checkExistingCitizenId(idNumber,userDetail.getId())) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Số định danh đã tồn tại", ""));
			}
			if (personRepositoryImpl.checkExistingEmail(email,userDetail.getId())) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Email đã tồn tại", ""));
			}
			if (personRepositoryImpl.checkExistingPhoneNumber(phoneNumber,userDetail.getId())) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Số điện thoại đã tồn tại", ""));
			}
			person.setFullname(fullname);
			person.setDob(dateOfBirth);
			person.setGender(gender);
			person.setPhoneNumber(phoneNumber);
			person.setEmail(email);
			person.setEthnic(ethnic);
			person.setReligion(religion);
			person.setHometown(hometown);
			person.setProvinceAddress(provinceAddress);
			person.setDistrictAddress(districtAddress);
			person.setWardAddress(wardAddress);
			person.setDetailAddress(detailAddress);
			person.setCitizenId(idNumber);
			person.setIdIssueDate(idIssueDate);
			person.setIdIssuePlace(idIssuePlace);
			localDateTime = LocalDateTime.now();
			person.setUpdatedAt(Timestamp.valueOf(localDateTime));
			// ---Person-info-end---

			// ---Family-info-start---
				// Relatives-start
			for (JsonNode relative : relativesObject) {
				String relativeRelationship = ((relative.get("relationship") == null)
						|| (relative.get("relationship").toString() == "")) ? ""
								: relative.get("relationship").toString();
				String relativeStatus = ((relative.get("status") == null) || (relative.get("status").toString() == ""))
						? ""
						: relative.get("status").toString();
				String relativeFullname = ((relative.get("fullName") == null)
						|| (relative.get("fullName").asText() == "")) ? "" : relative.get("fullName").asText();
				String relativeYearOfBirth = ((relative.get("yearOfBirth") == null)
						|| (relative.get("yearOfBirth").asText() == "")) ? "" : relative.get("yearOfBirth").asText();
				String relativePhoneNumber = ((relative.get("phoneNumber") == null)
						|| (relative.get("phoneNumber").asText() == "")) ? "" : relative.get("phoneNumber").asText();
				String relativeProvinceAddress = ((relative.get("provinceAddress") == null)
						|| (relative.get("provinceAddress").toString() == "")) ? ""
								: relative.get("provinceAddress").toString();
				String relativeDistrictAddress = ((relative.get("districtAddress") == null)
						|| (relative.get("districtAddress").toString() == "")) ? ""
								: relative.get("districtAddress").toString();
				String relativeWardAddress = ((relative.get("wardAddress") == null)
						|| (relative.get("wardAddress").toString() == "")) ? ""
								: relative.get("wardAddress").toString();
				String relativeDetailAddress = ((relative.get("detailAddress") == null)
						|| (relative.get("detailAddress").asText() == "")) ? ""
								: relative.get("detailAddress").asText();
				String relativeCurrentJob = ((relative.get("currentJob") == null)
						|| (relative.get("currentJob").asText() == "")) ? "" : relative.get("currentJob").asText();
				String relativePlaceOfWork = ((relative.get("placeOfWork") == null)
						|| (relative.get("placeOfWork").asText() == "")) ? "" : relative.get("placeOfWork").asText();
				String relativePhoneNumberOfCompany = ((relative.get("phoneNumberOfCompany") == null)
						|| (relative.get("phoneNumberOfCompany").asText() == "")) ? "" : relative.get("phoneNumberOfCompany").asText();

				String relativeIncome = ((relative.get("income") == null) || (relative.get("income").asText() == ""))
						? ""
						: relative.get("income").asText();

				Relative relativeInsert = new Relative();
				relativeInsert.setRelationship(relativeRelationship);
				relativeInsert.setStatus(relativeStatus);
				relativeInsert.setFullname(relativeFullname);
				relativeInsert.setYearOfBirth(relativeYearOfBirth);
				relativeInsert.setPhoneNumber(relativePhoneNumber);
				relativeInsert.setProvinceAddress(relativeProvinceAddress);
				relativeInsert.setDistrictAddress(relativeDistrictAddress);
				relativeInsert.setWardAddress(relativeWardAddress);
				relativeInsert.setDetailAddress(relativeDetailAddress);
				relativeInsert.setCurrentJob(relativeCurrentJob);
				relativeInsert.setPlaceOfWork(relativePlaceOfWork);
				relativeInsert.setPhoneNumberOfCompany(relativePhoneNumberOfCompany);
				relativeInsert.setIncome(relativeIncome);
				relativeInsert.setUser(user);
				relativeList.add(relativeInsert);

			}
				// Relatives-end
			String familyBackground = ((familyObject.get("familyBackground") == null)
					|| (familyObject.get("familyBackground").asText() == "")) ? ""
							: familyObject.get("familyBackground").asText();
			// ---Family-info-end---

			// ---Student-info-start---
			String studentStudentType = ((studentObject.get("studentType") == null)
					|| (studentObject.get("studentType").toString() == "")) ? ""
							: studentObject.get("studentType").toString();
			String studentUniversityName = ((studentObject.get("universityName") == null)
					|| (studentObject.get("universityName").toString() == "")) ? ""
							: studentObject.get("universityName").toString();
			String studentMajor = ((studentObject.get("major") == null) || (studentObject.get("major").asText() == ""))
					? ""
					: studentObject.get("major").asText();
			String studentClassCode = ((studentObject.get("classCode") == null)
					|| (studentObject.get("classCode").asText() == "")) ? "" : studentObject.get("classCode").asText();
			String studentStudentCode = ((studentObject.get("studentCode") == null)
					|| (studentObject.get("studentCode").asText() == "")) ? ""
							: studentObject.get("studentCode").asText();
			String studentHighSchoolGraduationExamScore = ((studentObject.get("highSchoolGraduationExamScore") == null)
					|| (studentObject.get("highSchoolGraduationExamScore").asText() == "")) ? ""
							: studentObject.get("highSchoolGraduationExamScore").asText();
			String studentDgnlScore = ((studentObject.get("dgnlScore") == null)
					|| (studentObject.get("dgnlScore").asText() == "")) ? "" : studentObject.get("dgnlScore").asText();
			String studentAdmissionViaDirectMethod = ((studentObject.get("admissionViaDirectMethod") == null)
					|| (studentObject.get("admissionViaDirectMethod").asText() == "")) ? ""
							: studentObject.get("admissionViaDirectMethod").asText();
			String studentAchievements = ((studentObject.get("achievements") == null)
					|| (studentObject.get("achievements").asText() == "")) ? ""
							: studentObject.get("achievements").asText();
			String studentDream = ((studentObject.get("dream") == null) || (studentObject.get("dream").asText() == ""))
					? ""
					: studentObject.get("dream").asText();
			String highSchoolType = ((studentObject.get("highSchoolType") == null)
					|| (studentObject.get("highSchoolType").toString() == "")) ? ""
							: studentObject.get("highSchoolType").toString();
			student.setStudentType(studentStudentType);
			student.setUniversityName(studentUniversityName);
			student.setMajor(studentMajor);
			student.setClassCode(studentClassCode);
			student.setStudentCode(studentStudentCode);
			student.setHighSchoolType(highSchoolType);
			student.setHighschoolGraduationExamScore(studentHighSchoolGraduationExamScore);
			student.setDgnlScore(studentDgnlScore);
			student.setAdmissionViaDirectMethod(studentAdmissionViaDirectMethod);
			student.setAchievements(studentAchievements);
			student.setDream(studentDream);
			student.setFamilyBackground(familyBackground);
			// ---Student-info-end---

			/* Insert data */
			if (personRepositoryImpl.edit(person) < 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("ERROR", "Have error when update person", ""));
			}
			if (studentRepositoryImpl.edit(student) < 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("ERROR", "Have error when update student", ""));
			}
			for (Relative r : relativeList) {
				// all relative have been delete before
				if (relativeRepositoryImpl.add(r) < 0) {
					return ResponseEntity.status(HttpStatus.OK)
							.body(new ResponseObject("ERROR", "Have error when update relative", ""));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured in submit admission form", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", ""));
	}

	public byte[] generateReportFromJson(String json){
//		Gson gson = new Gson();
//		JsonObject jObject = gson.fromJson(json, JsonObject.class); // parse
//		jObject.addProperty("version", "v3"); // modify
//		json = jObject.toString();
		StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
		JSONObject jsonObject = new JSONObject(entity);

		// Load .jrxml file from resources
		try {
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
//			JRDocxExporter exporter = new JRDocxExporter();
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfOutputStream));
			exporter.exportReport();

			// Set headers for the response
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//			headers.setContentDisposition(
//					ContentDisposition.builder("attachment").filename("DonXetTuyenVaoKtx.pdf").build());

			// Return the response with the DOCX file bytes and headers
			return pdfOutputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public ResponseEntity<ResponseObject> uploadFiles(MultipartHttpServletRequest request) {
		UserDetailsImpl userDetail = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Person person = personRepositoryImpl.findByUserId(userDetail.getId());
		Student student = studentRepositoryImpl.findByUserId(userDetail.getId());
//			// Lay r ds file
		MultiValueMap<String, MultipartFile> form = request.getMultiFileMap();
//		List<MultipartFile> files = form.get("file");
		List<MultipartFile> file1 = form.get(ValueFilesUpload.KEY_APP);
//		List<MultipartFile> file2 = form.get(ValueFilesUpload.KEY_TRANS);
		List<MultipartFile> file3 = form.get(ValueFilesUpload.KEY_PERSON);
		List<MultipartFile> file4 = form.get(ValueFilesUpload.KEY_PHOTO);
//		List<MultipartFile> file5 = form.get(ValueFilesUpload.KEY_HOUSE);
		Map<String, Object> files = new LinkedHashMap<>();
		// Dat ten folder (userId-fullName)
		StringBuilder nameConverted = new StringBuilder(removeDiacritic(person.getFullname()));
		String[] strList = nameConverted.toString().split(" ");
		nameConverted.setLength(0);
		for (int i = 0; i < strList.length; i++) {
			nameConverted.append(strList[i]);
		}
		StringBuilder title = new StringBuilder();
		title.append(String.valueOf(userDetail.getId()));
		title.append("-");
		title.append(nameConverted);
		StringBuilder pathSaveFile = new StringBuilder(System.getProperty("user.dir"));
		List<MultipartFile> listImageSave = new ArrayList<>();
		List<ImageModel> imageModels = new ArrayList<>();
//		B1: lay ra duong dan se luu file cho tung ca nhan
		pathSaveFile.append(Constant.URL_ADMISSON_FILE_UPLOAD);
		File dirToSave = new File(pathSaveFile.toString());
		if (!dirToSave.exists()) {
			if (dirToSave.mkdir()) {
				System.out.println("Directory Created" + pathSaveFile);
			}
		}
		// Xoa file va folder da ton tai
		deleteFolder(dirToSave, String.valueOf(userDetail.getId()));
		pathSaveFile.append(title + "/");
		dirToSave = new File(pathSaveFile.toString());
		if (!dirToSave.exists()) {
			if (dirToSave.mkdir()) {
				System.out.println("Directory Created" + pathSaveFile);
			}
		}
//		for (MultipartFile mpf : files) {
//			String filename = StringUtils.cleanPath(mpf.getOriginalFilename());
//			try {
//				Path path = Paths.get(pathSaveFile.toString() + filename);
//				Files.copy(mpf.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
		if(file1!=null) {
			for (MultipartFile mpf : file1) {
				String[] extentions = mpf.getOriginalFilename().split("\\.");
				String ex = extentions[extentions.length-1];
				StringBuilder filename = new StringBuilder(ValueFilesUpload.VALUE_APP);
				filename.append("-");
				filename.append(nameConverted);
				filename.append("-");
				filename.append(student.getStudentCodeDorm());
				filename.append(".").append(ex);
				files.put(filename.toString(), mpf);
			}
		}

//		for (MultipartFile mpf : file2) {
//			String[] extentions = mpf.getOriginalFilename().split("\\.");
//			String ex = extentions[extentions.length-1];
//			StringBuilder filename = new StringBuilder(ValueFilesUpload.VALUE_TRANS);
//			filename.append("-");
//			filename.append(nameConverted);
//			filename.append("-");
//			filename.append(student.getStudentCodeDorm());
//			filename.append(".").append(ex);
//			files.put(filename.toString(), mpf);
//		}
		if(file3!=null) {
			for (MultipartFile mpf : file3) {
				String[] extentions = mpf.getOriginalFilename().split("\\.");
				String ex = extentions[extentions.length-1];
				StringBuilder filename = new StringBuilder(ValueFilesUpload.VALUE_PERSON);
				filename.append("-");
				filename.append(nameConverted);
				filename.append("-");
				filename.append(student.getStudentCodeDorm());
				filename.append(".").append(ex);
				files.put(filename.toString(), mpf);
				
			}
		}
		if(file4!=null) {
			for (MultipartFile mpf : file4) {
				String[] extentions = mpf.getOriginalFilename().split("\\.");
				String ex = extentions[extentions.length-1];
				StringBuilder filename = new StringBuilder(ValueFilesUpload.VALUE_PHOTO);
				filename.append("-");
				filename.append(nameConverted);
				filename.append("-");
				filename.append(student.getStudentCodeDorm());
				filename.append(".").append(ex);
				files.put(filename.toString(), mpf);
				
			}
		}
//		for (MultipartFile mpf : file5) {
//			String[] extentions = mpf.getOriginalFilename().split("\\.");
//			String ex = extentions[extentions.length-1];
//			StringBuilder filename = new StringBuilder(ValueFilesUpload.VALUE_HOUSE);
//			filename.append("-");
//			filename.append(nameConverted);
//			filename.append("-");
//			filename.append(student.getStudentCodeDorm());
//			filename.append(".").append(ex);
//			files.put(filename.toString(), mpf);
//		}
		for(Map.Entry<String, Object> entry : files.entrySet()) {
			MultipartFile mpf = (MultipartFile) entry.getValue();
			try {
				Path path = Paths.get(pathSaveFile.toString() + entry.getKey());
				Files.copy(mpf.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Upload Successfully", ""));
	}

	public static String removeDiacritic(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
	}

	public static void deleteFolder(File srcfolder, String startWith) {
		File[] files = srcfolder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory() && file.getName().startsWith(startWith)) {
					deleteFolder(file, startWith);
				} else {
					if (srcfolder.getName().startsWith(startWith)) {
						file.delete();
					}
				}
			}
			if (srcfolder.getName().startsWith(startWith)) {
				srcfolder.delete();
			}
		}
	}
	public String calculationDormStudentCode(String gender) {
		/**
		 Format: Term + (Nam ? 01 : Nữ ? 02) + (count by gender  + 1) 
		 */
		int yearOfFoundation = 2016;
		String term = LocalDateTime.now().getYear() - yearOfFoundation + 1 + "";
		if(Integer.valueOf(term)<10) {
			term = "0" + term;
		}
		String result = "";
		
		JsonMapper jsonMapper = new JsonMapper();
		JsonNode genderNode = null;
		List<Person> persons = personRepositoryImpl.findAllByYear(LocalDateTime.now().getYear());
		int countByGender = 0;
		for(Person p : persons) {
			try {
				genderNode = jsonMapper.readTree(p.getGender());
				if(genderNode.get("value").asText().equals(gender)) {
					countByGender++;
				}
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		countByGender = countByGender + 1;
		result = term + (gender.equals("Nam") ? "01" : "02") + (countByGender < 10 ? "00" + countByGender : "0" + countByGender);
		return result;
	}
	public ResponseEntity<ResponseObject> findAllAdmissionByYear(int year){
		List<Person> persons = personRepositoryImpl.findAllByYear(year);
		List<AdmissionModel> admissionModelList = new ArrayList<>();
		List<Integer> userIdInvalidForm = new ArrayList<>();
		for(Person p : persons) {
			Student student = studentRepositoryImpl.findByUserId(p.getUser().getUserId());
			if(student==null) {
				userIdInvalidForm.add(p.getUser().getUserId());
				continue;
			}
			Status status = student.getStatus();
			AdmissionModel admissionModel = new AdmissionModel();
			admissionModel.setStudentCodeDorm(student.getStudentCodeDorm());
			admissionModel.setUserId(p.getUser().getUserId());
			admissionModel.setFullname(p.getFullname());
			admissionModel.setDob(p.getDob());
			admissionModel.setPhone(p.getPhoneNumber());
			admissionModel.setEmail(p.getEmail());
			admissionModel.setStatus(status.getLabel());
			if(p.getUser().isGoogleAccount()) {
				admissionModel.setAvatar(p.getUser().getAvatar());
			}else if(p.getUser().getAvatar()!=null){
				admissionModel.setAvatar(Ultil.converBaseImageNameToLink(p.getUser().getAvatar()) );
			}
			
			admissionModelList.add(admissionModel);
		}
		if(!admissionModelList.isEmpty() && userIdInvalidForm.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully",admissionModelList));
		}else if(!admissionModelList.isEmpty() && !userIdInvalidForm.isEmpty()){
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Thông tin tuyển sinh không hoàn chỉnh " + userIdInvalidForm ,admissionModelList));
		}else {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Not found",admissionModelList));
		}
	}
	public ResponseEntity<ResponseObject> findByUserId(int userId){
		
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		JsonMapper jsonMapper = new JsonMapper(); 
		JsonNode parser = null; 
		
		try {
			PersonModel personModel = personRepositoryImpl.findModelByUserId(userId);
			StudentModel studentModel = studentRepositoryImpl.findModelByUserId(userId);
			if(personModel==null) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Chưa có hồ sơ" ,result));
			}
			if(studentModel==null) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Thông tin tuyển sinh không hoàn chỉnh",result));
			}
			List<RelativeModel> relativeModelList = new ArrayList<>();
			relativeModelList = relativeRepositoryImpl.findModelByUserId(userId);
			Map<String, Object> familyInfoMap = new LinkedHashMap<String, Object>();
			Map<String, byte[]> fileUuploadContent = new LinkedHashMap<>();
			List<Object> fileUuploadContentList = new ArrayList<>();
			StringBuilder usrDir = new StringBuilder(Constant.USR_DIR).append(Constant.URL_ADMISSON_FILE_UPLOAD);
			File dir = new File(usrDir.toString());
			if(dir.exists()) {
				fileUuploadContent = getFileContentMap(dir,String.valueOf(userId), fileUuploadContent);
				for(Map.Entry<String, byte[]> entry : fileUuploadContent.entrySet()) {
					Map<String, Object> tmp = new LinkedHashMap<>();
					tmp.put("fileName", entry.getKey());
					tmp.put("fileContent", entry.getValue());
					fileUuploadContentList.add(tmp);
				}
			}
			
			familyInfoMap.put("relatives", relativeModelList);
			familyInfoMap.put("familyBackground", studentModel.getFamilyBackground());
			result.put("personalInfo", personModel);
			result.put("familyInfo", familyInfoMap);
			result.put("studentInfo", studentModel);
			result.put("fileUploaded", fileUuploadContentList);
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully",result));
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", e.getMessage(),result));
		}
	}
	public Map<String,byte[]> getFileContentMap(File srcDir, String startWith, Map<String,byte[]> fileContentMap){
		File[] files = srcDir.listFiles();
		if(files!=null) {
			for(File file : files) {
				if(file.isDirectory() && file.getName().startsWith(startWith)) {
					getFileContentMap(file, startWith,fileContentMap);
				}else if(file.isFile()){
					byte[] fileByte = null;
					try {
						InputStream in = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
						fileByte = IOUtils.toByteArray(in);
						fileContentMap.put(file.getName(), fileByte);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return fileContentMap;
	}
	public static class ValueFilesUpload { 
		public static final String KEY_APP = "application";
		public static final String KEY_TRANS = "transcriptAndAchievements";
		public static final String KEY_PERSON = "personalProfile";
		public static final String KEY_PHOTO = "photo";
		public static final String KEY_HOUSE = "houseImage";
		
		public static final String VALUE_APP = "DonXinVaoKTX";
		public static final String VALUE_TRANS = "HocBaVaThanhTich";
		public static final String VALUE_PERSON = "LyLichCaNhan";
		public static final String VALUE_PHOTO = "AnhThe";
		public static final String VALUE_HOUSE = "AnhNgoiNha";
		
	}
	
	public ResponseEntity<Object> updateStatusCode(String json) {
		JsonNode jsonNode;
		JsonMapper jsonMapper = new JsonMapper();
		String statusCode;
		Integer userId;
		try {
			jsonNode = jsonMapper.readTree(json);
			userId = jsonNode.get("userId") != null ? jsonNode.get("userId").asInt() : null;
			statusCode = jsonNode.get("statusCode") != null ? jsonNode.get("statusCode").asText() : "";
			Student student = studentRepositoryImpl.findByUserId(userId);
			Status status = new Status();
			status.setCode(statusCode);
			student.setStatus(status);
			if (studentRepositoryImpl.updateStatusCode(student) < 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("ERROR", "Have error when update status code student", ""));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", ""));
	}
	public ResponseEntity<byte[]> exportExcel() throws IOException {
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("TS2023");

        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Mã sinh viên",
                "Họ và tên",
                "Ngày sinh",
                "Giới tính",
                "Số điện thoại",
                "Email",
                "Địa chỉ thường trú",
                "Trường",
                "Ghi chú",
        };

        CellStyle headerCellStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerCellStyle.setFont(headerFont);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }
        
        List<Person> persons = personRepositoryImpl.findAllByYear(2023);
		List<AdmissionModel> admissionModelList = new ArrayList<>();
		List<Integer> userIdInvalidForm = new ArrayList<>();
        int rowNum = 1;
		for(Person p : persons) {
			Student student = studentRepositoryImpl.findByUserId(p.getUser().getUserId());
			if(student==null) {
				userIdInvalidForm.add(p.getUser().getUserId());
				continue;
			}
			Status status = student.getStatus();

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(student.getStudentCodeDorm());
            row.createCell(1).setCellValue(p.getFullname());
            row.createCell(2).setCellValue(p.getDob());
            row.createCell(3).setCellValue(getValueJson(p.getGender(), "value"));
            row.createCell(4).setCellValue(p.getPhoneNumber());
            row.createCell(5).setCellValue(p.getEmail());
            StringBuilder addressDetail = new StringBuilder(p.getDetailAddress());
            addressDetail.append(Constant.COMMA);
            addressDetail.append(getValueJson(p.getWardAddress(), "name"));
            addressDetail.append(Constant.COMMA);
            addressDetail.append(getValueJson(p.getDistrictAddress(),"name"));
            addressDetail.append(Constant.COMMA);
            addressDetail.append(getValueJson(p.getProvinceAddress(),"name"));
            row.createCell(6).setCellValue(addressDetail.toString());
            row.createCell(7).setCellValue(getValueJson(student.getUniversityName(),"label"));
		}

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();

		byte[] excelBytes = outputStream.toByteArray();

		HttpHeaders headersAPI = new HttpHeaders();
		headersAPI.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headersAPI.setContentDispositionFormData("attachment", "dsts2023.xlsx");

		return ResponseEntity.ok().headers(headersAPI).body(excelBytes);
	}
	
	private String getValueJson(String json, String key) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
		try {
			jsonNode = objectMapper.readTree(json);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        String value = jsonNode.get(key).asText();
        return value;
	}
}
