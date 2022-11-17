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
@Entity(name = "categories")
public class Category extends BaseEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5140639629424252892L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="category_id") 
	private Integer categoryId;
	
	@Column(name="category_name") 
	private String categoryName;	
}



