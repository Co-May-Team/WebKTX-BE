package com.webcmd.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.webcmd.model.TagModel;

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
@Entity(name = "posts")
public class Post extends BaseEntity{
	private static final long serialVersionUID =-5140639629424252892L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="post_id")
	private Integer postId;
    @ManyToOne 
    @JoinColumn(name = "user_id") 
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User userId;
    @ManyToOne 
    @JoinColumn(name = "category_id", nullable = false) 
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Category categoryId;
	private String title;
	@Column(name="small_picture_id")
	private String smallPictureId;
	private String content;
	@Column(name="is_published")
	private Boolean isPublished;
//	@Column(name="publish_date")
//	private Date publishDate;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "posts_tags",joinColumns = 
	@JoinColumn(name = "post_id"), inverseJoinColumns = 
			@JoinColumn(name = "tag_id"))
	private Set<Tag> listTags = new HashSet<>();
	

	
}
