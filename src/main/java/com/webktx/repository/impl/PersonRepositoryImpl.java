package com.webktx.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.webktx.entity.Person;
import com.webktx.entity.Relative;
import com.webktx.entity.Student;
import com.webktx.repository.IPersonRepository;

@Repository
@Transactional(rollbackFor = Exception.class)
public class PersonRepositoryImpl implements IPersonRepository{
	private static final Logger LOGGER = LoggerFactory.getLogger(RelativeRepositoryImpl.class);
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public Integer add(Person person) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Integer id = (Integer) session.save(person);
			return id;
		} catch (Exception e) {
			LOGGER.error("Error has occured at add() ", e);
		}
		return -1;
	}

	@Override
	public Integer edit(Person person) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.update(person);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured at edit() ", e);
			return 0;
		}
	}

	@Override
	public Integer deleteById(Integer personId) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Relative relative = session.find(Relative.class, personId);
			session.remove(relative);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured in delete() ", e);
			return 0;
		}
	}
	@Override
	public Person findByUserId(Integer userId) {
		try {
			Session session = sessionFactory.getCurrentSession();
			String hql = "FROM persons pr WHERE pr.user.userId = :userId";
			Query query = session.createQuery(hql);
			query.setParameter("userId", userId);
			Person person = (Person) query.getSingleResult();
			if(null != person) {
				return person;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	@Override
	public boolean isExistWithUserId(Integer userId) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "SELECT fullname FROM persons as ps where ps.user.userId = :userId";
		try {
			Query query = session.createQuery(hql.toString());
			LOGGER.info(hql.toString());
			query.setParameter("userId", userId);
			String fullname = (String) query.getSingleResult();
			if (!fullname.isEmpty()) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("Error has occured at isExistWithUserId() ", e);
		}
		return false;
	}
	@Override
	public List<Person> findAllAtCurrentYear() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM persons as ps where YEAR(ps.createdAt) = YEAR(CURRENT_DATE()) ";
		List<Person> persons = new ArrayList<>();
		try {
			Query query = session.createQuery(hql);
			persons = query.getResultList();
		} catch (Exception e) {
			LOGGER.error("Error has occured at findAllAtCurrentYear() ", e);
		}
		return persons;
	}
}

