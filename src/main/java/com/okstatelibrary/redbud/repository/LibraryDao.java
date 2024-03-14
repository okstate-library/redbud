package com.okstatelibrary.redbud.repository;

import org.springframework.data.repository.CrudRepository;

import com.okstatelibrary.redbud.entity.Library;

import java.util.List;

public interface LibraryDao extends CrudRepository<Library, Integer> {

	@SuppressWarnings("unchecked")
	Library save(Library library);

	List<Library> findAll();
}