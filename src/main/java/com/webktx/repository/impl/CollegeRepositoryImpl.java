package com.webktx.repository.impl;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.webktx.entity.College;
import com.webktx.model.CollegeModel;
import com.webktx.repository.ICollegeRepository;

@Repository
@Transactional(rollbackFor = Exception.class)
public class CollegeRepositoryImpl implements ICollegeRepository {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	@Transactional
	public List<College> findAll() {
		List<College> collegeList = new ArrayList<College>();
		StringBuilder hql = new StringBuilder("from colleges");
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			collegeList = query.getResultList();
		}
		catch  (Exception e) {
			LOGGER.error("Error has occured in findAll Colleges "+e, e);
		}
		return collegeList;
	}
	
	@Override
	public List<CollegeModel> findModelAll() {
		List<College> collegeList = new ArrayList<College>();
		List<CollegeModel> collegeModelList = new ArrayList<CollegeModel>();
		collegeList = findAll();
		collegeModelList = toModel(collegeList);
		return collegeModelList;
	}
	
	
	@Override
	public List<CollegeModel> toModel(List<College> collegeList) {
		List<CollegeModel> collegeModelList = new ArrayList<CollegeModel>();
		for (College college: collegeList) {
			CollegeModel collegeModel = new CollegeModel();
			collegeModel.setId(college.getCollegeId());
			collegeModel.setName(college.getCollegeName());
			collegeModelList.add(collegeModel);
		}
		return collegeModelList;
	}
	
}
