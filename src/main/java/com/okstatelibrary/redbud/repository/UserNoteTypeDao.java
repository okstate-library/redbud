package com.okstatelibrary.redbud.repository;

import org.springframework.data.repository.CrudRepository;
import com.okstatelibrary.redbud.entity.UserNoteType;

import java.util.List;

public interface UserNoteTypeDao extends CrudRepository<UserNoteType, Integer> {

	List<UserNoteType> findAll();
	
}