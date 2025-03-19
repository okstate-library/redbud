package com.okstatelibrary.redbud.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.okstatelibrary.redbud.entity.InstitutionRecord;

import java.util.List;

import javax.transaction.Transactional;

public interface InstitutionalRecordDao extends CrudRepository<InstitutionRecord, Integer> {

	List<InstitutionRecord> findAll();

	@Modifying
	@Transactional
	@Query(value = "Truncate table institution_record", nativeQuery = true)
	void truncate();
}