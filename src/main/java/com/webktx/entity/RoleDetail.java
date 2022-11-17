package com.webktx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "role_details")
public class RoleDetail extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "role_id")
	Integer roleId;
	
	@Id
	@Column(name = "permission_id")
	Integer permissionId;
	
	@Id
	@Column(name = "option_id")
	Integer optionId;
	
	

}
