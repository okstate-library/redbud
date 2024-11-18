package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.CirculationLog;
import com.okstatelibrary.redbud.repository.CirculationLogDao;
import com.okstatelibrary.redbud.service.CirculationLogService;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CirculationLogServiceImpl implements CirculationLogService {

	// private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private CirculationLogDao circulationLogDao;

	@Override
	public CirculationLog saveCirculationLog(CirculationLog group) {
		return circulationLogDao.save(group);
	}

	@Override
	public List<CirculationLog> getCirculationLogList(List<String> locations, String startDate, String endDate,
			boolean isEmptyDateWants) {

		List<CirculationLog> circulationLogs = circulationLogDao.getCirculationLogByLocations(locations);

		if (!isEmptyDateWants) {
			circulationLogs = circulationLogs.stream()
					.filter(c -> !c.getLoanDate().contentEquals(Constants.default_date)
							& isBetween(c.getLoanDate(), startDate, endDate))
					.collect(Collectors.toList());
		}

		return circulationLogs;
	}

	@Override
	public CirculationLog getCirculationLogByItemId(String itemId) {
		return circulationLogDao.getCirculationLogByItemId(itemId);
	}

	// Method to check if a date is between two other dates
	private static boolean isBetween(String objDate, String startDate, String endDate) {

		LocalDate date = DateUtil.getLocaleDate(objDate);

		return !date.isBefore(DateUtil.getLocaleDate(startDate)) && !date.isAfter(DateUtil.getLocaleDate(endDate));
	}
}
