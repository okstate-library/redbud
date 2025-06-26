package com.okstatelibrary.redbud.service;

import java.util.List;

import com.okstatelibrary.redbud.entity.CirculationLoan;

public interface CirculationLoanService {

	CirculationLoan saveCirculationLoan(CirculationLoan circulationLoan);

	List<CirculationLoan> getCirculationLoanList(List<String> location, String fromDate, String toDate,
			String loanAction, String materialType);

	CirculationLoan getCirculationLoanByRowId(String rowId);

}