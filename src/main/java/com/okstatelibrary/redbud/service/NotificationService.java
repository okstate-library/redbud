package com.okstatelibrary.redbud.service;

import java.util.Optional;

import com.okstatelibrary.redbud.entity.Notification;

public interface NotificationService {

	Optional<Notification> getNotification();

}