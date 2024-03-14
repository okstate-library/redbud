package com.okstatelibrary.redbud.repository;

import org.springframework.data.repository.CrudRepository;

import com.okstatelibrary.redbud.entity.Location;

import java.util.List;

public interface LocationDao extends CrudRepository<Location, Integer> {

	@SuppressWarnings("unchecked")
	Location save(Location location);

	List<Location> findAll();
}