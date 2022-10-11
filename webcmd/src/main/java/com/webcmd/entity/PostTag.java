package com.webcmd.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
@Entity(name = "posts_tags")
@IdClass(PostTag.class)
public class PostTag implements Serializable{
	@Id
	@Column(name="post_id")
    public int postId;
	@Id
	@Column(name="tag_id")
    public int tagId;  
	
}
