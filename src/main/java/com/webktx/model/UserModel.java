package com.webktx.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UserModel {
	private Integer id;
	private String username;
	private String fullName;
	private String password;
	private boolean enableLogin;
	private String email;
	private RoleDetailModel role;
	private Map<String, Object> roleCustom;
	private String avatar;
	private boolean isGoogleAccount;
	private boolean isAdmin;
	
}
