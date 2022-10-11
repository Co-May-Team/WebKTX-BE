package com.webcmd.model;

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
public class TagModel {
	private Integer tagId;
	private String tagName;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}

