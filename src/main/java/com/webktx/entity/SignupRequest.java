package com.webktx.entity;

import java.util.Date;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class SignupRequest {
	@Column(name = "full_name")
	private String fullName;
	private String email;
	@Column(name = "phone_number")
	private String phoneNumber;
	@Column(name="citizen_id")
	private String citizenId;
	private String username;
	private String password;
	private String dob;
}
