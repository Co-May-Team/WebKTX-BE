package com.webktx.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "comments")
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	Integer id;
	
	@Column(name = "comment_text")
	private String commentText;
	
	@OneToOne 
    @JoinColumn(name = "user_id") 
	@JsonIgnore
    private User user;
	
	@OneToOne 
    @JoinColumn(name = "post_id") 
	@JsonIgnore
    private Post post;
	
	@Column(name = "parent_comment_id")
    private Integer parentId;
	
	@OneToMany
    @JoinColumn(name = "comment_id")
	@JsonIgnore
    private List<CommentLike> commentLikes;
	
	@Column(name = "created_at")
	private Timestamp createdAt;
}
