package com.okstatelibrary.redbud.operations;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.web.client.RestClientException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.Campus;
import com.okstatelibrary.redbud.entity.Library;
import com.okstatelibrary.redbud.entity.Location;
import com.okstatelibrary.redbud.folio.entity.holding.HoldingsRecord;
import com.okstatelibrary.redbud.oclc.entity.Holding;
import com.okstatelibrary.redbud.oclc.entity.HoldingRoot;
import com.okstatelibrary.redbud.service.CampusService;
import com.okstatelibrary.redbud.service.InstitutionService;
import com.okstatelibrary.redbud.service.LibraryService;
import com.okstatelibrary.redbud.service.LocationService;
import com.okstatelibrary.redbud.service.external.OCLCService;
import org.apache.http.client.ClientProtocolException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OCLCMetadataProcess extends MainProcess {

	private OCLCService oclcService;

	public OCLCMetadataProcess()
			throws OAuthSystemException, OAuthProblemException, ClientProtocolException, IOException {

		oclcService = new OCLCService();

	}

	public void manipulate(InstitutionService institutionService, CampusService campusService,
			LibraryService libraryService, LocationService locationService, String institutionId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException, InterruptedException {

		oclcService.setToken();

		Thread.sleep(5000);

		// OCLC operation is running only for "Oklahoma State University, Stillwater"

		outerloop: for (Campus campus : campusService
				.getCampusListByInstitutionId("b3439a37-ec18-4d3f-a1a0-88a404b8062c")) {

			for (Library library : libraryService.getLibraryListByCampusId(campus.getCampus_id())) {

				for (Location location : locationService.getLocationListByLibraryId(library.getLibrary_id())) {

					// System.out.println("location id " + location.getLocation_id());

					if (location.getLocation_id().contentEquals("912064a8-6296-4d35-8c91-48722c5ddc59")) {

						System.out.println("campus-" + campus.getCampus_name() + "," + "library-"
								+ library.getLibrary_name() + "," + "location-" + location.getLocation_name() + ",");

						oclcProcess(location.getLocation_id());

						System.out.println("End of location  --  " + location.getLocation_name());

						break outerloop;
					}
				}

			}

		}

		System.out.println("End of OLC process");

		Thread.sleep(5000);

		oclcService.dropToken();

	}

	/// This method get OCLC numbers from FOLIO and send them to OCKLC API to
	/// process
	private void oclcProcess(String location) {
		try {

			List<HoldingsRecord> holdingList = folioService.getInventoryHoldings(location);

			System.out.println("list size: " + holdingList.size());

			List<String> setHoldigsList = new ArrayList<>();
			List<String> unSetHoldigsList = new ArrayList<>();

			int count = 0;

			for (HoldingsRecord inventory_holding : holdingList) {

				Set<String> oclcNumbers = folioService.getInventoryInstance(inventory_holding.getInstanceId());

				count++;

				if (oclcNumbers.size() > 1) {

					System.out.println("oclcNumbers.size() > 1" + " Folio id " + inventory_holding.getInstanceId());
				}

				for (String oclcNumber : oclcNumbers) {

					System.out.println("oclcNumber" + oclcNumber);

					HoldingRoot holdingRoot = oclcService.getOCLCItems(oclcNumber);

					if (holdingRoot != null && holdingRoot.holdings != null && holdingRoot.holdings.size() > 0) {

						for (Holding oclc_holding : holdingRoot.holdings) {

							String controlNumber = oclc_holding.currentControlNumber;

							if (!oclc_holding.holdingSet && !inventory_holding.discoverySuppress) {

//								System.out.println("holdingSet is " + holding.holdingSet + " discoverySuppress is "
//										+ selectedHolding.discoverySuppress + " oclcNummber " + oclcNumber
//										+ " Folio id " + selectedHolding.getInstanceId());

								if (!setHoldigsList.contains(controlNumber)) {

									System.out.println(
											"holdingSet is " + oclc_holding.holdingSet + " discoverySuppress is "
													+ inventory_holding.discoverySuppress + " oclcNummber " + oclcNumber
													+ " Folio id " + inventory_holding.getInstanceId());

									setHoldigsList.add(controlNumber);
								}

							} else if (oclc_holding.holdingSet && inventory_holding.discoverySuppress) {

//								System.out.println("holdingSet is " + holding.holdingSet + " discoverySuppress is "
//										+ selectedHolding.discoverySuppress + " oclcNummber " + oclcNumber
//										+ " Folio id " + selectedHolding.getInstanceId());

								if (!unSetHoldigsList.contains(controlNumber)) {

									System.out.println(
											"holdingSet is " + oclc_holding.holdingSet + " discoverySuppress is "
													+ inventory_holding.discoverySuppress + " oclcNummber " + oclcNumber
													+ " Folio id " + inventory_holding.getInstanceId());

									unSetHoldigsList.add(controlNumber);
								}
							}
						}

					}

				}

				if (count % 1000 == 0) {

					System.out.println("Process record Count" + count);

				}

			}

			System.out.println("OCLC Numbers that should change to  holdingSet true");

			for (String controlNumber : setHoldigsList) {

				System.out.println(controlNumber);

			}

			System.out.println("OCLC Numbers that should change to  holdingSet false");

			for (String controlNumber : unSetHoldigsList) {

				System.out.println(controlNumber);
			}

//
//			System.out.println("*********************OCLC operation *****************");
//
//			System.out.println("OCLC Numbers that should change to  holdingSet true");

//			for (String controlNumber : setHoldigsList) {
//
//				System.out.println(
//						"OCLCNumber " + controlNumber + "  response " + oclcService.setOCLCItems(controlNumber));
//
//			}
//
//			System.out.println("OCLC Numbers that should change to  holdingSet false");
//
//			for (String controlNumber : unSetHoldigsList) {
//
//				System.out.println(
//						"OCLCNumber " + controlNumber + "  response " + oclcService.unSetOCLCItems(controlNumber));
//			}

			System.out.println("End of processing ");

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			// return null;
		}

	}

}
