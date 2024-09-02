package com.okstatelibrary.redbud.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.service.external.FolioService;
import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

public class UserPatronGroupUpdateProcess extends MainProcess {

	// Stores the Start time
	protected String startTime;

	public void manipulate(GroupService groupService) {

		FolioPatronGroup foliGroups = null;

		try {
			foliGroups = folioService.getPatronGroups();
		} catch (RestClientException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (CsvFileModel csvFileModel : Constants.csvFileModels) {

			String filePath = AppSystemProperties.CvsFilePath + csvFileModel.csvFilePath;

			ArrayList<CsvUserModel> csvUserList = getCsvUsers(filePath);

			System.out.println("filePath " + filePath);

			System.out.println("csv file size " + csvUserList.size());

			System.out.println("BannerId, Name , folio User Group , custom field user group");

			int count = 0;

			for (CsvUserModel csvUserModel : csvUserList) {

				count++;

				if (count % 1000 == 0) {
					System.out.println("record count " + count);

				}

//					
				String[] customFields = csvUserModel.getUserGroup().split(";");

				String currentUserGroup = customFields[0];

//				System.out.println(csvUserModel.getBannerId() + "- " + csvUserModel.getFirstName() + " "
//						+ csvUserModel.getLastName() + " - "  + currentUserGroup);

				Usergroup futureUserGroup = foliGroups.usergroups.stream()
						.filter(selGroup -> selGroup.group.toLowerCase().equals(currentUserGroup.toLowerCase()))
						.findFirst().get();

//					System.out.println(csvUserModel.getBannerId() + " - " + csvUserModel.getFirstName() + " "
//							+ csvUserModel.getLastName() + " - " + csvUserModel.getUserGroup());

				try {

					FolioUser folioUser = folioService.getUserByExternalSystemId(csvUserModel.getBannerId());

					if (folioUser != null) {

						Usergroup folioUserGroup = foliGroups.usergroups.stream()
								.filter(selGroup -> selGroup.id.equals(folioUser.patronGroup)).findFirst().get();

//					System.out.println(folioUser.externalSystemId + " - " + folioUser.personal.firstName + " "
//							+ folioUser.personal.lastName + " - " + pastUserGroup.group + " - "
//							+ futureUserGroup.group);

						if (!futureUserGroup.id.equals(folioUser.patronGroup)) {

							// Update Code
							folioUser.patronGroup = futureUserGroup.id;

							CustomFields newCustommFields = new CustomFields();
							newCustommFields.additionalPatronGroup_4 = csvUserModel.getUserGroup();
							folioUser.customFields = newCustommFields;

							folioUser.metadata = getMetadata(folioUser.metadata);

							if (!folioService.updateUser(folioUser)) {

								printScreen("Error modify only Folio User " + folioUser, Constants.ErrorLevel.INFO);

							} else {

								System.out.println(csvUserModel.getBannerId() + ", " + csvUserModel.getFirstName() + " "
										+ csvUserModel.getLastName() + "," + folioUserGroup.group + ","
										+ currentUserGroup);
							}

//						System.out.println(folioUser.externalSystemId + " - " + folioUser.personal.firstName + " "
//								+ folioUser.personal.lastName + " - " + pastUserGroup.group);
						}
					}

				} catch (RestClientException | IOException e) {

					e.printStackTrace();
				}

			}

			System.out.println(" end of csv file " + csvFileModel.csvFilePath);

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

	// Get the users reading the csv file.
	public ArrayList<String> getValues(String filePath) throws IOException {

		ArrayList<String> idList = new ArrayList<String>();

		String line = "";

		// parsing a CSV file into BufferedReader class constructor
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(filePath));

		while ((line = br.readLine()) != null) // returns a Boolean value
		{
			idList.add(line);
		}

		return idList;
	}

}
