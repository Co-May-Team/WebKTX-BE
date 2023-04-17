package com.webktx.repository.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.webktx.entity.Person;
import com.webktx.entity.Relative;
import com.webktx.entity.Student;
import com.webktx.repository.IPersonRepository;

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
}
