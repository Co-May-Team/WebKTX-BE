package com.webktx.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.webktx.entity.CommentLike;

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
public class CommentModel {
	Integer id;
	
	private String commentText;
	
    private UserModel user;
	
    private PostModel post;
    private List<CommentModel> childs;
    
	private Integer parentId;
	
    private List<CommentLike> commentLikes;
	
	private Timestamp createdAt;
	
}
