package com.webktx.repository;

import java.util.List;

import com.webktx.entity.Post;
import com.webktx.entity.Relative;
import com.webktx.model.RelativeModel;

public interface IRelativeRepository {
	Integer add (Relative relative);
	Integer edit (Relative relative);
	Integer deleteById (Integer relativeId);
	List<Relative> findByUserId(Integer userId);
	boolean deleteAllByUserId(Integer userId);
	List<RelativeModel> findModelByUserId(Integer userId);
	List<RelativeModel> toModel(List<Relative> relativeList);
	
}
