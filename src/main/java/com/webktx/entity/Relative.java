package com.webktx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "relatives")
public class Relative {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	private String relationship;
	private String status;
	private String fullname;
	@Column(name="year_of_birth")
	private String yearOfBirth;
	@Column(name="phone_number")
	private String phoneNumber;
	@Column(name="province_address")
	private String provinceAddress;
	@Column(name="district_address")
	private String districtAddress;
	@Column(name="ward_address")
	private String wardAddress;
	@Column(name="detail_address")
	private String detailAddress;
	@Column(name="current_job")
	private String currentJob;
	@Column(name="place_of_work")
	private String placeOfWork;
	@Column(name="phone_number_of_company")
	private String phoneNumberOfCompany;
	private String income;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;
}
