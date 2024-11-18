package com.okstatelibrary.redbud.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;
import com.okstatelibrary.redbud.util.StringHelper;

public class UserProertiesUpdateProcess extends MainProcess {

	// Stores the Start time
	protected String startTime;

	private ArrayList<String> messageList;

	public void manipulate(GroupService groupService)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		messageList = new ArrayList<>();

		messageList.add("User properties Update Process" + "<br/>");

		messageList.add("Start Time " + DateUtil.getTodayDateAndTime() + "<br/>");

		// Remove comments in order to run the patron group changes 
//		FolioPatronGroup foliGroups = null;
//
//		try {
//			foliGroups = folioService.getPatronGroups();
//		} catch (RestClientException | IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		for (CsvFileModel csvFileModel : Constants.csvFileModels) {

			String filePath = AppSystemProperties.CvsFilePath + csvFileModel.csvFilePath;

			ArrayList<CsvUserModel> csvUserList = getCsvUsers(filePath);

			messageList.add("FilePath " + filePath);

			messageList.add("csv file size " + csvUserList.size());

			messageList.add("BannerId, Name , folio User Group , custom field user group");

			int count = 0;

			for (CsvUserModel csvUserModel : csvUserList) {

				count++;

//				if (count < 45000) {
//					continue;
//				}

				if (count % 500 == 0) {
					messageList.add("record count " + count);
				}

//					
				String[] customFields = csvUserModel.getUserGroup().split(";");

				String currentUserGroup = customFields[0];

				// Remove comments in order to run the patron group changes 
//				Usergroup futureUserGroup = foliGroups.usergroups.stream()
//						.filter(selGroup -> selGroup.group.toLowerCase().equals(currentUserGroup.toLowerCase()))
//						.findFirst().get();

//					System.out.println(csvUserModel.getBannerId() + " - " + csvUserModel.getFirstName() + " "
//							+ csvUserModel.getLastName() + " - " + csvUserModel.getUserGroup());

				FolioUser folioUser = folioService.getUserByExternalSystemId(csvUserModel.getBannerId());

				if (folioUser != null) {

					try {

						boolean isUserStatusChanged = false;
						
						// Remove comments in order to run the patron group changes 
						// Checking the patron group and if there is an difference updating the Folio.

//						if (!futureUserGroup.id.equals(folioUser.patronGroup)) {
//
//							// Update Code
//							folioUser.patronGroup = futureUserGroup.id;
//
//							CustomFields newCustommFields = new CustomFields();
//							newCustommFields.additionalPatronGroup_4 = csvUserModel.getUserGroup();
//							folioUser.customFields = newCustommFields;
//
//							folioUser.metadata = getMetadata(folioUser.metadata);
//
//							isUserStatusChanged = true;
//
//							updateFolioUser(folioUser, csvUserModel, " - Update Patron group");
//
//						}

						// Check for the bar codes are. If those are null and difference and update
						// FOLIO

						if (!StringHelper.isStringNullOrEmpty(csvUserModel.getISOCode())
								&& !StringHelper.isStringNullOrEmpty(folioUser.barcode)
								&& !folioUser.barcode.contentEquals(csvUserModel.getISOCode())) {

							isUserStatusChanged = true;

							folioUser.barcode = csvUserModel.getISOCode();

							updateFolioUser(folioUser, csvUserModel, " - Update Barcode");

						} else if (!StringHelper.isStringNullOrEmpty(csvUserModel.getISOCode())
								&& StringHelper.isStringNullOrEmpty(folioUser.barcode)) {

							isUserStatusChanged = true;

							folioUser.barcode = csvUserModel.getISOCode();

							updateFolioUser(folioUser, csvUserModel, " - Update Barcode");

						} else if (StringHelper.isStringNullOrEmpty(csvUserModel.getISOCode())
								|| StringHelper.isStringNullOrEmpty(folioUser.barcode)) {

							System.out.println(csvUserModel.getBannerId() + ", " + csvUserModel.getFirstName() + " "
									+ csvUserModel.getLastName() + " - Bar code NUll");
						}

						// Check the user status and if inactive change in to active.

						if (!folioUser.active && !isUserStatusChanged) {

							updateFolioUser(folioUser, csvUserModel, " - Update Status");
						}

					} catch (RestClientException e) {

						e.printStackTrace();

						System.out.println(csvUserModel.getBannerId() + "- " + csvUserModel.getFirstName() + " "
								+ csvUserModel.getLastName() + " - " + currentUserGroup);

					}
				}

			}

			messageList.add(" end of csv file " + csvFileModel.csvFilePath + "<br/>");

		}

		createAndSendEmail();

	}

	private void createAndSendEmail() {

		StringBuilder strBuilder = new StringBuilder();

		for (String message : messageList) {

			strBuilder.append(message + "<br/>");
		}

		strBuilder.append("<br/> End time: " + DateUtil.getTodayDateAndTime());

		this.sendEmaill("Users Property Update Process ", strBuilder.toString());

	}

	private void updateFolioUser(FolioUser folioUser, CsvUserModel csvUserModel, String updateCriteria) {

		folioUser.active = true;
		folioUser.expirationDate = DateUtil.get9MonthsAfterTodayDate();

		folioUser.metadata = getMetadata(folioUser.metadata);

		if (!folioService.updateUser(folioUser)) {

			printScreen("Error modify only Folio " + updateCriteria + folioUser, Constants.ErrorLevel.INFO);

			messageList.add("Error modify only Folio " + updateCriteria + folioUser);

		} else {

			messageList.add(csvUserModel.getBannerId() + ", " + csvUserModel.getFirstName() + " "
					+ csvUserModel.getLastName() + updateCriteria);
		}
	}

	// Get the users reading the csv file.
	public ArrayList<CsvUserModel> getCsvUsers(String filePath) {

		ArrayList<CsvUserModel> csvUserList = new ArrayList<CsvUserModel>();

		File folder = new File(filePath);

		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {

			File csvFile = listOfFiles[i];

			if (csvFile.isFile() && csvFile.getName().contains(".csv")) {

				String line = "";

				try {

					// parsing a CSV file into BufferedReader class constructor
					@SuppressWarnings("resource")
					BufferedReader br = new BufferedReader(new FileReader(csvFile));

					while ((line = br.readLine()) != null) // returns a Boolean value
					{

						String csvInstitution = line.split(",")[0];

						if (csvInstitution.equals("INSTITUTION")) {
							continue;
						}

						try {

							CsvUserModel csvModel = new CsvUserModel(line);

							csvUserList.add(csvModel);

						} catch (Exception e) {
							System.out.println("line" + line);
							e.printStackTrace();
						}

					}

				} catch (IOException e) {

					e.printStackTrace();
				}

			}
		}

		return csvUserList;
	}

}
