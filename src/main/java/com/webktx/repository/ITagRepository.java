package com.webktx.repository;

import java.util.List;

import com.webktx.entity.Tag;

public interface ITagRepository {
	Tag findById(Integer id);
	List<Tag> findAll();
}
