package com.okstatelibrary.redbud.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;
import com.okstatelibrary.redbud.util.StringHelper;

public class ChangeExpirationDateOfActiveUsers extends MainProcess {

	protected String startTime;

	private ArrayList<String> messageList;

	public void manipulate(GroupService groupService) {

		startTime = DateUtil.getTodayDateAndTime();

		messageList = new ArrayList<>();

		messageList.add("Change Expiration Date Of Active Users" + "<br/>");

		messageList.add("New expiry date will be " + DateUtil.get9MonthsAfterTodayDate() + "<br/>");

		messageList.add("Start Time " + DateUtil.getTodayDateAndTime() + "<br/>");

		try {

			printScreen("New expiry date" + DateUtil.getCustomDate(), Constants.ErrorLevel.INFO);

			List<PatronGroup> groupList = groupService.getGroupList();

			for (CsvFileModel csvFileModel : Constants.csvFileModels) {

				for (String institueCode : csvFileModel.institueCodes) {

					// && selGroup.getFolioGroupId().equals("02609d66-4b2a-47f6-988a-cf7b5b2932c7")

					List<PatronGroup> selGroupList = groupList.stream()
							.filter(selGroup -> selGroup.getInstitutionCode() != null
									&& selGroup.getInstitutionCode().equals(institueCode) && selGroup.isFolioOnly() == 0
									&& selGroup.getFolioGroupId().equals("c88e6e42-9544-4e5e-ae94-a50c07b9dfbf"))
							.collect(Collectors.toList());

					try {

						for (PatronGroup group : selGroupList) {

							messageList.add(group.getFolioGroupId() + "   " + group.getFolioGroupName());

							Root folioRoot = folioService.getUsersbyPatronGroup(group.getFolioGroupId());

							printScreen(
									"Folio Users count - " + group.getFolioGroupName() + " " + folioRoot.users.size());

							int userCount = 0;

							for (FolioUser folioUser : folioRoot.users) {

//								printScreen("username " + folioUser.username + " ,New expiration Date "
//										+ folioUser.expirationDate);

								folioUser.expirationDate = DateUtil.getCustomDate();

								if (folioUser.customFields == null
										|| folioUser.customFields.additionalPatronGroup_4 == null
										|| folioUser.customFields.additionalPatronGroup_4.isEmpty()) {

									CustomFields newCustommFields = new CustomFields();
									newCustommFields.additionalPatronGroup_4 = group.getFolioGroupName();
									folioUser.customFields = newCustommFields;
								}

								folioUser.metadata = getMetadata(folioUser.metadata);

								if (!folioService.updateUser(folioUser)) {

									printScreen("Error modify only Folio User " + folioUser, Constants.ErrorLevel.INFO);

									messageList.add("Error modify only Folio User " + folioUser);
								} else {
									userCount++;
								}

							}

							printScreen("Number of user updated of expiry date " + userCount,
									Constants.ErrorLevel.INFO);

						}

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

			}

			createAndSendEmail();

		} catch (Exception e1) {
			// TODO Auto-generated catch blocks
			e1.printStackTrace();
		}

	}

	private List<String> getUsers() {

		List<String> fruits = new ArrayList<>();

		try {

			String filePath = "/Users/library-mac/Desktop/osu_projs/extra/";

			File folder = new File(filePath);

			File[] listOfFiles = folder.listFiles();

			// List<PatronGroup> groupList = groupService.getGroupList();

			for (int i = 0; i < listOfFiles.length; i++) {

				File csvFile = listOfFiles[i];

				if (csvFile.isFile() && csvFile.getName().contains(".csv")) {

					System.out.println("File name " + csvFile);

					String line = "";

					try {

						// parsing a CSV file into BufferedReader class constructor
						@SuppressWarnings("resource")
						BufferedReader br = new BufferedReader(new FileReader(csvFile));

						int userCount = 0;
						int userGroupUserCount = 0;

						while ((line = br.readLine()) != null) // returns a Boolean value
						{

							String orgCode = line.split(",")[0];

							String barCode = line.split(",")[2];
							String exterNalSystemId = line.split(",")[1];

							String userGroup = line.split(",")[3];

							if (orgCode.trim().contentEquals("OUH-CHS")
									&& userGroup.trim().contentEquals("OUH-CHS-student-med")) {

								userGroupUserCount++;

								fruits.add(exterNalSystemId);
								fruits.add(barCode);

//								FolioUser folioUser = folioService.getUsersByExternalSystemId(exterNalSystemId);
//
//								// FolioUser folioUser = folioService.getUsersByBarcode(barCode);
//
//								if (folioUser != null) {
//									userCount++;
//
//									System.out.println(folioUser.toString() + " status " + folioUser.active
//											+ " expiry date " + folioUser.expirationDate);
//
////								folioUser.active = true;
////								folioUser.expirationDate = DateUtil.getCustomDate();
////
//////								if (folioUser.customFields == null
//////										|| folioUser.customFields.additionalPatronGroup_4 == null
//////										|| folioUser.customFields.additionalPatronGroup_4.isEmpty()) {
//////
//////									CustomFields newCustommFields = new CustomFields();
//////									newCustommFields.additionalPatronGroup_4 = group.getFolioGroupName();
//////									folioUser.customFields = newCustommFields;
//////								}
////
////								folioUser.metadata = getMetadata(folioUser.metadata);
//
////								if (!folioService.updateUser(folioUser)) {
////
////									printScreen("Error modify only Folio User " + folioUser,
////											Constants.ErrorLevel.INFO);
////
////									// messageList.add("Error modify only Folio User " + folioUser);
////								} else {
////									printScreen("Done User " + folioUser,
////											Constants.ErrorLevel.INFO);
////								}
//
//								} else {
//									System.out.println("exterNalSystemId" + exterNalSystemId);
//								}

							}

//
//						FolioUser folioUser = folioService.getUsersByBarcode(barCode);
//
//						// System.out.println("********************************");
//
//						if (folioUser != null) {
//
//							PatronGroup selectedGroup = groupList.stream()
//									.filter(selGroup -> selGroup.getInstitutionCode() != null
//											&& selGroup.getFolioGroupId().equals(folioUser.patronGroup))
//									.findFirst().get();
//
//							// System.out.println("Folio Group Name" + selectedGroup.getFolioGroupName());
//
//							String birthDate = line.split(",")[2];
//
//							String middleName = line.split(",")[3];
//
//							String email = line.split(",")[6];
//
//							String phoneNumber = "";
//
//							if (line.split(",").length == 8) {
//								phoneNumber = line.split(",")[7];
//							}
//
////							System.out.println("Folio User " + folioUser.personal.dateOfBirth + " : "
////									+ folioUser.personal.middleName + " : " + folioUser.personal.email + " : "
////									+ folioUser.personal.phone);
////
////							System.out.println("CSV User " + birthDate + " : " + middleName + " : " + email + " : "
////									+ phoneNumber);
//
//							if (birthDate != null && !birthDate.trim().isEmpty()) {
//
//								String date = DateUtil.getDate(birthDate).toString();
//
//								folioUser.personal.dateOfBirth = date;
//							}
//
//							folioUser.personal.middleName = middleName;
//							folioUser.personal.email = email;
//							folioUser.personal.phone = phoneNumber;
//
//							if (folioUser.customFields == null) {
//								CustomFields newCustommFields = new CustomFields();
//								newCustommFields.additionalPatronGroup_4 = selectedGroup.getFolioGroupName();
//								folioUser.customFields = newCustommFields;
//							} else {
//								folioUser.customFields.additionalPatronGroup_4 = selectedGroup.getFolioGroupName();
//							}
//
//							boolean isUpdated = folioService.updateUser(folioUser);
//
//							if (!isUpdated) {
//								System.out.println("Not Updated : " + barCode);
//							}
//
////							else {
////								System.out.println("Updated : " + barCode);
////							}
//
//						} else {
//
//							System.out.println("Null folio Users :" + barCode);
//						}

						}

						System.out.println("OUH-CHS-student-med users Count" + userGroupUserCount + " folio user Count "
								+ userCount);

					} catch (Exception e) {

						e.printStackTrace();
					}

				}
			}

		} catch (Exception e1) {

			e1.printStackTrace();
		}
		return fruits;
	}

	private void createAndSendEmail() {

		StringBuilder strBuilder = new StringBuilder();

		for (String message : messageList) {

			strBuilder.append(message + "<br/>");
		}

		strBuilder.append("<br/> End time: " + DateUtil.getTodayDateAndTime());

		this.sendEmaill("End of change Expiration Date Of Active Users ", strBuilder.toString());

	}

}
