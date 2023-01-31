package com.webktx.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.webktx.entity.ResponseObject;
import com.webktx.entity.Tag;
import com.webktx.model.TagModel;
import com.webktx.repository.impl.TagRepositoryImpl;

@Service
public class TagService {

	@Autowired
	TagRepositoryImpl tagRepositoryImpl;

	public ResponseEntity<Object> findAll() {
		try {
			List<TagModel> tagModels = new ArrayList<>();
			List<Tag> tags = tagRepositoryImpl.findAll();
			for(Tag tag : tags) {
				TagModel tagModel = new TagModel();
				tagModel.setTagId(tag.getTagId());
				tagModel.setTagName(tag.getTagName());
				tagModels.add(tagModel);
			}
			
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", tagModels));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR", "Have error:", e.getMessage()));
		}

	}
}
