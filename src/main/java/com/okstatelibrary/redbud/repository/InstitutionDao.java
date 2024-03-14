package com.okstatelibrary.redbud.repository;

import org.springframework.data.repository.CrudRepository;

import com.okstatelibrary.redbud.entity.Institution;

import java.util.List;

public interface InstitutionDao extends CrudRepository<Institution, Integer> {

	@SuppressWarnings("unchecked")
	Institution save(Institution institution);

	List<Institution> findAll();
}