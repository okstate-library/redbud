package com.okstatelibrary.redbud.service;

import java.util.List;

import com.okstatelibrary.redbud.entity.CirculationLog;

public interface CirculationLogService {

	CirculationLog saveCirculationLog(CirculationLog circulationLog);

	List<CirculationLog> getCirculationLogList(List<String> location, String fromDate, String toDate,
			boolean isEmptyDateWants, boolean isOpenLoans, String materialType);

	CirculationLog getCirculationLogByItemId(String itemId);

	List<CirculationLog> getCirculationLogListByLocation(String location_id);

	List<CirculationLog> getDistinctMaterialTypes();
}