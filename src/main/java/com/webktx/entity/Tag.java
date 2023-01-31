package com.webktx.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tags")
public class Tag {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tag_id")
	private Integer tagId;
	
	@Column(name = "tag_name")
	private String tagName;
	
	@ManyToMany()
	@JsonIgnore
	@JoinTable(name = "posts_tags", joinColumns = {
			@JoinColumn(name = "tag_id") }, inverseJoinColumns = {
					@JoinColumn(name = "post_id") })
	private List<Post> posts;
}
