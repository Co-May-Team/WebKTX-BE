package com.webktx.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.webktx.model.OptionModel;
import com.webktx.model.PermissionModel;

@Service("customRoleService")
public class CustomRoleService {
	public static final String CREATE = "Create";
	public static final String UPDATE = "Update";
	public static final String DELETE = "Delete";

	public static String getTokenFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");

		if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			return token.substring(7, token.length());
		}

		return null;
	}

	public boolean canCreate(String option, UserDetailsImpl userDetail) {
		return authorization(option, userDetail, CREATE);
	}

	public boolean canUpdate(String option, UserDetailsImpl userDetail) {
		return authorization(option, userDetail, UPDATE);
	}

	public boolean canDelete(String option, UserDetailsImpl userDetail) {
		return authorization(option, userDetail, DELETE);
	}

	public boolean isTheSameUser(Integer empId, UserDetailsImpl userDetail) {
		if (userDetail.getId().equals(empId)) {
			return true;
		}
		return false;
	}

	public boolean authorization(String option, UserDetailsImpl userDetail, String action) {
		// loop through options in role
		for (OptionModel optionModel : userDetail.getRoles().getOptions()) {
			// If same with option => // loop through permission in option
			if (optionModel.getName().equals(option)) {
				for (PermissionModel p : optionModel.getPermissions()) {
					if (p.getName().equals(action)) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
