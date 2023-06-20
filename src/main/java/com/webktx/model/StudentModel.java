package com.webktx.model;

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
import com.webktx.entity.Relative;
import com.webktx.entity.Status;
import com.webktx.entity.User;

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
public class StudentModel {
	private Object studentType;
	private Object universityName;
	private String major;
	private String classCode;
	private String studentCode;
	private Object highSchoolType;
	private String highSchoolGraduationExamScore;
	private String dgnlScore;
	private String admissionViaDirectMethod;
	private String achievements;
	private String dream;
	private String familyBackground;
	private Object studentProgram;
}
