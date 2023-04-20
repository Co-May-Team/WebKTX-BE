package com.webktx.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.webktx.entity.ResponseObject;
import com.webktx.entity.User;
import com.webktx.model.UserModel;
import com.webktx.repository.impl.UserRepositoryImpl;

@Service
public class UserService {

	@Autowired
	UserRepositoryImpl userRepositoryImpl;

	@Autowired
	PasswordEncoder encoder;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	public ResponseEntity<Object> add(String json) {
		JsonNode jsonNode;
		JsonMapper jsonMapper = new JsonMapper();
		String fullName;
		String email;
		String phoneNumber;
		String citizenId;
		String username;
		String password = null;
		User user = null;
		try {
			jsonNode = jsonMapper.readTree(json);
			fullName = jsonNode.get("fullname") != null ? jsonNode.get("fullname").asText() : "";
			email = jsonNode.get("email") != null ? jsonNode.get("email").asText() : "";
			phoneNumber = jsonNode.get("phoneNumber") != null ? jsonNode.get("phoneNumber").asText() : "";
			citizenId = jsonNode.get("citizenId") != null ? jsonNode.get("citizenId").asText() : "";
			username = jsonNode.get("username") != null ? jsonNode.get("username").asText() : "";
			password = jsonNode.get("password") != null ? jsonNode.get("password").asText() : "";
			if (!username.equals("")) {
				Boolean isExisted = userRepositoryImpl.checkExistingUserByUsername(username);
				user = new User();
				if (isExisted) {
					return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "", ""));
				} else {
					user.setFullName(fullName);
					user.setEmail(email);
					user.setPhoneNumber(phoneNumber);
					user.setCitizenId(citizenId);
					user.setUsername(username);
					user.setPassword(encoder.encode(password));
					user = userRepositoryImpl.add(user);
					if (null != user) {
						return ResponseEntity.status(HttpStatus.OK)
								.body(new ResponseObject("OK", "Successfully", user));
					} else {
						return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Failure", ""));
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	public ResponseEntity<Object> findByUsername(String username) {
		UserModel userModel = new UserModel();
		userModel = userRepositoryImpl.findByUsername(username);
		Map<String, Object> result = new HashMap<>(); 
		if (null != userModel) {
			if(userModel.getCitizenId() == null || userModel.getCitizenId().equals("")) {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Chưa có thông tin cccd", result));
			}else {
				result.put("userInfo", userModel);
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("OK", "Successfully", result));
			}
			
		}
		else {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Tài khoản không tồn tại", result));
		}
		
	}

}
