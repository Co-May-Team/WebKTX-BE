package com.webktx.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.entity.StringEntity;
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
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.webktx.entity.Person;
import com.webktx.entity.Relative;
import com.webktx.entity.ResponseObject;
import com.webktx.entity.Status;
import com.webktx.entity.Student;
import com.webktx.entity.User;
import com.webktx.model.ImageModel;
import com.webktx.repository.impl.PersonRepositoryImpl;
import com.webktx.repository.impl.RelativeRepositoryImpl;
import com.webktx.repository.impl.StudentRepositoryImpl;

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

@Service
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
		if (studentRepositoryImpl.isExistWithUserId(userDetail.getId())) {
			return this.edit(json);
		} else {
			return this.add(json);
		}
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
		status.setCode("CD");
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
			String ethnic = ((personalObject.get("ethnic") == null) || (personalObject.get("ethnic").asText() == ""))
					? ""
					: personalObject.get("ethnic").asText();
			String religion = ((personalObject.get("religion") == null)
					|| (personalObject.get("religion").asText() == "")) ? "" : personalObject.get("religion").asText();
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
				String relativeIncome = ((relative.get("income") == null) || (relative.get("income").asText() == ""))
						? ""
						: relative.get("income").asText();
				String relativeHealthStatus = ((relative.get("healthStatus") == null)
						|| (relative.get("healthStatus").asText() == "")) ? "" : relative.get("healthStatus").asText();

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
				relativeInsert.setIncome(relativeIncome);
				relativeInsert.setHealthStatus(relativeHealthStatus);
				relativeInsert.setUser(user);
				relativeList.add(relativeInsert);

			}
			String relativeFamilyBackground = ((familyObject.get("familyBackground") == null)
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
			String studentAverageGrade10 = ((studentObject.get("averageGrade10") == null)
					|| (studentObject.get("averageGrade10").asText() == "")) ? ""
							: studentObject.get("averageGrade10").asText();
			String studentAverageGrade11 = ((studentObject.get("averageGrade11") == null)
					|| (studentObject.get("averageGrade11").asText() == "")) ? ""
							: studentObject.get("averageGrade11").asText();
			String studentAverageGrade12 = ((studentObject.get("averageGrade12") == null)
					|| (studentObject.get("averageGrade12").asText() == "")) ? ""
							: studentObject.get("averageGrade12").asText();
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
			Student student = new Student();
			student.setStudentType(studentStudentType);
			student.setUniversityName(studentUniversityName);
			student.setMajor(studentMajor);
			student.setClassCode(studentClassCode);
			student.setStudentCode(studentStudentCode);
			student.setGpa10(studentAverageGrade10);
			student.setGpa11(studentAverageGrade11);
			student.setGpa12(studentAverageGrade12);
			student.setHighschoolGraduationExamScore(studentHighSchoolGraduationExamScore);
			student.setDgnlScore(studentDgnlScore);
			student.setAdmissionViaDirectMethod(studentAdmissionViaDirectMethod);
			student.setAchievements(studentAchievements);
			student.setDream(studentDream);
			student.setFamilyBackground(relativeFamilyBackground);
			student.setUser(user);
			student.setStatus(status);
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
		Person person = personRepositoryImpl.findByUserId(userDetail.getId());
		Student student = studentRepositoryImpl.findByUserId(userDetail.getId());
		List<Relative> relativeList = relativeRepositoryImpl.findByUserId(userDetail.getId());
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
			String ethnic = ((personalObject.get("ethnic") == null) || (personalObject.get("ethnic").asText() == ""))
					? ""
					: personalObject.get("ethnic").asText();
			String religion = ((personalObject.get("religion") == null)
					|| (personalObject.get("religion").asText() == "")) ? "" : personalObject.get("religion").asText();
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
				String relativeIncome = ((relative.get("income") == null) || (relative.get("income").asText() == ""))
						? ""
						: relative.get("income").asText();
				String relativeHealthStatus = ((relative.get("healthStatus") == null)
						|| (relative.get("healthStatus").asText() == "")) ? "" : relative.get("healthStatus").asText();
				for (Relative relativeUpdate : relativeList) {
					relativeUpdate.setRelationship(relativeRelationship);
					relativeUpdate.setStatus(relativeStatus);
					relativeUpdate.setFullname(relativeFullname);
					relativeUpdate.setYearOfBirth(relativeYearOfBirth);
					relativeUpdate.setPhoneNumber(relativePhoneNumber);
					relativeUpdate.setProvinceAddress(relativeProvinceAddress);
					relativeUpdate.setDistrictAddress(relativeDistrictAddress);
					relativeUpdate.setWardAddress(relativeWardAddress);
					relativeUpdate.setDetailAddress(relativeDetailAddress);
					relativeUpdate.setCurrentJob(relativeCurrentJob);
					relativeUpdate.setIncome(relativeIncome);
					relativeUpdate.setHealthStatus(relativeHealthStatus);
				}
			}
			String relativeFamilyBackground = ((familyObject.get("familyBackground") == null)
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
			String studentAverageGrade10 = ((studentObject.get("averageGrade10") == null)
					|| (studentObject.get("averageGrade10").asText() == "")) ? ""
							: studentObject.get("averageGrade10").asText();
			String studentAverageGrade11 = ((studentObject.get("averageGrade11") == null)
					|| (studentObject.get("averageGrade11").asText() == "")) ? ""
							: studentObject.get("averageGrade11").asText();
			String studentAverageGrade12 = ((studentObject.get("averageGrade12") == null)
					|| (studentObject.get("averageGrade12").asText() == "")) ? ""
							: studentObject.get("averageGrade12").asText();
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
			student.setStudentType(studentStudentType);
			student.setUniversityName(studentUniversityName);
			student.setMajor(studentMajor);
			student.setClassCode(studentClassCode);
			student.setStudentCode(studentStudentCode);
			student.setGpa10(studentAverageGrade10);
			student.setGpa11(studentAverageGrade11);
			student.setGpa12(studentAverageGrade12);
			student.setHighschoolGraduationExamScore(studentHighSchoolGraduationExamScore);
			student.setDgnlScore(studentDgnlScore);
			student.setAdmissionViaDirectMethod(studentAdmissionViaDirectMethod);
			student.setAchievements(studentAchievements);
			student.setDream(studentDream);
			student.setFamilyBackground(relativeFamilyBackground);
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
				if (relativeRepositoryImpl.edit(r) < 0) {
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

	public byte[] generateReportFromJson(String json) throws Exception {
		StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
		JSONObject jsonObject = new JSONObject(entity);
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
		headers.setContentDisposition(
				ContentDisposition.builder("attachment").filename("DonXetTuyenVaoKtx.pdf").build());

		// Return the response with the DOCX file bytes and headers
		return pdfOutputStream.toByteArray();
	}

	public ResponseEntity<ResponseObject> uploadFiles(MultipartHttpServletRequest request) {
		UserDetailsImpl userDetail = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Person person = personRepositoryImpl.findByUserId(userDetail.getId());
//			// Lay r ds file
		MultiValueMap<String, MultipartFile> form = request.getMultiFileMap();
		List<MultipartFile> files = form.get("file");
		// Dat ten folder (userId-fullName)
		String nameConverted = removeDiacritic(person.getFullname());
		String[] strList = nameConverted.split(" ");
		StringBuilder title = new StringBuilder();
		title.append(String.valueOf(userDetail.getId()));
		title.append("-");
		for (int i = 0; i < strList.length; i++) {
			title.append(strList[i]);
		}

		StringBuilder pathSaveFile = new StringBuilder(System.getProperty("user.dir"));
		List<MultipartFile> listImageSave = new ArrayList<>();
		List<ImageModel> imageModels = new ArrayList<>();
//		B1: lay ra duong dan se luu file cho tung ca nhan
		pathSaveFile.append("/TuyenSinh/");
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
		for (MultipartFile mpf : files) {
			String filename = StringUtils.cleanPath(mpf.getOriginalFilename());
	        try {
	            Path path = Paths.get(pathSaveFile.toString() + filename);
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
                        deleteFolder(file,startWith);
                    } else {
	                	if(srcfolder.getName().startsWith(startWith)) {
	                		file.delete();
	                	}
                    }
                }
                if(srcfolder.getName().startsWith(startWith)) {
                	srcfolder.delete();
                }
            }
    }
}
