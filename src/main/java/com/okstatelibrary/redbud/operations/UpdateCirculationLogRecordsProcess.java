
package com.okstatelibrary.redbud.operations;

import java.io.IOException;
import java.util.ArrayList;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.CirculationLog;
import com.okstatelibrary.redbud.entity.LocationModel;
import com.okstatelibrary.redbud.folio.entity.ItemRoot;
import com.okstatelibrary.redbud.folio.entity.holding.HoldingsRecord;
import com.okstatelibrary.redbud.folio.entity.holding.HoldingsStatement;
import com.okstatelibrary.redbud.folio.entity.inventory.Item;
import com.okstatelibrary.redbud.service.CampusService;
import com.okstatelibrary.redbud.service.CirculationLogService;
import com.okstatelibrary.redbud.service.InstitutionService;
import com.okstatelibrary.redbud.service.LibraryService;
import com.okstatelibrary.redbud.service.LocationService;
import com.okstatelibrary.redbud.service.ServicePointService;
import com.okstatelibrary.redbud.util.DateUtil;
import com.okstatelibrary.redbud.util.StringHelper;

public class UpdateCirculationLogRecordsProcess extends MainProcess {

	protected String startTime;

	public void manipulate(InstitutionService institutionService, CampusService campusService,
			LibraryService libraryService, LocationService locationService, ServicePointService servicePointService,
			CirculationLogService circulationLogService) throws IOException {

		try {

			System.out.println("start - UpdateCirculationLogRecordsProcess" + DateUtil.getTodayDateAndTime());

			InfrastructureSetupProcess infra = new InfrastructureSetupProcess();

			ArrayList<LocationModel> locations = infra.getLocations(institutionService, campusService, libraryService,
					locationService);

			getHoldingsCount(locations, circulationLogService);

			System.out.println("end" + DateUtil.getTodayDateAndTime());

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}

	}

	private void getHoldingsCount(ArrayList<LocationModel> locations, CirculationLogService circulationLogService)
			throws JsonParseException, JsonMappingException, RestClientException, IOException, InterruptedException {

		// List<CirculationLog> updateCirculationLogs = new ArrayList<>();

		for (LocationModel location : locations) {

			// System.out.println("location - " + location.location + " " +
			// location.location_id);

			ArrayList<HoldingsRecord> holdings = folioService
					.getHoldingsStorageByLocationIdAndHoldingStaement(location.location_id); // "4e4331ec-d652-4591-af7f-4c0dc6ddf485");
																								// // OSU LAB

			for (HoldingsRecord holdingRecord : holdings) {

				// System.out.println("holdingRecord.id " + holdingRecord.id);

				ItemRoot items = folioService.getItemByHoldingRecordId(holdingRecord.id);

				for (Item item : items.items) {

					if (item != null) {

						CirculationLog circulationLog = circulationLogService.getCirculationLogByItemId(item.id);

						if (circulationLog != null) {
//
							System.out.println("circulationLog.getId() " + circulationLog.getId());

							HoldingsStatement holdingsStatement = holdingRecord.holdingsStatements.get(0);

							circulationLog.setStatement(holdingsStatement.statement);

							circulationLog.setStaffNote(!StringHelper.isStringNullOrEmpty(holdingsStatement.staffNote)
									? holdingsStatement.staffNote
									: null);

							circulationLogService.saveCirculationLog(circulationLog);

						} else {
							System.out.println("  No circulationLog and itemid-" + item.id + " holdingRecord.id-"
									+ holdingRecord.id);
						}
					}
				}

			}

			Thread.sleep(3000);

		}

	}

}
