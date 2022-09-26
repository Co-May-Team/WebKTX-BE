package com.webcmd.model;

import javax.persistence.Entity;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.webcmd.entity.Category;
import com.webcmd.entity.User;

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
	private Integer post_id;
	private String user_name;
	private String category_name;
	private String title;
	private String small_picture_id;
	private String content;
	private String is_published;
	private String publish_date;
	private String created_at;
	private String updated_at;

}
