package com.webktx.repository.impl;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.webktx.entity.Relative;
import com.webktx.entity.Student;
import com.webktx.entity.User;
import com.webktx.model.StudentModel;
import com.webktx.repository.IStudentRepository;

@Repository
@Transactional(rollbackFor = Exception.class)
public class StudentRepositoryImpl implements IStudentRepository{
	private static final Logger LOGGER = LoggerFactory.getLogger(RelativeRepositoryImpl.class);
	@Autowired
	private SessionFactory sessionFactory;
	@Override
	public Integer add(Student student) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Integer id = (Integer) session.save(student);
			return id;
		} catch (Exception e) {
			LOGGER.error("Error has occured at add() ", e);
		}
		return -1;
	}

	@Override
	public Integer edit(Student student) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.update(student);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured at edit() ", e);
			return 0;
		}
	}

	@Override
	public Integer deleteById(Integer studentId) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Relative relative = session.find(Relative.class, studentId);
			session.remove(relative);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured in delete() ", e);
			return 0;
		}
	}
	@Override
	public Student findByUserId(Integer userId) {
		try {
			Session session = sessionFactory.getCurrentSession();
			String hql = "FROM students st WHERE st.user.userId = :userId";
			Query query = session.createQuery(hql);
			query.setParameter("userId", userId);
			Student student = (Student) query.getSingleResult();
			if(null != student) {
				return student;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	public StudentModel findModelByUserId(Integer userId) {
		try {
			Student student = null; 
			StudentModel studentModel = null;
			student = findByUserId(userId);
			studentModel = toModel(student);
			return studentModel;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return null;
		}
	}
	public StudentModel toModel(Student student) {
		JsonMapper jsonMapper = new JsonMapper(); 
		JsonNode parser = null; 
		StudentModel studentModel = new StudentModel();
		studentModel.setMajor(student.getMajor());
		studentModel.setClassCode(student.getClassCode());
		studentModel.setStudentCode(student.getStudentCode());
		studentModel.setHighSchoolGraduationExamScore(student.getHighschoolGraduationExamScore());
		studentModel.setDgnlScore(student.getDgnlScore());
		studentModel.setAdmissionViaDirectMethod(student.getAdmissionViaDirectMethod());
		studentModel.setAchievements(student.getAchievements());
		studentModel.setDream(student.getDream());
		studentModel.setFamilyBackground(student.getFamilyBackground());
			// parse to object
			try {
				parser = jsonMapper.readTree(student.getStudentType());
				studentModel.setStudentType(parser);
				parser = jsonMapper.readTree(student.getUniversityName());
				studentModel.setUniversityName(parser);
				parser = jsonMapper.readTree(student.getHighSchoolType());
				studentModel.setHighSchoolType(parser);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		return studentModel;
	}
	
	@Override
	public Integer updateStatusCode(Student student) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.update(student);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured at edit() ", e);
			return 0;
		}
	}
	
}
