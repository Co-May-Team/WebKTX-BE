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
public class PersonModel {
	private String fullName;
	private String dateOfBirth;
	private Object gender;
	private String phoneNumber;
	private String email;
	private Object ethnic;
	private Object religion;
	private Object hometown;
	private Object provinceAddress;
	private Object districtAddress;
	private Object wardAddress;
	private String detailAddress;
	private String idNumber;
	private String idIssueDate;
	private String idIssuePlace;
}
