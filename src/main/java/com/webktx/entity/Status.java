package com.webktx.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "statuses")
public class Status {
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Id
	private String code;
	private String label;
}
