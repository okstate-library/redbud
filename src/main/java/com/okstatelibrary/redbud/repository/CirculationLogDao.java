package com.okstatelibrary.redbud.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.okstatelibrary.redbud.entity.CirculationLog;

import java.util.List;

public interface CirculationLogDao extends CrudRepository<CirculationLog, Integer> {

	@SuppressWarnings("unchecked")
	CirculationLog save(CirculationLog circulationLog);
	
	List<CirculationLog> findAll();
	
	@Query(value = "SELECT * FROM redbuddb.circulation_log where item_id= :itemId", nativeQuery = true)
	CirculationLog getCirculationLogByItemId(@Param("itemId") String itemId);
}