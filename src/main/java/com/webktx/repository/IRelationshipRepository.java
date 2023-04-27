package com.webktx.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.webktx.entity.Relationship;

public interface IRelationshipRepository {
	public List<Relationship> findAll();
}
