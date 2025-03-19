package com.okstatelibrary.redbud.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.okstatelibrary.redbud.entity.Notification;

public interface NotificationDao extends CrudRepository<Notification, Integer> {
	
	List<Notification> findAll();

}