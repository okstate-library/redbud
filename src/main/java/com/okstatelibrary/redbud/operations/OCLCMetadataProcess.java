package com.okstatelibrary.redbud.operations;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.web.client.RestClientException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.folio.entity.holding.HoldingsRecord;
import com.okstatelibrary.redbud.oclc.entity.Holding;
import com.okstatelibrary.redbud.oclc.entity.HoldingRoot;
import com.okstatelibrary.redbud.service.external.OCLCService;
import com.okstatelibrary.redbud.util.DateUtil;

import org.apache.http.client.ClientProtocolException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OCLCMetadataProcess extends MainProcess {

	private OCLCService oclcService;

	public OCLCMetadataProcess()
			throws OAuthSystemException, OAuthProblemException, ClientProtocolException, IOException {

		oclcService = new OCLCService();
	}

	public void getOCLCItems(String oclcNumberss)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		// "00227353", "00227353", "77767587", "777251269", "32132132"

//		String[] oclcNumbers = { "1284171474" };
//
//		for (String str : oclcNumbers) {
//
//			HoldingRoot holdingRoot = oclcService.getOCLCItems(str);
//
//			System.out.println("Size " + holdingRoot.holdings.size());
//
//			if (holdingRoot != null && holdingRoot.holdings != null && holdingRoot.holdings.size() > 0) {
//
//				// System.out.println("id: " + entry.getKey() + ", nos:" + entry.getValue());
//
//				for (Holding holding : holdingRoot.holdings) {
//
//					if (!holding.holdingSet) {
//
//						// System.out.println("id: " + entry.getKey() + ", nos:" + entry.getValue());
//
//						System.out.println("holdingSet is False " + holding.requestedControlNumber);
//					} else {
//						System.out.println("holdingSet is true " + holding.requestedControlNumber);
//					}
//				}
//
//			} else {
//
//				// System.out.println("id: " + entry.getKey() + ", nos:" + entry.getValue());
//
//				System.out.println("No Oclc Records found for : " + str);
//			}
//		}

//  The main code to get OCLC numbers from FOLIO and send them to OCKLC API to process

		try {

			List<HoldingsRecord> holdingList = folioService.getInventoryHoldings("912064a8-6296-4d35-8c91-48722c5ddc59",
					"", "");

			System.out.println("list size: " + holdingList.size());

			List<Holding> setHoldigsList = new ArrayList<>();
			List<Holding> unSetHoldigsList = new ArrayList<>();

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

							if (!holding.holdingSet && !selectedHolding.discoverySuppress) {

								System.out.println("holdingSet is " + holding.holdingSet + " discoverySuppress is "
										+ selectedHolding.discoverySuppress + " oclcNummber " + oclcNumber
										+ " Folio id " + selectedHolding.getInstanceId());

								setHoldigsList.add(holding);

							} else if (holding.holdingSet && selectedHolding.discoverySuppress) {

								System.out.println("holdingSet is " + holding.holdingSet + " discoverySuppress is "
										+ selectedHolding.discoverySuppress + " oclcNummber " + oclcNumber
										+ " Folio id " + selectedHolding.getInstanceId());

								unSetHoldigsList.add(holding);
							}
						}

					}

				}

				if (count % 10000 == 0) {
					System.out.println("Process record Count" + count);
				}

			}

			for (Holding setHolding : setHoldigsList) {
				System.out.println("OCLCNumber " + setHolding.currentControlNumber + "  response "
						+ oclcService.setOCLCItems(setHolding.currentControlNumber));
			}

			for (Holding setHolding : unSetHoldigsList) {
				System.out.println("OCLCNumber " + setHolding.currentControlNumber + "  response "
						+ oclcService.unSetOCLCItems(setHolding.currentControlNumber));
			}

			System.out.println("End of processing ");

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			// return null;
		}

//		List<HashMap<String, List<String>>> list = folioService.getInventoryIdentifiers(DateUtil.getYesterdayDate(true),
//				DateUtil.getYesterdayDate(false));
//
//		System.out.println("list:" + list.size());
//
//		for (HashMap<String, List<String>> map : list) {
//
//			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
//
//				// List<String> oclcNumbers = new ArrayList<String>();
//
//				System.out.println("id: " + entry.getKey() + ", nos:" + entry.getValue());
//
//				if (entry.getValue().size() > 0) {
//
//					for (String oclcNumber : entry.getValue()) {
//
//						// oclcNumbers.add(oclcNumber.split("#")[1]);
//
//						String oclcNummber = (oclcNumber.split("#")[1]);
//
//						System.out.println(oclcNummber);
//								
//						HoldingRoot holdingRoot = oclcService.getOCLCItems(oclcNummber);
//
//						if (holdingRoot != null && holdingRoot.holdings != null && holdingRoot.holdings.size() > 0) {
//
//							// System.out.println("id: " + entry.getKey() + ", nos:" + entry.getValue());
//
//							for (Holding holding : holdingRoot.holdings) {
//
//								if (!holding.holdingSet) {
//
//									// System.out.println("id: " + entry.getKey() + ", nos:" + entry.getValue());
//
//									System.out.println("holdingSet is False " + holding.requestedControlNumber);
//								}
//							}
//
//						} else {
//
//							// System.out.println("id: " + entry.getKey() + ", nos:" + entry.getValue());
//
//							System.out.println("No Oclc Records found for : " + oclcNummber);
//						}
//
//					}
//				} else {
//					System.out.println("No Records ");
//				}
//
//			}
//		}

	}

}
