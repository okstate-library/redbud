package com.okstatelibrary.redbud.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.hibernate.internal.util.StringHelper;

import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

public class UserIntegrationProcess extends MainProcess {

	// Stores the Start time
	protected String startTime;

	public ArrayList<CsvUserModel> inactiveUsers;

	public void manipulate(GroupService groupService) {

		printScreen("UserIntegrationProcess Starts", Constants.ErrorLevel.INFO);

		startTime = DateUtil.getTodayDateAndTime();

		inactiveUsers = new ArrayList<CsvUserModel>();

		ArrayList<CsvRoot> csvUserList = new ArrayList<CsvRoot>();

		try {

			ReportModel report = new ReportModel();

			report.subReports = new ArrayList<SubReportModel>();

			List<PatronGroup> groupList = groupService.getGroupList();

			for (CsvFileModel csvFileModel : Constants.csvFileModels) {

				printScreen("Main Folder " + csvFileModel.csvFilePath, Constants.ErrorLevel.INFO);

				for (String instituteCode : csvFileModel.institueCodes) {

					csvUserList.add(new CsvRoot(instituteCode));
				}

				String filePath = AppSystemProperties.CvsFilePath + csvFileModel.csvFilePath;

				report.fileName = filePath;

				printScreen("FilePath : " + filePath, Constants.ErrorLevel.INFO);

				csvUserList = getCsvUsers(csvUserList, filePath);

				printScreen("Users need to set inactive count " + inactiveUsers.size(), Constants.ErrorLevel.INFO);

				for (String institueCode : csvFileModel.institueCodes) {

					CsvRoot csvRoot = csvUserList.stream().filter(selRoot -> selRoot.institution.equals(institueCode))
							.findAny().orElse(null);

					printScreen("########################################################", Constants.ErrorLevel.INFO);

					printScreen("Campus : " + institueCode + " : total # users in csv:" + csvRoot.users.size(),
							Constants.ErrorLevel.INFO);

					if (csvUserList == null || csvRoot.users.size() == 0) {
						break;
					}

					List<PatronGroup> selGroupList = groupList.stream()
							.filter(selGroup -> selGroup.getInstitutionCode() != null
									&& selGroup.getInstitutionCode().equals(institueCode)
									&& selGroup.isFolioOnly() == 0)
							// && selGroup.getFolioGroupName().equals("OKS-OSU-student-und"))
//									&& selGroup.getInstitutionGroup().equals("FACULTY-V"))
							.collect(Collectors.toList());

					try {

						for (PatronGroup group : selGroupList) {

							SubReportModel subReport = new SubReportModel(institueCode, group.getFolioGroupName());

							printScreen("", Constants.ErrorLevel.INFO);
							printScreen(group.getFolioGroupId() + "   " + group.getFolioGroupName(),
									Constants.ErrorLevel.INFO);

							List<CsvUserModel> csvUsers = csvRoot.users.stream()
									.filter(selUser -> selUser.getMainUserGroup().equals(group.getFolioGroupName()))
									.collect(Collectors.toList());

							printScreen("**************************************", Constants.ErrorLevel.INFO);
							printScreen("** New/modify users - need to add/modify in FOLIO ***",
									Constants.ErrorLevel.INFO);
							printScreen("**************************************", Constants.ErrorLevel.INFO);

							printScreen("New users from CSV " + csvUsers.size(), Constants.ErrorLevel.INFO);

							subReport.setNewUserErrorUserList = new ArrayList<String>();
							subReport.modifiedErrorUserList = new ArrayList<String>();

							FolioUser exist_user = null;

							for (CsvUserModel csvUser : csvUsers) {

								try {

									// printScreen("CSV User " + csvUser.toString(), Constants.ErrorLevel.INFO);

									exist_user = folioService.getUserByExternalSystemId(csvUser.getBannerId());

									// Adding a new user to the system

									try {

										if (exist_user == null) {

											subReport.possibleNewUserCount++;

											printScreen("Possible new User " + csvUser.toString(),
													Constants.ErrorLevel.INFO);

											FolioUser newFolioUser = new FolioUser();

											newFolioUser.active = true;
											newFolioUser.externalSystemId = csvUser.getBannerId();
											newFolioUser.barcode = csvUser.getISOCode();
											newFolioUser.patronGroup = group.getFolioGroupId();
											newFolioUser.username = csvUser.getOkeyUsername();
											newFolioUser.expirationDate = DateUtil.get9MonthsAfterTodayDate();

											Personal newPersonal = new Personal();
											newPersonal.firstName = csvUser.getFirstName();
											newPersonal.lastName = csvUser.getLastName();
											newPersonal.middleName = csvUser.getMiddleName();
											newPersonal.email = csvUser.getOkeyEmail();
											newPersonal.preferredFirstName = csvUser.getPreferedFirstName();
											newPersonal.mobilePhone = csvUser.getLocalPhone();
											newPersonal.phone = csvUser.getWorkPhone();
											newFolioUser.personal = newPersonal;

											CustomFields newCustommFields = new CustomFields();
											newCustommFields.additionalPatronGroup_4 = csvUser.getUserGroup();
											newFolioUser.customFields = newCustommFields;

											newFolioUser.metadata = getMetadata(newFolioUser.metadata);

											String errorMessage = folioService.createUser(newFolioUser);

											if (isStringNullOrEmpty(errorMessage)) {

												printScreen("Added folio user " + newFolioUser.toString(),
														Constants.ErrorLevel.INFO);

												subReport.setNewUserSucessCount++;
											} else {

												printScreen(" Error user adding" + newFolioUser,
														Constants.ErrorLevel.ERROR);

												subReport.setNewUserErrorCount++;

												subReport.setNewUserErrorUserList
														.add(newFolioUser.toString() + " error : " + errorMessage);
											}
										}

									} catch (Exception e1) {

										subReport.setNewUserErrorCount++;

										subReport.setNewUserErrorUserList.add(csvUser.toString());
									}

									// Editing an existing user

									if (exist_user != null) {

										try {

											subReport.possibleModifiedUserCount++;

											// printScreen("Exist User " + exist_user.toString(),
											// Constants.ErrorLevel.INFO);

											List<String> differences = new ArrayList<>();

											// Checking the difference between external id in FOLIO and csv list
											if (!Objects.equals(exist_user.externalSystemId, csvUser.getBannerId())) {

												differences.add("externalSystemId: " + exist_user.externalSystemId
														+ " -> " + csvUser.getBannerId());
											}

											// Checking the difference between barcode in FOLIO and csv list
											if (!StringHelper.isEmptyOrWhiteSpace(csvUser.getISOCode())
													&& !Objects.equals(exist_user.barcode, csvUser.getISOCode())) {

												differences.add("barcode: " + exist_user.barcode + " -> "
														+ csvUser.getISOCode());

												// Bar code update
												if (StringHelper.isEmptyOrWhiteSpace(csvUser.getISOCode())) {
													exist_user.barcode = csvUser.getBannerId();
												} else {
													exist_user.barcode = csvUser.getISOCode();
												}

											}

											// Checking the difference between username in FOLIO and csv list
											if (!Objects.equals(exist_user.username, csvUser.getOkeyUsername())) {

												differences.add("username: " + exist_user.username + " -> "
														+ csvUser.getOkeyUsername());

												exist_user.username = csvUser.getOkeyUsername();
											}

											// Checking the difference between user's status in FOLIO and csv list
											if (!Objects.equals(exist_user.active, true)) {

												differences.add("status: " + exist_user.username + " -> "
														+ csvUser.getOkeyUsername());

												exist_user.active = true;
												exist_user.expirationDate = DateUtil.getActiveUserExpireDate();

											}

											// Checking the difference between user's patron's group name in FOLIO and
											// csv
											// list
											String customField = (exist_user.customFields != null
													&& !StringHelper.isEmptyOrWhiteSpace(
															exist_user.customFields.additionalPatronGroup_4))
																	? exist_user.customFields.additionalPatronGroup_4
																	: "";

											if (!Objects.equals(customField, csvUser.getUserGroup())) {

												differences.add(
														"userGroup: " + customField + " -> " + csvUser.getUserGroup());

												CustomFields newCustommFields = new CustomFields();
												newCustommFields = new CustomFields();
												newCustommFields.additionalPatronGroup_4 = csvUser.getUserGroup();

												exist_user.customFields = newCustommFields;
											}

											// If there is any different update
											if (!differences.isEmpty()) {

												differences.forEach(System.out::println);

												printScreen("Possible edit User " + csvUser.toString() + "\\n"
														+ exist_user.toString(), Constants.ErrorLevel.INFO);

												if (!folioService.updateUser(exist_user)) {
													printScreen("Error modify only Folio User " + exist_user,
															Constants.ErrorLevel.INFO);

													subReport.modifiedErrorUserCount++;
													subReport.modifiedErrorUserList.add(exist_user.toString());

												} else {
													subReport.modifiedSucessUserCount++;
												}

											}

										} catch (Exception e1) {

											subReport.modifiedErrorUserCount++;

											subReport.modifiedErrorUserList.add(csvUser.getBannerId());
										}
									}

								} catch (Exception e1) {

									e1.printStackTrace();
								}

							}

							report.subReports.add(subReport);
						}

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

			}

			report = inactiveUsers(inactiveUsers, report);

			emailReport(report);

			printScreen("UserIntegrationProcess Ends", Constants.ErrorLevel.INFO);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	// **********************
	// Expired user - in Folio user status is updated to inactive and later we are
	// going to delete the record.
	// **********************

	private ReportModel inactiveUsers(ArrayList<CsvUserModel> inactiveUsers, ReportModel report) {

		printScreen("**********************************************************************",
				Constants.ErrorLevel.INFO);
		printScreen("** Inactive Users - need to change the status to inactive to FOLIO ***",
				Constants.ErrorLevel.INFO);
		printScreen("**********************************************************************",
				Constants.ErrorLevel.INFO);

		printScreen("Users need to inactive " + inactiveUsers.size(), Constants.ErrorLevel.INFO);

		report.setToInactiveErrorUserList = new ArrayList<String>();

		for (CsvUserModel csvUser : inactiveUsers) {

			try {

				FolioUser exist_user = folioService.getUserByExternalSystemId(csvUser.getBannerId());

				if (exist_user != null && exist_user.active) {

					printScreen("Possible Inactive User " + exist_user.toString() + "\\n" + exist_user.toString(),
							Constants.ErrorLevel.INFO);

					exist_user.active = false;

					// Error occurred when updating the user to inactive status
					// need to add a dummy record.
					CustomFields newCustommFields = new CustomFields();
					newCustommFields.additionalPatronGroup_4 = Constants.expired_user_cutom_field;
					exist_user.customFields = newCustommFields;

					if (!folioService.updateUser(exist_user)) {

						printScreen("Error modify only Folio User " + exist_user, Constants.ErrorLevel.INFO);

						report.setToInactiveErrorUserCount++;
						report.setToInactiveErrorUserList.add(exist_user.toString());
					} else {
						printScreen("Only Folio User modified " + exist_user, Constants.ErrorLevel.INFO);

						report.setToInactiveSucessUserCount++;
					}

				} else {
					printScreen("Error modifying Folio  user banner id" + csvUser.getBannerId(),
							Constants.ErrorLevel.ERROR);

					report.setToInactiveErrorUserCount++;
					report.setToInactiveErrorUserList.add("Banner id " + csvUser.getBannerId());
				}

			} catch (Exception e1) {

				e1.printStackTrace();

				printScreen("Error inactive user " + csvUser.toString(), Constants.ErrorLevel.ERROR);

			}

		}

		return report;

	}

	// Send the email to the system.
	private void emailReport(ReportModel reportModel) {

		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("Start time:" + startTime + "\n");

		if (reportModel != null && reportModel.fileName != null) {
			strBuilder.append("<br/><br/>");

			strBuilder.append("File name -" + reportModel.fileName + "\n");

		}

		// Email body for the users added and modified.

		for (SubReportModel subReport : reportModel.subReports) {

			strBuilder.append("<br/><br/>");

			strBuilder.append(" Institute  <b> " + subReport.institueCodes + "</b> Patron Group <b> "
					+ subReport.patronGroupName + "</b><br/>");

			strBuilder.append("<table width='80%' border='1' align='center'>");

			strBuilder.append(
					"<tr> <td> Users/Source </td> <td> Possible count </td> <td> Done count </td> <td> Error count </td></tr>");

			strBuilder.append("<tr><td> New users  </td><td>" + subReport.possibleNewUserCount + "</td><td> "
					+ subReport.setNewUserSucessCount + "</td><td> " + subReport.setNewUserErrorCount + "</td></tr>");

			strBuilder.append("<tr><td>Modified Users </td><td>" + subReport.possibleModifiedUserCount + " </td><td>"
					+ subReport.modifiedSucessUserCount + " </td><td>" + subReport.modifiedErrorUserCount
					+ "</td></tr>");

			strBuilder.append("</table>");

			strBuilder.append("</hr>");

			int index = 1;

			if (subReport.setNewUserErrorUserList != null && subReport.setNewUserErrorUserList.size() > 0)

			{
				strBuilder.append("<u> Error occured when adding new users, user list. </u><br/>");

				for (String str : subReport.setNewUserErrorUserList) {
					strBuilder.append(index++ + ". " + str + "<br/>");
				}
				strBuilder.append("<br/><br/>");

			}

			strBuilder.append("</hr>");
			index = 1;

			if (subReport.modifiedErrorUserList != null && subReport.modifiedErrorUserList.size() > 0) {
				strBuilder.append("<u>Error occured when modifying existing users</u><br/>");

				for (String str : subReport.modifiedErrorUserList) {
					strBuilder.append(index++ + ". " + str + "<br/>");
				}

				strBuilder.append("<br/><br/>");
			}

		}

		strBuilder.append("</hr>");

		// Email body for the inactive user status changes.

		strBuilder.append("<u> Users Inactive Error</u><br/>");

		for (String inactiveUserError : reportModel.setToInactiveErrorUserList) {

			strBuilder.append(inactiveUserError + "<br/>");
		}

		strBuilder.append("End time: " + DateUtil.getTodayDateAndTime());

		this.sendEmaill("CSV Reading Process ", strBuilder.toString());

	}

	// Get the users reading the csv file.
	public ArrayList<CsvRoot> getCsvUsers(ArrayList<CsvRoot> csvUserList, String filePath) {

		File folder = new File(filePath);

		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {

			File csvFile = listOfFiles[i];

			if (csvFile.isFile() && csvFile.getName().contains("library_active_users")) {

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

						CsvRoot rootForReport = csvUserList.stream()
								.filter(selRoot -> selRoot.institution.equals(csvInstitution)).findAny().orElse(null);

						try {

							CsvUserModel csvModel = new CsvUserModel(false, line);

							if (csvModel != null) {

								CsvRoot root = csvUserList.stream()
										.filter(selRoot -> selRoot.institution.equals(csvModel.getInstitution()))
										.findAny().orElse(null);

								if (root != null) {
									root.users.add(csvModel);
								}

							}

						} catch (ArrayIndexOutOfBoundsException e) {

							if (rootForReport != null) {
								// ReportModel report = rootForReport.report;
								// report.columnIndexErrorRows++;
							} else {
								e.printStackTrace();
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				} catch (IOException e) {

					e.printStackTrace();
				}

			} else if (csvFile.isFile() && csvFile.getName().contains("inactive")) {

				String line = "";

				try {

					// parsing a CSV file into BufferedReader class constructor
					@SuppressWarnings("resource")
					BufferedReader br = new BufferedReader(new FileReader(csvFile));

					while ((line = br.readLine()) != null) // returns a Boolean value
					{
						String csvInstitution = line.split(",")[0];

						if (csvInstitution.equals("BANNER_ID")) {
							continue;
						}

						try {

							CsvUserModel csvModel = new CsvUserModel(true, line);

							if (csvModel != null) {
								inactiveUsers.add(csvModel);
							}

						} catch (Exception e) {
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
