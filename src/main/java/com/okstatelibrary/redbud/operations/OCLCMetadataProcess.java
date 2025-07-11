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

		for (Campus campus : campusService.getCampusListByInstitutionId(institutionId)) {

			for (Library library : libraryService.getLibraryListByCampusId(campus.getCampus_id())) {

				if (library.getLibrary_id().contentEquals("189ff94d-d146-4751-aeff-7dae9e4ccde1")) {

					for (Location location : locationService.getLocationListByLibraryId(library.getLibrary_id())) {

						System.out.print("campus-" + campus.getCampus_name() + "," + "library-"
								+ library.getLibrary_name() + "," + "location-" + location.getLocation_name() + ",");

						oclcProcess(location.getLocation_id());

						System.out.println(" ");

					}
				}

			}

		}

		Thread.sleep(5000);

		oclcService.dropToken();

	}

	/// This method get OCLC numbers from FOLIO and send them to OCKLC API to
	/// process
	private void oclcProcess(String location) {
		try {

			List<HoldingsRecord> holdingList = folioService.getInventoryHoldings(location, "", "");

			System.out.println("list size: " + holdingList.size());

			List<String> setHoldigsList = new ArrayList<>();
			List<String> unSetHoldigsList = new ArrayList<>();

			int count = 0;

			for (HoldingsRecord selectedHolding : holdingList) {

				Set<String> oclcNumbers = folioService.getInventoryInstance(selectedHolding.getInstanceId());

				count++;

				if (oclcNumbers.size() > 1) {
					System.out.println("oclcNumbers.size() > 1" + " Folio id " + selectedHolding.getInstanceId());
				}

				for (String oclcNumber : oclcNumbers) {

					// String oclcNummber = (oclcNumber.split("#")[1]);

					HoldingRoot holdingRoot = oclcService.getOCLCItems(oclcNumber);

					if (holdingRoot != null && holdingRoot.holdings != null && holdingRoot.holdings.size() > 0) {

						for (Holding holding : holdingRoot.holdings) {

							String controlNumber = holding.currentControlNumber;

							if (!holding.holdingSet && !selectedHolding.discoverySuppress) {

								System.out.println("holdingSet is " + holding.holdingSet + " discoverySuppress is "
										+ selectedHolding.discoverySuppress + " oclcNummber " + oclcNumber
										+ " Folio id " + selectedHolding.getInstanceId());

								if (!setHoldigsList.contains(controlNumber)) {
									setHoldigsList.add(controlNumber);
								}

							} else if (holding.holdingSet && selectedHolding.discoverySuppress) {

								System.out.println("holdingSet is " + holding.holdingSet + " discoverySuppress is "
										+ selectedHolding.discoverySuppress + " oclcNummber " + oclcNumber
										+ " Folio id " + selectedHolding.getInstanceId());

								if (!unSetHoldigsList.contains(controlNumber)) {
									unSetHoldigsList.add(controlNumber);
								}
							}
						}

					}

				}

				if (count % 10000 == 0) {
					System.out.println("Process record Count" + count);
				}

			}

			System.out.println("*********************OCLC operation *****************");

			System.out.println("OCLC Numbers that should change to  holdingSet true");

			for (String controlNumber : setHoldigsList) {

				System.out.println(
						"OCLCNumber " + controlNumber + "  response " + oclcService.setOCLCItems(controlNumber));

			}

			System.out.println("OCLC Numbers that should change to  holdingSet false");

			for (String controlNumber : unSetHoldigsList) {

				System.out.println(
						"OCLCNumber " + controlNumber + "  response " + oclcService.unSetOCLCItems(controlNumber));
			}

			System.out.println("End of processing ");

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			// return null;
		}

	}

}
