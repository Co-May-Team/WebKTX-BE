package com.webktx.model;

import java.time.LocalDateTime;

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
	private String userName;
	private String categoryName;
	private String title;
	private String smallPictureId;
	private String content;
	private Boolean isPublished;
	private String summary;
	private LocalDateTime publishDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
