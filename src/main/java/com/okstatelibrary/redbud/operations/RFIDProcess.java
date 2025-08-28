
package com.okstatelibrary.redbud.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.okstatelibrary.redbud.entity.LocationModel;
import com.okstatelibrary.redbud.folio.entity.inventory.Item;
import com.okstatelibrary.redbud.service.CampusService;
import com.okstatelibrary.redbud.service.CirculationLogService;
import com.okstatelibrary.redbud.service.InstitutionService;
import com.okstatelibrary.redbud.service.LibraryService;
import com.okstatelibrary.redbud.service.LocationService;
import com.okstatelibrary.redbud.service.ServicePointService;
import com.okstatelibrary.redbud.util.DateUtil;

public class RFIDProcess extends MainProcess {

	protected String startTime;

	public void manipulate(InstitutionService institutionService, CampusService campusService,
			LibraryService libraryService, LocationService locationService, ServicePointService servicePointService,
			CirculationLogService circulationLogService) throws IOException {

		try {

			System.out.println("start - RFIDProcess" + DateUtil.getTodayDateAndTime());

			InfrastructureSetupProcess infra = new InfrastructureSetupProcess();

			ArrayList<LocationModel> locations = infra.getLocations(institutionService, campusService, libraryService,
					locationService);

			int i = 1;

			for (LocationModel location : locations) {

				if (location.location_id.equals("03de4f93-9d20-4c5b-890f-2cf784a72d09")) {

					System.out.println("location " + i + " ## " + location.location_id + " ## " + location.location);

					ArrayList<Item> items = folioService.getItemsByLocationId(location.location_id, location.location);

					i++;
				}
			}

			System.out.println("Done");

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}

	}

};