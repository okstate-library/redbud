package com.okstatelibrary.redbud.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

public class UserIntegrationProcess extends MainProcess {

	// Stores the Start time
	protected String startTime;

	//
	public void manipulate(GroupService groupService) {

		startTime = DateUtil.getTodayDateAndTime();

		ArrayList<CsvRoot> csvUserList = new ArrayList<CsvRoot>();

		try {
			List<PatronGroup> groupList = groupService.getGroupList();

			for (CsvFileModel csvFileModel : Constants.csvFileModels) {

				for (String instituteCode : csvFileModel.institueCodes) {

					csvUserList.add(new CsvRoot(instituteCode));
				}

				String filePath = AppSystemProperties.CvsFilePath + csvFileModel.csvFilePath;

				csvUserList = getCsvUsers(csvUserList, filePath);

				for (String institueCode : csvFileModel.institueCodes) {

					CsvRoot csvRoot = csvUserList.stream().filter(selRoot -> selRoot.institution.equals(institueCode))
							.findAny().orElse(null);

					printScreen(institueCode + "  " + csvRoot.users.size(), Constants.ErrorLevel.INFO);

					if (csvUserList == null || csvRoot.users.size() == 0) {

						break;
					}

					List<PatronGroup> selGroupList = groupList.stream()
							.filter(selGroup -> selGroup.getInstitutionCode() != null
									&& selGroup.getInstitutionCode().equals(institueCode)
									&& selGroup.isFolioOnly() == 0)
//									&& selGroup.getFolioGroupName().equals("OKS-OSU-faculty")
//									&& selGroup.getInstitutionGroup().equals("FACULTY-V"))
							.collect(Collectors.toList());

					try {

						ReportModel report = csvRoot.report;

						report.fileName = filePath;

						report.subReports = new ArrayList<SubReportModel>();

						for (PatronGroup group : selGroupList) {

							SubReportModel subReport = new SubReportModel(institueCode, group.getFolioGroupName());

							printScreen(group.getFolioGroupId() + "   " + group.getFolioGroupName(),
									Constants.ErrorLevel.INFO);

							Root folioRoot = folioService.getUsersbyPatronGroup(group.getFolioGroupId());

							List<CsvUserModel> users = csvRoot.users.stream()
									.filter(selUser -> selUser.getMainUserGroup().equals(group.getFolioGroupName()))
									.collect(Collectors.toList());

							printScreen("Folio Users count - " + folioRoot.users.size() + " CSV Users count - "
									+ users.size(), Constants.ErrorLevel.INFO);

							// **********************
							// USERS IN CSV FILE ONLY
							// **********************

							printScreen("**************************************", Constants.ErrorLevel.INFO);
							printScreen("** New Users ***", Constants.ErrorLevel.INFO);
							printScreen("**************************************", Constants.ErrorLevel.INFO);

							List<CsvUserModel> newUsersFromCSV = users.stream()
									.filter(o1 -> folioRoot.users.stream()
											.noneMatch(o2 -> o2.externalSystemId != null
													&& !o2.externalSystemId.trim().isEmpty()
													&& o2.externalSystemId.equals(o1.getBannerId())))
									.collect(Collectors.toList());

							printScreen("New users from CSV " + newUsersFromCSV.size(), Constants.ErrorLevel.INFO);

							// newUsersFromCSV.forEach(System.out::println);

							subReport.newUsersFromCSVCount = newUsersFromCSV.size();
							subReport.existingUserModified = new ArrayList<String>();
							subReport.newUsersFromCSVAddedFolioErrorUserList = new ArrayList<String>();

							for (CsvUserModel newUser : newUsersFromCSV) {

								FolioUser newFolioUser = new FolioUser();

								newFolioUser.active = true;
								newFolioUser.externalSystemId = newUser.getBannerId();
								newFolioUser.barcode = newUser.getISOCode();
								newFolioUser.patronGroup = group.getFolioGroupId();
								newFolioUser.username = newUser.getOkeyUsername();
								newFolioUser.expirationDate = DateUtil.get9MonthsAfterTodayDate();

								Personal newPersonal = new Personal();
								newPersonal.firstName = newUser.getFirstName();
								newPersonal.lastName = newUser.getLastName();
								newPersonal.middleName = newUser.getMiddleName();
								newPersonal.email = newUser.getOkeyEmail();
								newPersonal.preferredFirstName = newUser.getPreferedFirstName();
								newPersonal.mobilePhone = newUser.getLocalPhone();
								newPersonal.phone = newUser.getWorkPhone();
								newFolioUser.personal = newPersonal;

								CustomFields newCustommFields = new CustomFields();
								newCustommFields.additionalPatronGroup_4 = newUser.getUserGroup();
								newFolioUser.customFields = newCustommFields;

								newFolioUser.metadata = getMetadata(newFolioUser.metadata);

								try {

									String errorMessage = folioService.createUser(newFolioUser);

									if (isStringNullOrEmpty(errorMessage)) {

										printScreen("Added folio user id " + newFolioUser, Constants.ErrorLevel.INFO);

										subReport.newUsersFromCSVAddedFolioCount++;
									} else {

										if (errorMessage.contains("user_exists")) {

											FolioUser exist_user = folioService
													.getUserByExternalSystemId(newFolioUser.externalSystemId);

											if (exist_user != null) {

												exist_user.active = true;
												exist_user.expirationDate = DateUtil.get9MonthsAfterTodayDate();

												newCustommFields = new CustomFields();
												newCustommFields.additionalPatronGroup_4 = newUser.getUserGroup();
												exist_user.customFields = newCustommFields;

												folioService.updateUser(exist_user);

												subReport.existingUserModified.add(exist_user.toString());

											} else {
												printScreen(" User not Added " + newFolioUser,
														Constants.ErrorLevel.ERROR);

												subReport.newUsersFromCSVAddedFolioErrorCount++;

												subReport.newUsersFromCSVAddedFolioErrorUserList
														.add(newFolioUser.toString() + " error : " + errorMessage);
											}

										} else {

											printScreen(" User not Added " + newFolioUser, Constants.ErrorLevel.ERROR);

											subReport.newUsersFromCSVAddedFolioErrorCount++;

											subReport.newUsersFromCSVAddedFolioErrorUserList
													.add(newFolioUser.toString() + " error : " + errorMessage);
										}

									}

								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();

									subReport.newUsersFromCSVAddedFolioErrorCount++;

									subReport.newUsersFromCSVAddedFolioErrorUserList.add(newFolioUser.toString());
								}

							}

							// **********************
							// USERS IN FOLIO AND CSV FILE
							// -- The existing users expire date is going change.
							// **********************

							printScreen("**************************************", Constants.ErrorLevel.INFO);
							printScreen("** Users in both CSV file and Folio***", Constants.ErrorLevel.INFO);
							printScreen("**************************************", Constants.ErrorLevel.INFO);

							List<FolioUser> userInFolioAndCsv = folioRoot.users.stream().filter(o1 -> users.stream()
									.anyMatch(o2 -> o1.externalSystemId != null && !o1.externalSystemId.trim().isEmpty()
											&& o1.externalSystemId.equals(o2.getBannerId())))
									.collect(Collectors.toList());

							List<CsvUserModel> userInFolioAndCsv2 = users.stream().filter(o1 -> folioRoot.users.stream()
									.anyMatch(o2 -> o2.externalSystemId != null && !o2.externalSystemId.trim().isEmpty()
											&& o2.externalSystemId.equals(o1.getBannerId())))
									.collect(Collectors.toList());

							subReport.usersInFolioAndCsvCount = userInFolioAndCsv.size();
							subReport.modifiedUsersInFolioAndCsvErrorUserList = new ArrayList<String>();

							printScreen("user In Folio And Csv " + userInFolioAndCsv.size() + " -- "
									+ userInFolioAndCsv2.size(), Constants.ErrorLevel.INFO);

							for (FolioUser folioUser : userInFolioAndCsv) {

								CsvUserModel commonCSVModel = userInFolioAndCsv2.stream()
										.filter(u -> u.getBannerId().equals(folioUser.externalSystemId)).findAny()
										.orElse(null);

								try {

									if (commonCSVModel != null) {

										if (commonCSVModel.getISOCode() != null
												&& !commonCSVModel.getISOCode().trim().isEmpty()) {
											folioUser.barcode = commonCSVModel.getISOCode();
										}

										folioUser.externalSystemId = commonCSVModel.getBannerId();

										folioUser.username = commonCSVModel.getOkeyUsername();
										folioUser.expirationDate = DateUtil.get9MonthsAfterTodayDate();

										Personal newPersonal = new Personal();
										newPersonal.firstName = commonCSVModel.getFirstName();
										newPersonal.lastName = commonCSVModel.getLastName();
										newPersonal.middleName = commonCSVModel.getMiddleName();
										newPersonal.email = commonCSVModel.getOkeyEmail();
										newPersonal.preferredFirstName = commonCSVModel.getPreferedFirstName();
										newPersonal.mobilePhone = commonCSVModel.getLocalPhone();
										newPersonal.phone = commonCSVModel.getWorkPhone();

										folioUser.personal = newPersonal;

										CustomFields newCustommFields = new CustomFields();
										newCustommFields.additionalPatronGroup_4 = commonCSVModel.getUserGroup();

										folioUser.customFields = newCustommFields;

										folioUser.metadata = getMetadata(folioUser.metadata);

										printScreen("Common user updatinng " + commonCSVModel,
												Constants.ErrorLevel.INFO);

										if (!folioService.updateUser(folioUser)) {
											printScreen("Error modify Folio User " + folioUser,
													Constants.ErrorLevel.ERROR);

											subReport.modifiedUsersInFolioAndCsvErrorCount++;

											subReport.modifiedUsersInFolioAndCsvErrorUserList.add(folioUser.toString());

										} else {
											printScreen("Folio User modified " + folioUser, Constants.ErrorLevel.INFO);
											subReport.modifiedUsersInFolioAndCsvCount++;

										}

									} else {
										printScreen("Common user null " + commonCSVModel, Constants.ErrorLevel.ERROR);
									}

								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();

									printScreen("commonCSVModel" + commonCSVModel, Constants.ErrorLevel.ERROR);

									subReport.modifiedUsersInFolioAndCsvErrorCount++;

									subReport.modifiedUsersInFolioAndCsvErrorUserList.add(folioUser.toString());
								}

							}

							// **********************
							// USERS IN FOLIO ONLY
							// -- Users should be set expired.
							// **********************

							printScreen("**************************************", Constants.ErrorLevel.INFO);
							printScreen("**** USERS IN FOLIO ONLY**************", Constants.ErrorLevel.INFO);
							printScreen("**************************************", Constants.ErrorLevel.INFO);

							List<FolioUser> userInFoliOnly = folioRoot.users.stream()
									.filter(element -> !userInFolioAndCsv.contains(element))
									.collect(Collectors.toList());

							subReport.usersInFoliOnlyCount = userInFoliOnly.size();

							subReport.modifiedUsersInFoliOnlyErrorUserList = new ArrayList<String>();

							printScreen("user InFolio Except Csv " + userInFoliOnly.size(), Constants.ErrorLevel.INFO);

							// userInFoliOnly.forEach(System.out::println);

							for (FolioUser folioUser : userInFoliOnly) {

								try {

									folioUser.active = false;

									// Error occurred when updating the user to inactive status
									// need to add a dummy record.
									CustomFields newCustommFields = new CustomFields();
									newCustommFields.additionalPatronGroup_4 = "Expired user custom field update";
									folioUser.customFields = newCustommFields;

									folioUser.metadata = getMetadata(folioUser.metadata);

//									if (!folioService.updateUser(folioUser)) {
//										printScreen("Error modify only Folio User " + folioUser,
//												Constants.ErrorLevel.INFO);
//
//										subReport.modifiedUsersInFoliOnlyErrorCount++;
//										subReport.modifiedUsersInFoliOnlyErrorUserList
//												.add(folioUser.toString());
//									} else {
//										printScreen("Only Folio User modified " + folioUser,
//												Constants.ErrorLevel.INFO);
//										subReport.modifiedUsersInFoliOnlyCount++;
//									}

								} catch (Exception e1) {

									e1.printStackTrace();

									printScreen("Error modifying Folio  user" + folioUser, Constants.ErrorLevel.ERROR);

									subReport.modifiedUsersInFoliOnlyErrorCount++;
									subReport.modifiedUsersInFoliOnlyErrorUserList.add(folioUser.toString());
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

			emailReport(csvUserList);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	// Send the email to the system.
	private void emailReport(ArrayList<CsvRoot> csvRoots) {

		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("Start time:" + startTime + "\n");

		for (CsvRoot csvRoot : csvRoots) {

			if (csvRoot.report != null && csvRoot.report.fileName != null) {
				strBuilder.append("<br/><br/>");

				strBuilder.append("File name -" + csvRoot.report.fileName + "\n");

			}

			if (csvRoot.report != null && csvRoot.report.subReports != null && csvRoot.report.subReports.size() > 0) {

				strBuilder.append("<br/><br/>");

				for (SubReportModel subReport : csvRoot.report.subReports) {

					strBuilder.append(" Institute  <b> " + subReport.institueCodes + "</b> Patron Group <b> "
							+ subReport.patronGroupName + "</b><br/>");

					strBuilder.append("<table width='80%' border='1' align='center'>");

					strBuilder.append(
							"<tr> <td> Source</td> <td> Possible count </td> <td> Done count </td> <td> Error count </td></tr>");

					strBuilder.append("<tr><td> From CSV - New users </td><td>" + subReport.newUsersFromCSVCount
							+ "</td><td> " + subReport.newUsersFromCSVAddedFolioCount + "</td><td> "
							+ subReport.newUsersFromCSVAddedFolioErrorCount + "</td></tr>");

					strBuilder.append("<tr><td>Users in CSV & Folio</td><td>" + subReport.usersInFolioAndCsvCount
							+ " </td><td>" + subReport.modifiedUsersInFolioAndCsvCount + " </td><td>"
							+ subReport.modifiedUsersInFolioAndCsvErrorCount + "</td></tr>");

					strBuilder.append("<tr><td> Only Folio users</td><td>" + subReport.usersInFoliOnlyCount
							+ " </td><td>" + subReport.modifiedUsersInFoliOnlyCount + " </td><td>"
							+ subReport.modifiedUsersInFoliOnlyErrorCount + "</td></tr> </table>");

					strBuilder.append("<br/><br/>");

					// Users in two Different user groups in Folio and CSV file
					if (subReport.existingUserModified != null && subReport.existingUserModified.size() > 0)

					{
						strBuilder.append("<u>Users in two Different user groups in Folio and CSV file</u><br/>");

						for (String str : subReport.existingUserModified) {
							strBuilder.append(str + "<br/>");
						}
						strBuilder.append("<br/><br/>");

					}

					if (subReport.newUsersFromCSVAddedFolioErrorUserList != null
							&& subReport.newUsersFromCSVAddedFolioErrorUserList.size() > 0)

					{
						strBuilder.append("<u>From CSV - New users  - Add error users</u><br/>");

						for (String str : subReport.newUsersFromCSVAddedFolioErrorUserList) {
							strBuilder.append(str + "<br/>");
						}
						strBuilder.append("<br/><br/>");

					}

					if (subReport.modifiedUsersInFolioAndCsvErrorUserList != null
							&& subReport.modifiedUsersInFolioAndCsvErrorUserList.size() > 0)

					{

						strBuilder.append("<u>Users in CSV & Folio - Modified error users</u><br/>");

						for (String str : subReport.modifiedUsersInFolioAndCsvErrorUserList) {
							strBuilder.append(str + "<br/>");
						}

						strBuilder.append("<br/><br/>");
					}

					if (subReport.modifiedUsersInFoliOnlyErrorUserList != null
							&& subReport.modifiedUsersInFoliOnlyErrorUserList.size() > 0)

					{

						strBuilder.append("<u>Only Folio users - error users</u><br/>");

						for (String str : subReport.modifiedUsersInFoliOnlyErrorUserList) {
							strBuilder.append(str + "<br/>");
						}

						strBuilder.append("<br/><br/>");
					}
				}
			}

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

						CsvRoot rootForReport = csvUserList.stream()
								.filter(selRoot -> selRoot.institution.equals(csvInstitution)).findAny().orElse(null);

						try {

							CsvUserModel csvModel = new CsvUserModel(line);

							if (csvModel != null) {

								CsvRoot root = csvUserList.stream()
										.filter(selRoot -> selRoot.institution.equals(csvModel.getInstitution()))
										.findAny().orElse(null);

								if (root != null) {
									root.users.add(csvModel);
								} else {
								}

							}

						} catch (ArrayIndexOutOfBoundsException e) {

							if (rootForReport != null) {
								ReportModel report = rootForReport.report;
								report.columnIndexErrorRows++;
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

	public ArrayList<Root> getFolioUsers(FolioService folioService, GroupService groupService, String instituteCode)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		ArrayList<Root> userList = new ArrayList<>();

		List<PatronGroup> groups = groupService.getGroupListByInstituteCode(instituteCode);

		for (PatronGroup group : groups) {

			String folioGroupId = group.getFolioGroupId();

			Root root = folioService.getUsersbyPatronGroup(folioGroupId);

			root.folioGroupId = folioGroupId;

			root.folioGroupName = group.getFolioGroupName();

			root.institutionGroup = group.getInstitutionGroup();

			userList.add(root);

		}

		return userList;
	}

}
