package com.webktx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "persons")
public class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	@Column(name="user_id")
	private String userId;
	private String fullname;
	private String dob;
	private String gender;
	@Column(name="phone_number")
	private String phoneNumber;
	private String email;
	private String ethnic;
	private String religion;
	private String hometown;
	@Column(name="province_address")
	private String provinceAddress;
	@Column(name="district_address")
	private String districtAddress;
	@Column(name="ward_address")
	private String wardAddress;
	@Column(name="detail_address")
	private String detailAddress;
	@Column(name="citizen_id")
	private String citizenId;
	@Column(name="idIssue_date")
	private String idIssueDate;
	@Column(name="idIssue_place")
	private String idIssuePlace;
}
