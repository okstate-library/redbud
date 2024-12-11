package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.CirculationLog;
import com.okstatelibrary.redbud.repository.CirculationLogDao;
import com.okstatelibrary.redbud.service.CirculationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
			boolean isEmptyDateWants, boolean isOpenLoans, String materialType) {

		String guidList = locations.stream().map(guid -> "'" + guid + "'") // Add single quotes
				.collect(Collectors.joining(","));

		System.out.println("materialType " + materialType);
		
		return circulationLogDao.getCirculationLogByLocations(guidList, startDate, endDate, isEmptyDateWants,
				isOpenLoans, materialType);
	}

	@Override
	public CirculationLog getCirculationLogByItemId(String itemId) {
		return circulationLogDao.getCirculationLogByItemId(itemId);
	}

	@Override
	public List<CirculationLog> getCirculationLogListByLocation(String location_id) {
		return circulationLogDao.getCirculationLogListByLocation(location_id);
	}

	@Override
	public List<CirculationLog> getDistinctMaterialTypes() {
		return circulationLogDao.getDistinctMaterialTypes();
	}

}
