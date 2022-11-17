package com.webktx.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "posts")
public class Post extends BaseEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5749599802400649565L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private Integer postId;
	
    @OneToOne 
    @JoinColumn(name = "user_id") // thông qua khóa ngoại 
    private User user;

    @OneToOne 
    @JoinColumn(name = "category_id") // thông qua khóa ngoại 
    private Category category;
     
    private String summary;
    
	private String title;
	
	@Column(name = "small_picture_id")
	private String smallPictureId;
	
	private String content;
	
	@Column(name = "is_published")
	private Boolean isPublished;
	
	@Column(name = "publish_date")
	private LocalDateTime publishDate;
}
