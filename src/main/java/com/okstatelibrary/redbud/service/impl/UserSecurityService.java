package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.User;
import com.okstatelibrary.redbud.repository.UserDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityService implements UserDetailsService {

	private static final Logger LOG = LoggerFactory.getLogger(UserSecurityService.class);

	@Autowired
	private UserDao userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// User user = new User();

//		user.setFirstName("Admin");
//		user.setLastName("Lastname");
//		user.setRoleId(1);
//		user.setUserId(Long.parseLong("1"));
//		user.setUsername("admin");
//		user.setPassword("$2a$12$0nyne1/4.1.28ILaR9CqBuf0Uj.zne2Xr.OkQDd3XPW0OKbkKqrX6");
//		user.setDeleted(false);
//		user.setEnabled(true);
//		user.setEmail("admin@gmail.com");

		User user = userDao.findByUsername(username);

		if (null == user || user.isDeleted()) {
			LOG.warn("Username {} not found", username);
			throw new UsernameNotFoundException("Username " + username + " not found");
		}

		return user;
	}
}