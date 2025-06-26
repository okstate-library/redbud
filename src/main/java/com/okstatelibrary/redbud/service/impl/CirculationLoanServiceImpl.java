package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.CirculationLoan;
import com.okstatelibrary.redbud.repository.CirculationLoanDao;
import com.okstatelibrary.redbud.service.CirculationLoanService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CirculationLoanServiceImpl implements CirculationLoanService {

	// private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private CirculationLoanDao circulationLoanDao;

	@Override
	public CirculationLoan saveCirculationLoan(CirculationLoan circulationLoan) {
		return circulationLoanDao.save(circulationLoan);
	}

	@Override
	public List<CirculationLoan> getCirculationLoanList(List<String> locations, String startDate, String endDate,
			String loanAction, String materialType) {

		String guidList = locations.stream().map(guid -> "'" + guid + "'") // Add single quotes
				.collect(Collectors.joining(","));

		System.out.println("materialType " + materialType);

		return circulationLoanDao.getCirculationLoanByLocations(guidList, startDate, endDate, loanAction, materialType);
	}

	@Override
	public CirculationLoan getCirculationLoanByRowId(String rowId) {

		return circulationLoanDao.getCirculationLoanByRowId(rowId);
	}

}
