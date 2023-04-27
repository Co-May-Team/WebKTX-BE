package com.webktx.repository.impl;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.webktx.entity.Relative;
import com.webktx.entity.Student;
import com.webktx.entity.User;
import com.webktx.repository.IStudentRepository;

@Repository
@Transactional(rollbackFor = Exception.class)
public class StudentRepositoryImpl implements IStudentRepository{
	private static final Logger LOGGER = LoggerFactory.getLogger(RelativeRepositoryImpl.class);
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public boolean isExistWithUserId(Integer userId) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM students as st where st.user.userId = :userId";
		try {
			Query query = session.createQuery(hql.toString());
			LOGGER.info(hql.toString());
			query.setParameter("userId", userId);
			Student user = (Student) query.getSingleResult();
			if (user!=null) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured at isExistWithUserId() ", e);
		}
		return false;
	}
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
}
