
package com.okstatelibrary.redbud.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.CirculationLog;
import com.okstatelibrary.redbud.entity.Location;
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

public class NeverCirculatedItemsSearchProces extends MainProcess {

	protected String startTime;

	public void manipulate(InstitutionService institutionService, CampusService campusService,
			LibraryService libraryService, LocationService locationService, ServicePointService servicePointService,
			CirculationLogService circulationLogService) throws IOException {

		try {

			System.out.println("start - UpdateCirculationLogRecordsProcess" + DateUtil.getTodayDateAndTime());

			InfrastructureSetupProcess infra = new InfrastructureSetupProcess();

			ArrayList<LocationModel> locations = infra.getLocations(institutionService, campusService, libraryService,
					locationService);

			List<LocationModel> selLocations = locations.stream()
					.filter(l -> l.library_id.contentEquals("90fd1e1d-dc88-4751-aa50-70b35a594360"))
					.filter(item -> item.location_id.equals("912064a8-6296-4d35-8c91-48722c5ddc59")) // Exclude OKS-OSU
																										// Main Stacks
					.collect(Collectors.toList());

			int i = 1;

			for (LocationModel location : selLocations) {
				// {912064a8-6296-4d35-8c91-48722c5ddc59 OKS-OSU Main Stacks
				// "222f991f-ad0f-43ac-98bd-342c16c39588

				System.out.println("location " + i + " ## " + location.location_id + " ## " + location.location);
				// + " items count " + items.size());

				ArrayList<Item> items = folioService.getItemsByLocationId(location.location_id, location.location);

//				int itemCount = 0;
//
//				for (Item item : items) {
//
//					int loanCount = folioService.getLoanCountByItemId(item.id);
//
//					if (loanCount == 0 && item.lastCheckIn == null) {
//						
//						//Item item	folioService.getItemByItemId(item.id);
//						
//						System.out.println(item.id + "##" + item.barcode + "##" + item.title);
//						
//					}
//
////					if (itemCount % 1000 == 0) {
////						System.out.println("Item count " + itemCount);
////					}
////
////					itemCount++;
//
//				}
//

//
//				i++;
			}

			System.out.println("Done");

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}

	}

};