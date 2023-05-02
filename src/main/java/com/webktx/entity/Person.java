package com.webktx.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

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
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	@Column(name="created_at")
	private Timestamp createdAt;
	@Column(name="updated_at")
	private Timestamp updatedAt; 
}
