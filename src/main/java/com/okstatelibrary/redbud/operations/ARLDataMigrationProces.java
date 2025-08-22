
package com.okstatelibrary.redbud.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.okstatelibrary.redbud.entity.LocationModel;
import com.okstatelibrary.redbud.folio.entity.instance.InstanceFormat;
import com.okstatelibrary.redbud.folio.entity.instance.InstanceType;
import com.okstatelibrary.redbud.folio.entity.inventory.Item;
import com.okstatelibrary.redbud.service.CampusService;
import com.okstatelibrary.redbud.service.CirculationLogService;
import com.okstatelibrary.redbud.service.InstitutionService;
import com.okstatelibrary.redbud.service.LibraryService;
import com.okstatelibrary.redbud.service.LocationService;
import com.okstatelibrary.redbud.service.ServicePointService;
import com.okstatelibrary.redbud.util.DateUtil;

public class ARLDataMigrationProces extends MainProcess {

	protected String startTime;

	public void manipulate(InstitutionService institutionService, CampusService campusService,
			LibraryService libraryService, LocationService locationService, ServicePointService servicePointService,
			CirculationLogService circulationLogService) throws IOException {

		try {

			System.out.println("start - UpdateCirculationLogRecordsProcess" + DateUtil.getTodayDateAndTime());

			InfrastructureSetupProcess infra = new InfrastructureSetupProcess();

			ArrayList<LocationModel> locations = infra.getLocations(institutionService, campusService, libraryService,
					locationService);

//			List<LocationModel> selLocations = locations.stream()
//					.filter(l -> l.library_id.contentEquals("90fd1e1d-dc88-4751-aa50-70b35a594360"))
//					.filter(item -> item.location_id.equals("912064a8-6296-4d35-8c91-48722c5ddc59")) // Exclude OKS-OSU
//																										// Main Stacks
//					.collect(Collectors.toList());

			int locationCount = 1;

			for (LocationModel location : locations) {

//				System.out.println(
//						"location " + locationCount + " ## " + location.location_id + " ## " + location.location);

				ArrayList<InstanceFormat> formats = folioService.getInstanceFormats();

				ArrayList<InstanceType> types = folioService.getInstanceTypes();

				int itemCount = 0;

				for (InstanceFormat format : formats) {

					for (InstanceType type : types) {

						int loanCount = folioService.getInstanceCountByFormatAndType(format.id, type.id,
								location.location_id);

						if (loanCount > 0) {
							System.out.println(location.institution + "," + location.campus + "," + location.library
									+ "," + location.location + "," + format.name + "," + type.name + "," + loanCount);
						}

						itemCount++;

					}

				}

				System.out.println("locationCount" + locationCount);

				locationCount++;

			}

			System.out.println("Done");

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}

	}

};