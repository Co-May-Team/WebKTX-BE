package com.webktx.entity;

import java.sql.Timestamp;

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
@Entity(name = "colleges")
public class College {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	
	@Column(name="id") 
	private Integer collegeId;
	
	@Column(name="name") 
	private String collegeName;
	
	@Column(name="created_at") 
	private Timestamp createdAt;
	
	@Column(name="updated_at") 
	private Timestamp updatedAt;
	
	
}
