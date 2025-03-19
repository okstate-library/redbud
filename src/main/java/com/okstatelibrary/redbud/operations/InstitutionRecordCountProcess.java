
package com.okstatelibrary.redbud.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.InstitutionRecord;
import com.okstatelibrary.redbud.entity.Location;
import com.okstatelibrary.redbud.folio.entity.holding.HoldingsRecord2;
import com.okstatelibrary.redbud.service.InstitutionRecordService;
import com.okstatelibrary.redbud.service.LocationService;
import com.okstatelibrary.redbud.util.DateUtil;

public class InstitutionRecordCountProcess extends MainProcess {

	protected String startTime;

	private ArrayList<String> messageList;

	public void manipulate(LocationService locationService, InstitutionRecordService institutionRecordService)
			throws IOException {

		try {

			System.out.println("Running Institutional Holdings Counting process");

			System.out.println("start - " + DateUtil.getTodayDateAndTime());

			messageList = new ArrayList<>();

			messageList.add("Running Institutional Holdings Count process" + "<br/>");

			messageList.add("Start Time " + DateUtil.getTodayDateAndTime() + "<br/>");

			institutionRecordService.truncate();

			List<Location> locations = locationService.getLocationList();

			setHoldingsCount(locations, institutionRecordService);

			System.out.println("end - " + DateUtil.getTodayDateAndTime());

			createAndSendEmail();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}

	}

	@Transactional
	private void setHoldingsCount(List<Location> locations, InstitutionRecordService institutionRecordService)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		int locationProcced = 0;

		for (Location location : locations) {

			System.out.println("location name" + location.getLocation_name());

			InstitutionRecord institutionalHoldings = new InstitutionRecord();

			institutionalHoldings.setInstitutionId(location.getInstitution_id());
			institutionalHoldings.setLocationId(location.getLocation_id());

			String locationId = location.getLocation_id();

			ArrayList<HoldingsRecord2> holdings = folioService.getInventoryHoldingsByLocation(locationId);

			Map<String, Long> nameCounts = holdings.stream()
					.collect(Collectors.groupingBy(HoldingsRecord2::getInstanceId, Collectors.counting()));

			// Calculate non-duplicate count
			int nonDuplicateCount = (int) nameCounts.values().stream().filter(count -> count == 1) // Only unique
																									// occurrences
					.count();

			// Calculate duplicate count
			int duplicateCount = (int) nameCounts.values().stream().filter(count -> count > 1) // Only duplicates
					.count();

			institutionalHoldings.setInstanceCount(nonDuplicateCount + duplicateCount);
			institutionalHoldings.setHoldingCount(holdings.size());
			institutionalHoldings.setItemCount(folioService.getItemCountByLocationId(locationId));
			institutionalHoldings.setDate(DateUtil.getTodayDate());
			
			institutionRecordService.saveInstitutionRecordCounts(institutionalHoldings);

			locationProcced++;

		}

		messageList.add(
				"Total number of locations  " + locations.size() + " nUmber of locations procced  " + locationProcced);

		System.out.println("Done by getting data");

	}

	private void createAndSendEmail() {

		StringBuilder strBuilder = new StringBuilder();

		for (String message : messageList) {

			strBuilder.append(message + "<br/>");
		}

		strBuilder.append("<br/> End time: " + DateUtil.getTodayDateAndTime());

		this.sendEmaill("Running Institutional Holdings Counting process ", strBuilder.toString());

	}

}
