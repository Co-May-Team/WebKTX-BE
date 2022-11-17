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
@Entity(name = "roles")
public class Role extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7927497668977446895L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "role_id")
	Integer roleId;
	
	@Column(name = "role_name")
	String roleName;
	
	@Column
	String description;
	
	
}
