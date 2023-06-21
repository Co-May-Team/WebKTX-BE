package com.webktx.entity;

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
	@Column(name="student_type")
	private String studentType;
	@Column(name="university_name")
	private String universityName;
	private String major;
	@Column(name="class_code")
	private String classCode;
	@Column(name="student_code")
	private String studentCode;
	@Column(name="high_school_type")
	private String highSchoolType;
	@Column(name="highschool_graduation_exam_score")
	private String highschoolGraduationExamScore;
	@Column(name="dgnl_score")
	private String dgnlScore;
	@Column(name="admission_via_direct_method")
	private String admissionViaDirectMethod;
	private String achievements;
	private String dream;
	
	@OneToOne
	@JoinColumn(name = "status_code")
	private Status status;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;
	
	@OneToMany()
	@JoinColumn(name = "student_id")
	@JsonIgnore
	private List<Relative> relatives;
	@Column(name="family_background")
	private String familyBackground;
	
	@Column(name="dorm_student_code")
	private String dormStudentCode;
	@Column(name="student_program")
	private String studentProgram;
}
