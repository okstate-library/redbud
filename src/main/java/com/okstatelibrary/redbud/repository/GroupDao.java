package com.okstatelibrary.redbud.repository;

import org.springframework.data.repository.CrudRepository;

import com.okstatelibrary.redbud.entity.PatronGroup;

import java.util.List;

public interface GroupDao extends CrudRepository<PatronGroup, Integer> {

	@SuppressWarnings("unchecked")
	PatronGroup save(PatronGroup group);

	List<PatronGroup> findAll();
}