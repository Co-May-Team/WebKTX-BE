package com.webktx.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.webktx.entity.Relative;
import com.webktx.entity.Student;
import com.webktx.model.StudentModel;

@Repository
@Transactional(rollbackFor = Exception.class)
public interface IStudentRepository {
	Integer add (Student student);
	Integer edit (Student student);
	Integer deleteById (Integer studentId);
	Student findByUserId(Integer userId);
	StudentModel findModelByUserId(Integer userId);
	StudentModel toModel(Student student);
	Integer updateStatusCode(Student student);
}
