package com.webktx.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webktx.entity.ResponseObject;
import com.webktx.model.CollegeModel;
import com.webktx.repository.impl.CollegeRepositoryImpl;

@Service
@Transactional(rollbackFor = Exception.class)
public class CollegeService {
	@Autowired
	CollegeRepositoryImpl collegeRepositoryImpl;
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	public ResponseEntity<Object> findAll() {
		List<CollegeModel> collegeListTMP = new ArrayList<CollegeModel>();
		Set<CollegeModel> collegeModelSet = new LinkedHashSet<CollegeModel>();
		try {
			collegeListTMP = collegeRepositoryImpl.findModelAll();
			Map<String, Object> results = new TreeMap<String, Object>();
			for(CollegeModel categorytModel : collegeListTMP) {
				collegeModelSet.add(categorytModel);
			}
			results.put("colleges", collegeModelSet);
			if (results.size() > 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("OK", "Successfully", results));
			} else {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", results));
			}
				
		} catch (Exception e) {
			LOGGER.error("ERROR:" + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", e.getMessage(), ""));
		}
	}
	
}
