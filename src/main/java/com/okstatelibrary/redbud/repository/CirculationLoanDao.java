package com.okstatelibrary.redbud.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.okstatelibrary.redbud.entity.CirculationLoan;

import java.util.List;

public interface CirculationLoanDao extends CrudRepository<CirculationLoan, Integer> {

	@SuppressWarnings("unchecked")
	CirculationLoan save(CirculationLoan circulationLoan);

	@Query(nativeQuery = true, value = "call get_CirculationLoanDetails(:locationIds,:fromDate ,:toDate ,:loan_action,:materialType)")
	List<CirculationLoan> getCirculationLoanByLocations(@Param("locationIds") String locationIds,
			@Param("fromDate") String fromDate, @Param("toDate") String toDate, @Param("loan_action") String loanAction,
			@Param("materialType") String materialType);

	@Query(value = "SELECT * FROM circulation_loan WHERE row_id=:row_id", nativeQuery = true)
	CirculationLoan getCirculationLoanByRowId(String row_id);

}