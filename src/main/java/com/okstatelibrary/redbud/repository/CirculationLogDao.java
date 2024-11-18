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

	@Query(value = "SELECT * FROM circulation_log WHERE item_id= :itemId", nativeQuery = true)
	CirculationLog getCirculationLogByItemId(@Param("itemId") String itemId);

	@Query(value = "SELECT * FROM circulation_log WHERE location IN (:locationIds)", nativeQuery = true)
	List<CirculationLog> getCirculationLogByLocations(@Param("locationIds") List<String> locationIds);
}