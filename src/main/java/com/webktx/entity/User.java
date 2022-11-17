package com.webktx.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User extends BaseEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2434931036656589062L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="user_id") 
	private Integer userId;
	@Column(name = "full_name")
	private String fullName;
	private String email;
	@Column(name = "phone_number")
	private String phoneNumber;
	@Column(name="citizen_id")
	private String citizenId;
	private String username;
	private String password;
	
	@OneToOne
	@JoinColumn(name = "role_id")
	Role role;
	
	@OneToMany // Quan hệ 1-n với đối tượng ở dưới (Person) (1 địa điểm có nhiều người ở)
    @JoinColumn(name = "user_id")
    private List<Post> posts;
	
}
