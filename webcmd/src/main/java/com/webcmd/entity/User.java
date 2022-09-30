package com.webcmd.entity;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id") 
	private Integer userId;
	@Column(name="full_name") 
	private String fullName;
	private String email;
	@Column(name="phonenumber") 
	private String phoneNumber;
	private String cccd;
	@Column(name="username") 
	private String userName;
	private String password;
	
	@OneToMany(mappedBy = "userId")
	Set<Post> posts;
}
