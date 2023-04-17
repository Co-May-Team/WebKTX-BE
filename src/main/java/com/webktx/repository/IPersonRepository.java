package com.webktx.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.webktx.entity.Person;
import com.webktx.entity.Student;
@Repository
@Transactional(rollbackFor = Exception.class)
public interface IPersonRepository {
	Integer add (Person person);
	Integer edit (Person person);
	Integer deleteById (Integer personId);
}
