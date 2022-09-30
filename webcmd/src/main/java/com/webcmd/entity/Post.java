package com.webcmd.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Entity(name = "posts")
public class Post extends BaseEntity{
	private static final long serialVersionUID =-5140639629424252892L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="post_id")
	private Integer postId;
    @ManyToOne 
    @JoinColumn(name = "user_id") // thông qua khóa ngoại 
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
	//@Column(name="user_id")
    private User userId;
    @ManyToOne 
    @JoinColumn(name = "category_id") // thông qua khóa ngoại 
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    //@Column(name="category_id")
    private Category categoryId;
	private String title;
	@Column(name="small_picture_id")
	private String smallPictureId;
	private String content;
	@Column(name="is_published")
	private Boolean isPublished;
//	@Column(name="publish_date")
//	private Date publishDate;
	

	
}
