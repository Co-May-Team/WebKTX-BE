package com.webktx.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "students")
public class Student {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	@Column(name="user_id")
	private Integer userId;
	@Column(name="student_type")
	private String studentType;
	@Column(name="university_name")
	private String universityName;
	private String major;
	@Column(name="class_code")
	private String classCode;
	private String gpa10;
	private String gpa11;
	private String gpa12;
	@Column(name="highschool_graduation_exam_score")
	private String highschoolGraduationExamScore;
	@Column(name="dgnl_score")
	private String dgnlScore;
	@Column(name="admission_via_direct_method")
	private String admissionViaDirectMethod;
	private String achievements;
	private String dream;
	
	@OneToMany()
	@JoinColumn(name = "student_id")
	private List<Relative> relatives;
	
}
