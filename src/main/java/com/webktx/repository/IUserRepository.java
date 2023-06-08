package com.webktx.repository;

import com.webktx.entity.User;
import com.webktx.model.UserModel;

public interface IUserRepository {
	User add(User user);
	Integer edit(User user);
	Boolean checkExistingUserByUsername(String username);
	User loadUserByUsername(String username);
	UserModel findByUsername(String username);
	User findById(Integer userId);
	Boolean checkExistingUserByCitizenId(String citizenId);
	Boolean checkExistingEmail(String email);

}
