package com.okstatelibrary.redbud.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.internal.util.StringHelper;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

public class UserProertiesUpdateProcess extends MainProcess {

	// Stores the Start time
	protected String startTime;

	private ArrayList<String> messageList;

	public void manipulate(GroupService groupService)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		System.out.println("UserProertiesUpdateProcess is start running");
	
		messageList = new ArrayList<>();

		messageList.add("User properties Update Process" + "<br/>");

		messageList.add("Start Time " + DateUtil.getTodayDateAndTime() + "<br/>");

		for (CsvFileModel csvFileModel : Constants.csvFileModels) {

			String filePath = AppSystemProperties.CvsFilePath + csvFileModel.csvFilePath;

			ArrayList<CsvUserModel> csvUserList = getCsvUsers(filePath);

			messageList.add("FilePath " + filePath);

			messageList.add("csv file size " + csvUserList.size());

			messageList.add("BannerId, Name , folio User Group , custom field user group");

			int count = 0;

			for (CsvUserModel csvUser : csvUserList) {

				count++;

				if (count % 500 == 0) {
					messageList.add("record count " + count);
					System.out.println("record count " + count);
				}

				if (count < 3500) {
					continue;
				}

				try {

					FolioUser folio_user = folioService.getUserByExternalSystemId(csvUser.getBannerId());

					if (folio_user != null) {

						String customField = (folio_user.customFields != null
								&& !StringHelper.isEmptyOrWhiteSpace(folio_user.customFields.additionalPatronGroup_4))
										? folio_user.customFields.additionalPatronGroup_4
										: "";
//
//						printScreen(exist_user.externalSystemId + " " + newUser.getBannerId(),
//								Constants.ErrorLevel.INFO);
//						printScreen(exist_user.barcode + " " + newUser.getISOCode(), Constants.ErrorLevel.INFO);
//						printScreen(customField + " " + newUser.getUserGroup(), Constants.ErrorLevel.INFO);
//						printScreen(exist_user.username + " " + newUser.getOkeyUsername(), Constants.ErrorLevel.INFO);

//						printScreen(Objects.equals(exist_user.externalSystemId.trim(),
//								newUser.getBannerId().trim()),
//								Constants.ErrorLevel.INFO);

						List<String> differences = new ArrayList<>();

						if (!Objects.equals(folio_user.externalSystemId, csvUser.getBannerId())) {

							differences.add("externalSystemId: " + folio_user.externalSystemId + " -> "
									+ csvUser.getBannerId());
						}
//
						if (!StringHelper.isEmptyOrWhiteSpace(csvUser.getISOCode())
								&& !Objects.equals(folio_user.barcode, csvUser.getISOCode())) {

							differences.add("barcode: " + folio_user.barcode + " -> " + csvUser.getISOCode());
						}

						if (!Objects.equals(customField, csvUser.getUserGroup())) {

							differences.add("userGroup: " + customField + " -> " + csvUser.getUserGroup());
						}

						if (!Objects.equals(folio_user.username, csvUser.getOkeyUsername())) {

							differences.add("username: " + folio_user.username + " -> " + csvUser.getOkeyUsername());
						}

						// At any moment user group is not changing at edit modeOnly user group change

//						Optional<PatronGroup> selectedUserGroup = groupList.stream()
//								.filter(selGroup -> selGroup.getInstitutionCode() != null && selGroup.isFolioOnly() == 0
//										&& selGroup.getFolioGroupName().equals(csvUser.getMainUserGroup()))
//								.findFirst();
//						
//						if (!Objects.equals(folio_user.patronGroup, selectedUserGroup.get().getFolioGroupId())) {
//
//							differences.add("patronGroup Change id " + folio_user.patronGroup + " -> "
//									+ selectedUserGroup.get().getFolioGroupId());
//
//							differences.add("patronGroup Change name " + csvUser.getUserGroup() + " -> "
//									+ selectedUserGroup.get().getFolioGroupName());
//						}

						if (!differences.isEmpty()) {

							differences.forEach(System.out::println);

							printScreen("Edit User " + csvUser.toString() + folio_user.toString(),
									Constants.ErrorLevel.INFO);

							if (!folio_user.active) {
								folio_user.active = true;

								folio_user.expirationDate = DateUtil.get9MonthsAfterTodayDate();
							}

							if (StringHelper.isEmptyOrWhiteSpace(csvUser.getISOCode())) {
								folio_user.barcode = csvUser.getBannerId();
							} else {
								folio_user.barcode = csvUser.getISOCode();
							}

							folio_user.username = csvUser.getOkeyUsername();

							CustomFields newCustommFields = new CustomFields();
							newCustommFields.additionalPatronGroup_4 = csvUser.getUserGroup();
							folio_user.customFields = newCustommFields;

							folio_user.metadata = getMetadata(folio_user.metadata);

							if (!folioService.updateUser(folio_user)) {
								printScreen("Error modify only Folio User " + folio_user, Constants.ErrorLevel.INFO);

							} else {
								printScreen("User updated " + folio_user, Constants.ErrorLevel.INFO);
							}

						}
					}

				} catch (RestClientException e) {

					e.printStackTrace();

					printScreen("Error User " + csvUser.getBannerId(), Constants.ErrorLevel.INFO);

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

	// Get the users reading the csv file.
	public ArrayList<CsvUserModel> getCsvUsers(String filePath) {

		ArrayList<CsvUserModel> csvUserList = new ArrayList<CsvUserModel>();

		File folder = new File(filePath);

		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {

			File csvFile = listOfFiles[i];

			if (csvFile.isFile() && csvFile.getName().contains("library_folio")) {

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

							CsvUserModel csvModel = new CsvUserModel(false, line);

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
