package com.okstatelibrary.redbud.repository;

import org.springframework.data.repository.CrudRepository;

import com.okstatelibrary.redbud.entity.ServicePoint;

import java.util.List;

public interface ServicePointDao extends CrudRepository<ServicePoint, Integer> {

	@SuppressWarnings("unchecked")
	ServicePoint save(ServicePoint servicePoint);

	List<ServicePoint> findAll();
}