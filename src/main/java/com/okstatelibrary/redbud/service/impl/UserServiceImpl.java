package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.User;
import com.okstatelibrary.redbud.repository.UserDao;
import com.okstatelibrary.redbud.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

//	@Override
//	public User findByUsername(String username) {
//		User user = new User();
//
//		user.setFirstName("Admin");
//		user.setLastName("Lastname");
//		user.setRoleId(1);
//		user.setUserId(Long.parseLong("1"));
//		user.setUsername("admin");
//		user.setPassword("$2a$12$0nyne1/4.1.28ILaR9CqBuf0Uj.zne2Xr.OkQDd3XPW0OKbkKqrX6");
//		user.setDeleted(false);
//		user.setEnabled(true);
//		user.setEmail("admin@gmail.com");
//
//		return user;
//	}

	public void save(User user) {
		userDao.save(user);
	}

	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}

//
	public User findByEmail(String email) {
		return userDao.findByEmail(email);
	}

	public User createUser(User user) {
		User localUser = userDao.findByUsername(user.getUsername());

		if (localUser != null) {
			LOG.info("User with username {} already exist. Nothing will be done. ", user.getUsername());
		} else {
			String encryptedPassword = passwordEncoder.encode(user.getPassword());
			user.setPassword(encryptedPassword);

			localUser = userDao.save(user);
		}

		return localUser;
	}

	public boolean checkUserExists(String username, String email) {
		return checkUsernameExists(username) || checkEmailExists(username);
	}

	public boolean checkUsernameExists(String username) {
		return null != findByUsername(username);

	}

	public boolean checkEmailExists(String email) {
		return null != findByEmail(email);

	}

	public User saveUser(User user) {
		return userDao.save(user);
	}

	public List<User> findUserList() {

		return userDao.findAll().stream().filter(u -> !u.isDeleted()).collect(Collectors.toList());
	}

	@Override
	public void updatePassword(User user) {

		String encryptedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encryptedPassword);

		userDao.save(user);
	}

}