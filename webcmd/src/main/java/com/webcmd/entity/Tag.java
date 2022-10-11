package com.webcmd.entity;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tags")
	public class Tag extends BaseEntity{
	private static final long serialVersionUID = -5140639629424252892L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="tag_id") 
	private Integer tagId;
	@Column(name="tag_name") 
	private String tagName;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="listTags")
	private Set<Post> listTags = new HashSet<>();

	
	
	
}