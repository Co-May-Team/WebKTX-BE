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
@Entity(name = "permissions")
public class Permission extends BaseEntity {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4760796176172174216L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "permission_id")
	Integer permissionId;
	
	@Column(name = "permission_name")
	String permissionName;
}


