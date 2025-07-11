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

	@Query(nativeQuery = true, value = "call get_CirculationLogDetails(:locationIds,:fromDate ,:toDate ,:isEmptyDateWants,:isOpenLoans,:materialType)")
	List<CirculationLog> getCirculationLogByLocations(@Param("locationIds") String locationIds,
			@Param("fromDate") String fromDate, @Param("toDate") String toDate,
			@Param("isEmptyDateWants") boolean isEmptyDateWants, @Param("isOpenLoans") boolean isOpenLoans,
			@Param("materialType") String materialType);

	@Query(value = "SELECT * FROM circulation_log WHERE location= :location_id", nativeQuery = true)
	List<CirculationLog> getCirculationLogListByLocation(@Param("location_id") String location_id);

	@Query(nativeQuery = true, value = "call get_DistinctMaterialTypes()")
	List<CirculationLog> getDistinctMaterialTypes();

}