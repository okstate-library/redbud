
package com.okstatelibrary.redbud.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.InstitutionalHoldings;
import com.okstatelibrary.redbud.entity.LocationModel;
import com.okstatelibrary.redbud.service.CampusService;
import com.okstatelibrary.redbud.service.InstitutionService;
import com.okstatelibrary.redbud.service.InstitutionalHoldingsService;
import com.okstatelibrary.redbud.service.LibraryService;
import com.okstatelibrary.redbud.service.LocationService;
import com.okstatelibrary.redbud.service.ServicePointService;
import com.okstatelibrary.redbud.util.DateUtil;

public class UpdateStaffNoteProcess extends MainProcess {

	protected String startTime;

	public void manipulate(InstitutionService institutionService, CampusService campusService,
			LibraryService libraryService, LocationService locationService, ServicePointService servicePointService,
			InstitutionalHoldingsService institutionalHoldingsService) throws IOException {

		try {

			System.out.println("start" + DateUtil.getTodayDateAndTime());

			InfrastructureSetupProcess infra = new InfrastructureSetupProcess();

			ArrayList<LocationModel> locations = infra.getLocations(institutionService, campusService, libraryService,
					locationService);

			getHoldingsCount(locations, institutionalHoldingsService);

			System.out.println("end" + DateUtil.getTodayDateAndTime());

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}

	}

	private void getHoldingsCount(ArrayList<LocationModel> locations,
			InstitutionalHoldingsService institutionalHoldingsService)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		HashMap<String, Integer> locationList = new HashMap<>();

		Collections.sort(locations, new Comparator<LocationModel>() {
			@Override
			public int compare(LocationModel o1, LocationModel o2) {
				return o1.institution.compareTo(o2.institution);
			}
		});

		for (LocationModel location : locations) {

			int recordCount = folioService.getInventoryHoldingsCount(location.location_id);

			System.out.println(location.institution + "," + location.location + "," + recordCount);

			String key = location.institution;

			if (locationList.containsKey(key)) {
				locationList.put(key, locationList.get(key) + recordCount);
			} else {

				locationList.put(key, recordCount);
			}

		}

		for (String key : locationList.keySet()) {

			System.out.println(key + "," + locationList.get(key));

			InstitutionalHoldings institutionalHoldings = new InstitutionalHoldings();

			institutionalHoldings.setInstitutionalHoldingsName(key);
			institutionalHoldings.setRecordCount(locationList.get(key));

			institutionalHoldingsService.saveInstitutionalHoldings(institutionalHoldings);
		}
	}

}
