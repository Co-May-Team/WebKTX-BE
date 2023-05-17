package com.webktx.repository;

import java.util.List;

import com.webktx.entity.College;
import com.webktx.model.CollegeModel;


public interface ICollegeRepository {
	List<College> findAll();
	List<CollegeModel> toModel(List<College> collegeList);
	List<CollegeModel> findModelAll();
}
