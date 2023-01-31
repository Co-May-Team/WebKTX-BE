package com.webktx.model;

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
	private String categoryName;
	private String title;
	private byte[] thumbnail;
	private String content;
	private Boolean isPublished;
	private String summary;	
	private LocalDateTime publishDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
