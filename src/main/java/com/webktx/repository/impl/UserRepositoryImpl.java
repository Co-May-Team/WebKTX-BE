package com.webktx.repository.impl;

import java.util.List;

import javax.persistence.Query;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.webktx.entity.User;
import com.webktx.model.RoleDetailModel;
import com.webktx.model.UserModel;
import com.webktx.repository.IUserRepository;
import com.webktx.ultil.Ultil;

import ch.qos.logback.classic.pattern.Util;

@Transactional
@Repository
public class UserRepositoryImpl implements IUserRepository {
	
	private final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class); 
	
	@Autowired
	SessionFactory sessionFactory;
	
	@Autowired
	RoleRepositoryImpl roleRepositoryImpl;

	@Override
	public User add(User user) {
		try {
			Session session = sessionFactory.getCurrentSession();
			Integer id = (Integer) session.save(user);
			if (id != null) {
				user.setUserId(id);
				return user;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Boolean checkExistingUserByUsername(String username) {
		try {
			Session session = sessionFactory.getCurrentSession();
			String hql = "FROM users as u WHERE u.username = :username";
			Query query = session.createQuery(hql);
			query.setParameter("username", username.trim());
			List<User> users = (List<User>) query.getResultList();
			if(!users.isEmpty()) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}
	
	@Override
	public User loadUserByUsername(String username) {
		try {
			Session session = sessionFactory.getCurrentSession();
			String hql = "FROM users as u WHERE u.username = :username";
			Query query = session.createQuery(hql);
			query.setParameter("username", username);
			User user = (User) query.getSingleResult();
			if(null != user) {
				return user;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	@Override
	public UserModel findByUsername(String username) {
		UserModel userModel = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			StringBuilder hql = new StringBuilder();
			hql.append("FROM users AS usr where usr.username = :username");
			Query query = session.createQuery(hql.toString());
			query.setParameter("username", username);
			User user = (User) query.getSingleResult();

			if(null != user) {
				userModel = new UserModel();
				RoleDetailModel roleDetailModel = new RoleDetailModel();
				roleDetailModel = roleRepositoryImpl.findRoleDetailsByRoleId(user.getRole().getRoleId());
				userModel.setRole(roleDetailModel);
				userModel.setEmail(user.getEmail());
				userModel.setUsername(user.getUsername());
				userModel.setPassword(user.getPassword());
				userModel.setId(user.getUserId());
				userModel.setFullName(user.getFullName());
				userModel.setGoogleAccount(user.isGoogleAccount());
				if(user.isGoogleAccount()) {
					userModel.setAvatar(user.getAvatar());
				}else if(user.getAvatar()!=null){
					userModel.setAvatar(Ultil.converBaseImageNameToLink(user.getAvatar()) );
				}
				if(user.getRole().getRoleId()==1) {
					userModel.setAdmin(true);
				}else {
					userModel.setAdmin(false);
				}
				userModel.setCitizenId(user.getCitizenId());
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}


		return userModel;
	}

	@Override
	public User findById(Integer userId) {
		User user = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			StringBuilder hql = new StringBuilder();
			hql.append("FROM users AS usr where usr.userId = :userId");
			Query query = session.createQuery(hql.toString());
			query.setParameter("userId", userId);
			user = (User) query.getSingleResult();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}


		return user;
	}
	@Override
	public Boolean checkExistingUserByCitizenId(String citizenId) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder hql = new StringBuilder("SELECT username from users as u where u.citizenId = :citizenId ");
		Query query = session.createQuery(hql.toString());
		query.setParameter("citizenId",citizenId );
		try {
			String result = (String) query.getSingleResult();
			if (!result.equals("")) {
				return true;
			}
		}
		 catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}
	@Override
	public Boolean checkExistingEmail(String email) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder hql = new StringBuilder("SELECT username from users as u where u.email = :email ");
		Query query = session.createQuery(hql.toString());
		query.setParameter("email",email );
		try {
			String result = (String) query.getSingleResult();
			if (!result.equals("")) {
				return true;
			}
		}
		 catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}

	@Override
	public Integer edit(User user) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.update(user);
			return 1;
		} catch (Exception e) {
			return 0;
		}
	}
}
