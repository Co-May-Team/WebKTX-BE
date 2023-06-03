package com.webktx.repository.impl;

import java.util.ArrayList;
import java.util.Iterator;
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
import com.webktx.entity.Post;
import com.webktx.entity.Relative;
import com.webktx.model.RelativeModel;
import com.webktx.repository.IRelativeRepository;
@Repository
@Transactional(rollbackFor = Exception.class)
public class RelativeRepositoryImpl implements IRelativeRepository{
	private static final Logger LOGGER = LoggerFactory.getLogger(RelativeRepositoryImpl.class);

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public Integer add(Relative relative) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Integer id = (Integer) session.save(relative);
			return id;
		} catch (Exception e) {
			LOGGER.error("Error has occured at add() ", e);
		}
		return -1;
	}

	@Override
	public Integer edit(Relative relative) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.update(relative);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured at edit() ", e);
			return 0;
		}
	}

	@Override
	public Integer deleteById(Integer relativeId) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Relative relative = session.find(Relative.class, relativeId);
			session.remove(relative);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured in delete() ", e);
			return 0;
		}
	}
	@Override
	public List<Relative> findByUserId(Integer userId) {
		List<Relative> relativeList = new ArrayList<>();
		try {
			Session session = sessionFactory.getCurrentSession();
			String hql = "FROM relatives rl WHERE rl.user.userId = :userId";
			Query query = session.createQuery(hql);
			query.setParameter("userId", userId);
			relativeList = query.getResultList();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		return relativeList;
	}
	@Override
	public List<RelativeModel> findModelByUserId(Integer userId) {
		List<Relative> relativeList = new ArrayList<>();
		List<RelativeModel> relativeModelList = new ArrayList<>();
		relativeList = findByUserId(userId);
		relativeModelList = toModel(relativeList);
		return relativeModelList;
	}
	@Override
	public List<RelativeModel> toModel(List<Relative> relativeList) {
		JsonMapper jsonMapper = new JsonMapper(); 
		JsonNode parser = null; 
		List<RelativeModel> relativeModelList = new ArrayList<>();
		for(Relative relative : relativeList) {
			RelativeModel relativeModel = new RelativeModel();
			relativeModel.setFullName(relative.getFullname());
			relativeModel.setYearOfBirth(relative.getYearOfBirth());
			relativeModel.setPhoneNumber(relative.getPhoneNumber());
			relativeModel.setDetailAddress(relative.getDetailAddress());
			relativeModel.setCurrentJob(relative.getCurrentJob());
			relativeModel.setPlaceOfWork(relative.getPlaceOfWork());
			relativeModel.setPhoneNumberOfCompany(relative.getPhoneNumberOfCompany());
			relativeModel.setIncome(relative.getIncome());
				// parse to object
				try {
					parser = jsonMapper.readTree(relative.getRelationship());
					relativeModel.setRelationship(parser);
					parser = jsonMapper.readTree(relative.getStatus());
					relativeModel.setStatus(parser);
					parser = jsonMapper.readTree(relative.getProvinceAddress());
					relativeModel.setProvinceAddress(parser);
					parser = jsonMapper.readTree(relative.getDistrictAddress());
					relativeModel.setDistrictAddress(parser);
					parser = jsonMapper.readTree(relative.getWardAddress());
					relativeModel.setWardAddress(parser);
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			relativeModelList.add(relativeModel);
		}
		
		return relativeModelList;
	}
	
	@Override
	public boolean deleteAllByUserId(Integer userId) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "DELETE relatives rl where rl.user.userId = :userId";
		try {
			Query query = session.createQuery(hql);
			query.setParameter("userId", userId);
			int result = query.executeUpdate();
			if (result >= 0) {
				StringBuilder noticeMessage = new StringBuilder("All relatives of user ");
				noticeMessage.append(userId);
				noticeMessage.append("have been deleted");
			    System.out.println(noticeMessage);
			    return true;
			}
			
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
		return false;
	}
}
