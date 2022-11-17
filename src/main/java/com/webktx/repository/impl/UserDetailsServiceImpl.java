package com.webktx.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.webktx.model.UserModel;
import com.webktx.service.UserDetailsImpl;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
    @Autowired
    UserRepositoryImpl userRepositoryImpl;

	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	UserModel user = null;
		user = userRepositoryImpl.findByUsername(username);
		if(user == null) {
			throw new UsernameNotFoundException("User Not Found with username: " + username);
		}
        return UserDetailsImpl.build(user);
	}
	

}
