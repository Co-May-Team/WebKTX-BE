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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.webktx.entity.Person;
import com.webktx.entity.Relative;
import com.webktx.entity.Student;
import com.webktx.model.PersonModel;
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
	public PersonModel findModelByUserId(Integer userId) {
		PersonModel personModel = null;
		Person person = null;
		person = findByUserId(userId);
		personModel = toModel(person);
		return personModel;
	}
	@Override
	public PersonModel toModel(Person person) {
		JsonMapper jsonMapper = new JsonMapper(); 
		JsonNode parser = null; 
		//convert to PersonModel start
		PersonModel personModel = new PersonModel();
		personModel.setFullName(person.getFullname());
		personModel.setDateOfBirth(person.getDob());
		personModel.setPhoneNumber(person.getPhoneNumber());
		personModel.setEmail(person.getEmail());
		personModel.setDetailAddress(person.getDetailAddress());
		personModel.setIdNumber(person.getCitizenId());
		personModel.setIdIssueDate(person.getIdIssueDate());
		personModel.setIdIssuePlace(person.getIdIssuePlace());
			// parse to object
		try {
			parser = jsonMapper.readTree(person.getGender());
			personModel.setGender(parser);
			parser = jsonMapper.readTree(person.getEthnic());
			personModel.setEthnic(parser);
			parser = jsonMapper.readTree(person.getReligion());
			personModel.setReligion(parser);
			parser = jsonMapper.readTree(person.getHometown());
			personModel.setHometown(parser);
			parser = jsonMapper.readTree(person.getProvinceAddress());
			personModel.setProvinceAddress(parser);
			parser = jsonMapper.readTree(person.getDistrictAddress());
			personModel.setDistrictAddress(parser);
			parser = jsonMapper.readTree(person.getWardAddress());
			personModel.setWardAddress(parser);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return personModel;
		//convert to PersonModel end
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
	public List<Person> findAllByYear(int year) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM persons as ps where YEAR(ps.createdAt) = :year order by ps.id desc";
		List<Person> persons = new ArrayList<>();
		try {
			Query query = session.createQuery(hql);
			query.setParameter("year", year);
			persons = query.getResultList();
		} catch (Exception e) {
			LOGGER.error("Error has occured at findAllAtCurrentYear() ", e);
		}
		return persons;
	}
	@Override
	public Boolean checkExistingCitizenId(String citizenId, int userId) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder hql = new StringBuilder("SELECT fullname from persons as p where p.citizenId = :citizenId and p.user.userId != :userId");
		Query query = session.createQuery(hql.toString());
		query.setParameter("citizenId",citizenId );
		query.setParameter("userId",userId );
		try {
			String result = (String) query.getSingleResult();
			if (!result.equals("")) {
				return true;
			}
		}
		 catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}
	@Override
	public Boolean checkExistingEmail(String email, int userId) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder hql = new StringBuilder("SELECT fullname from persons as p where p.email = :email and p.user.userId != :userId");
		Query query = session.createQuery(hql.toString());
		query.setParameter("email",email );
		query.setParameter("userId",userId );

		try {
			String result = (String) query.getSingleResult();
			if (!result.isEmpty()) {
				return true;
			}
		}
		 catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}
	@Override
	public Boolean checkExistingPhoneNumber(String phoneNumber, int userId) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder hql = new StringBuilder("SELECT fullname from persons as p where p.phoneNumber = :phoneNumber  and p.user.userId != :userId");
		Query query = session.createQuery(hql.toString());
		query.setParameter("phoneNumber",phoneNumber );
		query.setParameter("userId",userId );

		try {
			String result = (String) query.getSingleResult();
			if (!result.equals("")) {
				return true;
			}
		}
		 catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}
}

