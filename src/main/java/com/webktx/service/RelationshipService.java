package com.webktx.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.webktx.entity.Relationship;
import com.webktx.entity.ResponseObject;
import com.webktx.repository.impl.RelationshipRepositopryImpl;

@Service
public class RelationshipService {
	@Autowired
	RelationshipRepositopryImpl relationshipRepositoryImpl;
	public ResponseEntity<ResponseObject> findAll(){
		List<Relationship> relationshipList = new ArrayList<>();
		relationshipList = relationshipRepositoryImpl.findAll();
		if (relationshipList.size() > 0) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", relationshipList));
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Data not found", ""));
		}
	}
}
