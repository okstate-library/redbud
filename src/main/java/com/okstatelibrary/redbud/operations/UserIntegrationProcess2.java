package com.okstatelibrary.redbud.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.internal.util.StringHelper;

import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

public class UserIntegrationProcess2 extends MainProcess {

	// Stores the Start time
	protected String startTime;

	public ArrayList<CsvUserModel> inactiveUsers;

	public void manipulate(GroupService groupService) {

		printScreen("UserIntegrationProcess Starts", Constants.ErrorLevel.INFO);

		startTime = DateUtil.getTodayDateAndTime();

		inactiveUsers = new ArrayList<CsvUserModel>();

		ArrayList<CsvRoot> csvUserList = new ArrayList<CsvRoot>();

		try {

			List<PatronGroup> groupList = groupService.getGroupList();

			for (CsvFileModel csvFileModel : Constants.csvFileModels) {

				printScreen("Main Folder " + csvFileModel.csvFilePath, Constants.ErrorLevel.INFO);

				for (String instituteCode : csvFileModel.institueCodes) {

					csvUserList.add(new CsvRoot(instituteCode));
				}

				String filePath = AppSystemProperties.CvsFilePath + csvFileModel.csvFilePath;

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
									&& selGroup.getInstitutionCode().equals(institueCode) && selGroup.isFolioOnly() == 0)
									//&& selGroup.getFolioGroupName().equals("OKS-OSU-staff"))
//									&& selGroup.getInstitutionGroup().equals("FACULTY-V"))
							.collect(Collectors.toList());

					try {

						ReportModel report = csvRoot.report;

						report.fileName = filePath;

						report.subReports = new ArrayList<SubReportModel>();

						for (PatronGroup group : selGroupList) {

							SubReportModel subReport = new SubReportModel(institueCode, group.getFolioGroupName());

							printScreen("", Constants.ErrorLevel.INFO);
							printScreen(group.getFolioGroupId() + "   " + group.getFolioGroupName(),
									Constants.ErrorLevel.INFO);

							Root folioRoot = folioService.getUsersbyPatronGroup(group.getFolioGroupId());

							List<CsvUserModel> csvUsers = csvRoot.users.stream()
									.filter(selUser -> selUser.getMainUserGroup().equals(group.getFolioGroupName()))
									.collect(Collectors.toList());

//							folioRoot.users.forEach(user -> System.out.println(user.externalSystemId));
//
//							printScreen("----- dsadd -----", Constants.ErrorLevel.INFO);
//
//							csvUsers.forEach(user -> System.out.println(user.getBannerId()));

							printScreen("Folio Users count - " + folioRoot.users.size() + " CSV Users count - "
									+ csvUsers.size(), Constants.ErrorLevel.INFO);

							printScreen("**************************************", Constants.ErrorLevel.INFO);
							printScreen("** New/modify users - need to add/modify in FOLIO ***",
									Constants.ErrorLevel.INFO);
							printScreen("**************************************", Constants.ErrorLevel.INFO);

							Set<String> folioIds = folioRoot.users.stream().map(u -> u.externalSystemId)
									.filter(Objects::nonNull).map(String::trim).collect(Collectors.toSet());

							List<CsvUserModel> newUsersFromCSV = csvUsers.stream()
									.filter(csvUser -> !folioIds.contains(csvUser.getBannerId()))
									.collect(Collectors.toList());

							printScreen("New users from CSV " + newUsersFromCSV.size(), Constants.ErrorLevel.INFO);

							// newUsersFromCSV.forEach(user -> System.out.println(user.getBannerId()));

							subReport.setNewUserCount = newUsersFromCSV.size();
							subReport.setNewUserErrorUserList = new ArrayList<String>();

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

									FolioUser exist_user = folioService
											.getUserByExternalSystemId(newFolioUser.externalSystemId);

									if (exist_user == null) {

										// **********************
										// Add new users to the FOLIO
										// **********************

										printScreen("New User " + newUser.toString(), Constants.ErrorLevel.INFO);

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

									} else {
										subReport.setNewUserErrorCount++;

										subReport.setNewUserErrorUserList.add(newFolioUser.toString()
												+ " error : user exists with active/inactive or different patron group ");
									}

								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();

									subReport.setNewUserErrorCount++;

									subReport.setNewUserErrorUserList.add(newFolioUser.toString());
								}

							}

							// **********************
							// Users should modify based on the csv file details.
							// **********************

							List<CsvUserModel> modifiedUsersFromCSV = csvUsers.stream().filter(o1 -> folioRoot.users
									.stream()
									.anyMatch(o2 -> o2.externalSystemId != null && !o2.externalSystemId.trim().isEmpty()
											&& o2.externalSystemId.equals(o1.getBannerId())))
									.collect(Collectors.toList());

							subReport.usersInFolioAndCsvCount = modifiedUsersFromCSV.size();
							subReport.modifiedErrorUserList = new ArrayList<String>();

							printScreen("Modify users from CSV " + modifiedUsersFromCSV.size(),
									Constants.ErrorLevel.INFO);

							for (CsvUserModel modifyUser : modifiedUsersFromCSV) {

								FolioUser exist_user = folioService.getUserByExternalSystemId(modifyUser.getBannerId());

								try {

									if (exist_user != null) {

										List<String> differences = new ArrayList<>();

										if (!Objects.equals(exist_user.externalSystemId, modifyUser.getBannerId())) {

											differences.add("externalSystemId: " + exist_user.externalSystemId + " -> "
													+ modifyUser.getBannerId());
										}

										//
										if (!StringHelper.isEmptyOrWhiteSpace(modifyUser.getISOCode())
												&& !Objects.equals(exist_user.barcode, modifyUser.getISOCode())) {

											differences.add("barcode: " + exist_user.barcode + " -> "
													+ modifyUser.getISOCode());
										}

										String customField = (exist_user.customFields != null && !StringHelper
												.isEmptyOrWhiteSpace(exist_user.customFields.additionalPatronGroup_4))
														? exist_user.customFields.additionalPatronGroup_4
														: "";

										if (!Objects.equals(customField, modifyUser.getUserGroup())) {

											differences.add(
													"userGroup: " + customField + " -> " + modifyUser.getUserGroup());
										}

										if (!Objects.equals(exist_user.username, modifyUser.getOkeyUsername())) {

											differences.add("username: " + exist_user.username + " -> "
													+ modifyUser.getOkeyUsername());
										}

										if (!differences.isEmpty()) {

											differences.forEach(System.out::println);

											printScreen("Edit User " + modifyUser.toString() + "\\n"
													+ exist_user.toString(), Constants.ErrorLevel.INFO);

											// Only active users are exists in the csv file
											// so if the users are inactive they should convert to active.

											if (!exist_user.active) {

												exist_user.active = true;
												exist_user.expirationDate = DateUtil.get9MonthsAfterTodayDate();

											}

											// Bar code update
											if (StringHelper.isEmptyOrWhiteSpace(modifyUser.getISOCode())) {
												exist_user.barcode = modifyUser.getBannerId();
											} else {
												exist_user.barcode = modifyUser.getISOCode();
											}

											exist_user.username = modifyUser.getOkeyUsername();

											// User group update only

											CustomFields newCustommFields = new CustomFields();
											newCustommFields = new CustomFields();
											newCustommFields.additionalPatronGroup_4 = modifyUser.getUserGroup();
											exist_user.customFields = newCustommFields;

											// exist_user.metadata = getMetadata(newFolioUser.metadata);

											if (!folioService.updateUser(exist_user)) {
												printScreen("Error modify only Folio User " + exist_user,
														Constants.ErrorLevel.INFO);

												subReport.modifiedErrorUserCount++;
												subReport.modifiedErrorUserList.add(exist_user.toString());
											} else {
												subReport.modifiedSucessUserCount++;
											}

										} else {
											subReport.modifiedErrorUserCount++;
											subReport.modifiedErrorUserList.add(modifyUser.toString() + "\n"
													+ exist_user.toString() + " - No differences");
										}

									} else {
										subReport.modifiedErrorUserCount++;
										subReport.modifiedErrorUserList
												.add(modifyUser.toString() + " - user might not exist in the FOLIO");
									}

								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();

									subReport.modifiedErrorUserCount++;

									subReport.modifiedErrorUserList.add(modifyUser.getBannerId());
								}

							}

							// **********************
							// Expired user - in Folio user status is updated to inactive and later we are
							// going to delete the record.
							// **********************

							printScreen("**********************************************************************",
									Constants.ErrorLevel.INFO);
							printScreen("** Inactive Users - need to change the status to inactive to FOLIO ***",
									Constants.ErrorLevel.INFO);
							printScreen("**********************************************************************",
									Constants.ErrorLevel.INFO);

							Set<String> bannerIds = inactiveUsers.stream().map(o -> o.getBannerId())
									.filter(id -> id != null && !id.trim().isEmpty()).collect(Collectors.toSet());

							List<FolioUser> setInactiveUsers = folioRoot.users.stream()
									.filter(u -> bannerIds.contains(u.externalSystemId)).collect(Collectors.toList());

							subReport.setToInactiveUserCount = setInactiveUsers.size();

							subReport.setToInactiveErrorUserList = new ArrayList<String>();

							printScreen("Users need to inactive " + setInactiveUsers.size(), Constants.ErrorLevel.INFO);

							for (FolioUser folioUser : setInactiveUsers) {

								try {

									printScreen(folioUser.toString(), Constants.ErrorLevel.INFO);

									folioUser.active = false;

									// Error occurred when updating the user to inactive status
									// need to add a dummy record.
									CustomFields newCustommFields = new CustomFields();
									newCustommFields.additionalPatronGroup_4 = Constants.expired_user_cutom_field;
									folioUser.customFields = newCustommFields;

									folioUser.metadata = getMetadata(folioUser.metadata);

									if (!folioService.updateUser(folioUser)) {
										printScreen("Error modify only Folio User " + folioUser,
												Constants.ErrorLevel.INFO);

										subReport.setToInactiveErrorUserCount++;
										subReport.setToInactiveErrorUserList.add(folioUser.toString());
									} else {
										printScreen("Only Folio User modified " + folioUser, Constants.ErrorLevel.INFO);

										subReport.setToInactiveSucessUserCount++;
									}

								} catch (Exception e1) {

									e1.printStackTrace();

									printScreen("Error modifying Folio  user" + folioUser.toString(),
											Constants.ErrorLevel.ERROR);

									subReport.setToInactiveErrorUserCount++;
									subReport.setToInactiveErrorUserList.add(folioUser.toString());
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

			printScreen("UserIntegrationProcess Starts", Constants.ErrorLevel.INFO);

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
							"<tr> <td> Users/Source </td> <td> Possible count </td> <td> Done count </td> <td> Error count </td></tr>");

					strBuilder.append("<tr><td> New users  </td><td>" + subReport.setNewUserCount + "</td><td> "
							+ subReport.setNewUserSucessCount + "</td><td> " + subReport.setNewUserErrorCount
							+ "</td></tr>");

					strBuilder.append("<tr><td>Modified Users </td><td>" + subReport.usersInFolioAndCsvCount
							+ " </td><td>" + subReport.modifiedSucessUserCount + " </td><td>"
							+ subReport.modifiedErrorUserCount + "</td></tr>");

					strBuilder.append("<tr><td> Inactive </td><td>" + subReport.setToInactiveUserCount + " </td><td>"
							+ subReport.setToInactiveSucessUserCount + " </td><td>"
							+ subReport.setToInactiveErrorUserCount + "</td></tr> </table>");

					strBuilder.append("<br/><br/>");

					// Users in two Different user groups in Folio and CSV file
//					if (subReport.existingUserModified != null && subReport.existingUserModified.size() > 0)
//					{
//						strBuilder.append("<u>Users in two Different user groups in Folio and CSV file</u><br/>");
//
//						for (String str : subReport.existingUserModified) {
//							strBuilder.append(str + "<br/>");
//						}
//						strBuilder.append("<br/><br/>");
//
//					}

					int index = 1;

					if (subReport.setNewUserErrorUserList != null && subReport.setNewUserErrorUserList.size() > 0)

					{
						strBuilder.append("<u> Error occured when adding new users, user list. </u><br/>");

						for (String str : subReport.setNewUserErrorUserList) {
							strBuilder.append(index++ + ". " + str + "<br/>");
						}
						strBuilder.append("<br/><br/>");

					}

					index = 1;

					if (subReport.modifiedErrorUserList != null && subReport.modifiedErrorUserList.size() > 0)

					{

						strBuilder.append("<u>Error occured when modifying existing users</u><br/>");

						for (String str : subReport.modifiedErrorUserList) {
							strBuilder.append(index++ + ". " + str + "<br/>");
						}

						strBuilder.append("<br/><br/>");
					}

					index = 1;
					if (subReport.setToInactiveErrorUserList != null && subReport.setToInactiveErrorUserList.size() > 0)

					{

						strBuilder.append("<u>Error occured when modifying status to inactive existing users</u><br/>");

						for (String str : subReport.setToInactiveErrorUserList) {
							strBuilder.append(index++ + ". " + str + "<br/>");
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

			if (csvFile.isFile() && csvFile.getName().contains("add_update")) {

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

						CsvRoot rootForReport = csvUserList.stream()
								.filter(selRoot -> selRoot.institution.equals(csvInstitution)).findAny().orElse(null);

						try {

							CsvUserModel csvModel = new CsvUserModel(true, line);

							if (csvModel != null) {

								inactiveUsers.add(csvModel);

//								CsvRoot root = csvUserList.stream().findAny().orElse(null);
//
//								if (root != null) {
//									root.expireUsers.add(csvModel);
//								}

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

}
