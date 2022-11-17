package com.webktx.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

import com.webktx.entity.LoginRequest;
import com.webktx.entity.Permission;
import com.webktx.entity.ResponseObject;
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
}
