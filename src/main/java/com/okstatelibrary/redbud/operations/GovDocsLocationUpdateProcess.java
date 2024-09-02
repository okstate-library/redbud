package com.okstatelibrary.redbud.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.folio.entity.holding.HoldingsRecord;
import com.okstatelibrary.redbud.folio.entity.inventory.Item;
import com.okstatelibrary.redbud.folio.entity.inventory.PermanentLocation;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.service.external.FolioService;
import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

public class GovDocsLocationUpdateProcess extends MainProcess {

	// Stores the Start time
	protected String startTime;

	public void manipulate(GroupService groupService)
			throws JsonParseException, JsonMappingException, RestClientException, IOException, InterruptedException {

		FolioPatronGroup foliGroups = null;

		try {
			foliGroups = folioService.getPatronGroups();
		} catch (RestClientException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Map<String, String> map = new HashMap<>();

//		map.put("OKS-OSU Gov Docs Books", "5786bece-3dae-4d56-85a3-3a5402bf1921");
//		map.put("OKS-OSU Gov Docs Oversize", "240b5123-8b7f-43ce-bc9e-4dc34f1f0ce1");
//		map.put("OKS-OSU Gov Docs Periodical", "30576e1f-4fdb-4d3d-b195-b8dbd075f5b0");
		map.put("OKS-OSU Gov Docs Legal Reference", "59123119-f149-422f-a231-d8de99f7f355");
//		map.put("OKS-OSU Gov Docs Reference, Main", "bb4ba5bf-7443-4a07-a034-20348999267d");
//		map.put("OKS-OSU Gov Docs Reference", "8ef9df9d-0b62-40a8-ac14-4d4e0fc33e17");

		for (Map.Entry<String, String> entry : map.entrySet()) {

			String locationName = entry.getKey();
			String locationId = entry.getValue();

			System.out.println("Location name: " + locationName + ", location Id: " + locationId);

			HoldingRoot holdingRoot = folioService.getHoldingsStorageByLocationId(entry.getValue());

			System.out.println("Total Holdings Records" + holdingRoot.totalRecords);

			int emptyRecords = 0;
			int notEmptyRecords = 0;

			for (HoldingsRecord holding : holdingRoot.holdingsRecords) {

				System.out.println(" holding.instanceId " + holding.id);

				ItemRoot itemRoot = folioService.getInventoryItemById(holding.id);

				System.out.println(" Total item records " + itemRoot.totalRecords);

				for (Item item : itemRoot.items) {

					if (item.permanentLocation == null) {
						emptyRecords++;

						System.out.println("Hoding id" + holding.id + "----");

						item.permanentLocation = new PermanentLocation();

						item.permanentLocation.id = locationId;
						item.permanentLocation.name = locationName;
						folioService.updateInventoryItem(item);

						System.out.println("DONE DONE");

						Thread.sleep(5000);

					} else {
						notEmptyRecords++;
						// System.out.println(item.title + " " + holding.id + "+++");
					}
				}

				System.out.println("emptyRecords-" + emptyRecords + "notEmptyRecords-" + notEmptyRecords);

				System.out.println("");

				emptyRecords = 0;
				notEmptyRecords = 0;

			}

		}

//		for (CsvFileModel csvFileModel : Constants.csvFileModels) {
//
//			String filePath = AppSystemProperties.CvsFilePath + csvFileModel.csvFilePath;
//
//			ArrayList<CsvUserModel> csvUserList = getCsvUsers(filePath);
//
//			System.out.println("filePath " + filePath);
//
//			System.out.println("csv file size " + csvUserList.size());
//
//			System.out.println("BannerId, Name , folio User Group , custom field user group");
//
//			int count = 0;
//
//			for (CsvUserModel csvUserModel : csvUserList) {
//
//				count++;
//
//				if (count % 1000 == 0) {
//					System.out.println("record count " + count);
//
//				}
//
////					System.out.println(csvUserModel.getBannerId() + "- " + csvUserModel.getFirstName() + " "
////							+ csvUserModel.getLastName());
////					
//				String[] customFields = csvUserModel.getUserGroup().split(";");
//
//				String currentUserGroup = customFields[0];
//
//				Usergroup futureUserGroup = foliGroups.usergroups.stream()
//						.filter(selGroup -> selGroup.group.toLowerCase().equals(currentUserGroup.toLowerCase()))
//						.findFirst().get();
//
////					System.out.println(csvUserModel.getBannerId() + " - " + csvUserModel.getFirstName() + " "
////							+ csvUserModel.getLastName() + " - " + csvUserModel.getUserGroup());
//
//				try {
//
//					FolioUser folioUser = folioService.getUserByExternalSystemId(csvUserModel.getBannerId());
//
//					if (folioUser != null) {
//
//						Usergroup folioUserGroup = foliGroups.usergroups.stream()
//								.filter(selGroup -> selGroup.id.equals(folioUser.patronGroup)).findFirst().get();
//
////					System.out.println(folioUser.externalSystemId + " - " + folioUser.personal.firstName + " "
////							+ folioUser.personal.lastName + " - " + pastUserGroup.group + " - "
////							+ futureUserGroup.group);
//
//						if (!futureUserGroup.id.equals(folioUser.patronGroup)) {
//
//							// Update Code
////							folioUser.patronGroup = futureUserGroup.id;
////
////							CustomFields newCustommFields = new CustomFields();
////							newCustommFields.additionalPatronGroup_4 = csvUserModel.getUserGroup();
////							folioUser.customFields = newCustommFields;
////
////							folioUser.metadata = getMetadata(folioUser.metadata);
//
////							if (!folioService.updateUser(folioUser)) {
////
////								printScreen("Error modify only Folio User " + folioUser, Constants.ErrorLevel.INFO);
////
////							} else {
//
//							System.out.println(csvUserModel.getBannerId() + ", " + csvUserModel.getFirstName() + " "
//									+ csvUserModel.getLastName() + "," + folioUserGroup.group + "," + currentUserGroup);
//							// }
//
////						System.out.println(folioUser.externalSystemId + " - " + folioUser.personal.firstName + " "
////								+ folioUser.personal.lastName + " - " + pastUserGroup.group);
//						}
//					}
//
//				} catch (RestClientException | IOException e) {
//
//					e.printStackTrace();
//				}
//
//			}
//
//			System.out.println(" end of csv file " + csvFileModel.csvFilePath);
//
//		}

	}

}
