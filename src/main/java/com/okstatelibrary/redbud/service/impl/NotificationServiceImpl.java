package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.Notification;
import com.okstatelibrary.redbud.repository.NotificationDao;
import com.okstatelibrary.redbud.service.NotificationService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

	// private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private NotificationDao notificationDao;

	@Override
	public Optional<Notification> getNotification() {

		return notificationDao.findById((int) 1);
	}

}