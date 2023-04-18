package com.webktx.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.webktx.entity.LoginRequest;
import com.webktx.entity.Permission;
import com.webktx.entity.ResponseObject;
import com.webktx.entity.Role;
import com.webktx.entity.SignupRequest;
import com.webktx.entity.User;
import com.webktx.model.OptionModel;
import com.webktx.model.PermissionModel;
import com.webktx.model.RoleDetailModel;
import com.webktx.model.UserModel;
import com.webktx.repository.IUserRepository;
import com.webktx.repository.impl.PermissionRepositoryImpl;
import com.webktx.repository.impl.RoleRepositoryImpl;
import com.webktx.security.jwt.JwtUtils;

@Service
public class AuthService {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	IUserRepository userRepository;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	RoleRepositoryImpl roleRepository;
	
	@Autowired
	PermissionRepositoryImpl permissionRepositoryImpl;
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		List<Integer> roleIds = new ArrayList<>();
		List<RoleDetailModel> roleDetailModels = new ArrayList<>();
		RoleDetailModel roleDetailModel = new RoleDetailModel();
		String jwt = "";
		Map<String, Object> result = new TreeMap<>();
		UserModel userModel = userRepository.findByUsername(loginRequest.getUsername());
		if (userModel == null) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(new ResponseObject("ERROR", "Tài khoản không tồn tại", ""));
		}
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		if (userDetails != null) {
			roleIds = roleRepository.findAllRoleIdByUserId(userDetails.getId());
			for (Integer roleId : roleIds) {
				roleDetailModel = roleRepository.findRoleDetailsByRoleId(roleId);
				roleDetailModels.add(roleDetailModel);
			}
			// Summary of role list
			RoleDetailModel roleDetailResult = new RoleDetailModel();

			roleDetailResult = roleDetailModels.get(0);
			Map<String, Object> optionMaps = new LinkedHashMap<>();
			List<Permission> permissions = permissionRepositoryImpl.findAll();
			if (roleDetailModels.size() >= 1) {
				// check each role's permission
				for (int i = 0; i < roleDetailModels.size(); i++) {
					List<OptionModel> optionsModels = roleDetailModels.get(i).getOptions();
					for (int j = 0; j < optionsModels.size(); j++) {
						int perNeededLength = roleDetailResult.getOptions().get(j).getPermissions().size();
						int perAllLegth = permissions.size();
						Map<String, Object> permissionsMap = new LinkedHashMap<>();
						if(perAllLegth == perNeededLength) {
							for (int k = 0; k < permissions.size(); k++) {
								PermissionModel permissionNeeded = roleDetailResult.getOptions().get(j).getPermissions()
										.get(k);
								if (permissions.get(k).getPermissionId() == permissionNeeded.getId()) {
									permissionsMap.put(permissions.get(k).getPermissionName(), true);
								}else {
									permissionsMap.put(permissions.get(k).getPermissionName(), false);
								}
							}
						}else {
							for (int k = 0; k < perAllLegth; k++) {
								boolean isSelect = false;
								for(int m = 0; m < perNeededLength; m++) {
									if( permissions.get(k).getPermissionId() == roleDetailResult.getOptions().get(j).getPermissions().get(m).getId()) {
										permissionsMap.put(permissions.get(k).getPermissionName(), true);
										isSelect = true;
									}
								}
								if(!isSelect) {
									permissionsMap.put(permissions.get(k).getPermissionName(), false);
								}
							}	
						}
						optionMaps.put(optionsModels.get(j).getName(), permissionsMap);
					}
				}
			}

			userModel.setRoleCustom(optionMaps);
			jwt = jwtUtils.generateJwtToken(userDetails);
			result.put("accessToken", jwt);
			result.put("userInfo", userModel);
		}

		return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Đăng nhập hoàn tất", result));
	}
	public ResponseEntity<?> signup(SignupRequest signUpRequest) {
		if (userRepository.checkExistingUserByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Tên đăng nhập đã tồn tại", ""));
		}
		if (userRepository.checkExistingUserByCitizenId(signUpRequest.getCitizenId())) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Số căn cước công dân đã tồn tại", ""));
		}
		if (userRepository.checkExistingEmail(signUpRequest.getEmail())) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Email đã tồn tại", ""));
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    LocalDateTime localDateTime = LocalDateTime.now();
//	    Timestamp timestamp = Timestamp.valueOf(formatter.format(localDateTime));
		User user = new User();
		user.setFullName(signUpRequest.getFullName());
		user.setPhoneNumber(signUpRequest.getPhoneNumber());
		user.setCitizenId(signUpRequest.getCitizenId());
		user.setDob(signUpRequest.getDob().toString());
		user.setCreatedAt(localDateTime);
		user.setUsername(signUpRequest.getUsername());
		user.setEmail(signUpRequest.getEmail());
		user.setPassword(encoder.encode(signUpRequest.getPassword()));
		Role role = new Role();
		// set default role
		role.setRoleId(2);
		user.setRole(role);
		userRepository.add(user);
		return ResponseEntity.ok("User registered successfully!");
	}
//	public ResponseEntity<Object> changePassword(String json) {
//		UserDetailsImpl userDetail = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
//				.getPrincipal();
//		JsonMapper jsonMapper = new JsonMapper();
//		Integer userId = null;
//		String existingPassword = null;
//		String newPassword = null;
//		try {
//			JsonNode jsonObject = jsonMapper.readTree(json);
//			userId = jsonObject.get("userId").asInt();
//			existingPassword = jsonObject.get("existingPassword").asText();
//			newPassword = jsonObject.get("newPassword").asText();
//		} catch (Exception e) {
//			LOGGER.error("Have error at changePassword();", e);
//			return ResponseEntity.status(HttpStatus.OK)
//					.body(new ResponseObject("ERROR", "Cập nhật mật khẩu thất bại", ""));
//		}
//		// check if matches user id
//		if (userDetail.getId().equals(userId)) {
//			if (encoder.matches(existingPassword, userDetail.getPassword())) {
//				String newPasswordEncoder = encoder.encode(newPassword);
//				userDetail.setPassword(newPasswordEncoder);
//				Employee employee = employeeRepository.findById(empId);
//				employee.setPassword(newPasswordEncoder);
//				employeeRepository.edit(employee);
//
//			}else {
//				return ResponseEntity.status(HttpStatus.OK)
//						.body(new ResponseObject("ERROR", "Mật khẩu cũ không chính xác", ""));
//			}
//		} else {
//			return ResponseEntity.status(HttpStatus.OK)
//					.body(new ResponseObject("ERROR", "Không có quyền cập nhật mật khẩu của người khác", ""));
//		}
//		return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Cập nhật mật khẩu thành công",""));
//	}
}
