package com.okstatelibrary.redbud.repository;

import org.springframework.data.repository.CrudRepository;

import com.okstatelibrary.redbud.entity.Campus;

import java.util.List;

public interface CampusDao extends CrudRepository<Campus, Integer> {

	@SuppressWarnings("unchecked")
	Campus save(Campus library);

	List<Campus> findAll();
}