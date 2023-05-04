package com.webktx.model;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class RelativeModel {
	private Object relationship;
	private Object  status;
	private String fullName;
	private String yearOfBirth;
	private String phoneNumber;
	private Object provinceAddress;
	private Object districtAddress;
	private Object wardAddress;
	private String detailAddress;
	private String currentJob;
	private String phoneNumberOfCompany;
	private String income;
	private String healthStatus;
}
