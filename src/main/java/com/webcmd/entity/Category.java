package com.webcmd.entity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@Entity(name = "categories")
public class Category{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="category_id") 
	private Integer category_id;
	private String category_name;
	private String created_at;
	private String updated_at;
	
	
	
//	@Column(name = "created_at")
//	@Temporal(TemporalType.TIMESTAMP)
//	public String created_at() {
//	    return created_at();
//	}
//	@Column(name = "updated_at")
//	@Temporal(TemporalType.TIMESTAMP)
//	public String updated_at() {
//	    return updated_at();
//	}
	
	
}



