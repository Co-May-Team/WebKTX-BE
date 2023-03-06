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
@Entity(name = "images")
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "image_id")
	Integer id;
	
	@Column(name = "user_id")
	Integer userId;
	
	@Column(name = "image_name")
	String imageName;
	
	@Column(name = "title")
	String title;
}
