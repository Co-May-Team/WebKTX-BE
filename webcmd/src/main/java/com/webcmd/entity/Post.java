package com.webcmd.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
public class Post{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer post_id;
//	@OneToOne()
//	@JoinColumn(name="user_id")
//	private User user_id;
//	@OneToOne()
//	@JoinColumn(name="category_id")
//	private Category category_id;
	
	// Many to One Có nhiều người ở 1 địa điểm.
    @ManyToOne 
    @JoinColumn(name = "user_id") // thông qua khóa ngoại 
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user_id;
 // Many to One Có nhiều người ở 1 địa điểm.
    @ManyToOne 
    @JoinColumn(name = "category_id") // thông qua khóa ngoại 
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Category category_id;
    
	private String title;
	private String small_picture_id;
	private String content;
	private String is_published;
	private String publish_date;
	private String created_at;
	private String updated_at;
}
