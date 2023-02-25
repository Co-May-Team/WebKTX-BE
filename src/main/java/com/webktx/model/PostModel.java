package com.webktx.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class PostModel {
	private Integer postId;
	private List<TagModel> tagModels;
	private String userName;
	private CategoryModel category;
	private String title;
	private String thumbnail;
	private String content;
	private String summary;	
	private LocalDateTime publishedAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private int viewed;

}
