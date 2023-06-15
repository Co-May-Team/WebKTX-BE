package com.webktx.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.webktx.entity.Person;
import com.webktx.entity.Student;
import com.webktx.model.PersonModel;
@Repository
@Transactional(rollbackFor = Exception.class)
public interface IPersonRepository {
	Integer add (Person person);
	Integer edit (Person person);
	Integer deleteById (Integer personId);
	Person findByUserId(Integer userId);
	boolean isExistWithUserId(Integer userId);
	List<Person> findAllByYear(int year);
	PersonModel toModel(Person person);
	PersonModel findModelByUserId(Integer userId);
	Boolean checkExistingCitizenId(String citizenId, int userId);
	Boolean checkExistingEmail(String email, int userId);
	Boolean checkExistingPhoneNumber(String phoneNumber, int userId);
}
