package com.okstatelibrary.redbud.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

public class InactiveUserProcess extends MainProcess {

	protected String startTime;

	public void manipulate(GroupService groupService) {

		startTime = DateUtil.getTodayDateAndTime();

		// ArrayList<CsvRoot> csvUserList = new ArrayList<CsvRoot>();

		try {
			List<PatronGroup> groupList = groupService.getGroupList();

			for (CsvFileModel csvFileModel : Constants.csvFileModels) {

				String filePath = "";

				for (String institueCode : csvFileModel.institueCodes) {

//					CsvRoot csvRoot = csvUserList.stream().filter(selRoot -> selRoot.institution.equals(institueCode))
//							.findAny().orElse(null);
//
//					printScreen(institueCode + "  " + csvRoot.users.size(), Constants.ErrorLevel.INFO);
//
//					if (csvUserList == null || csvRoot.users.size() == 0) {
//
//						break;
//					}

					List<PatronGroup> selGroupList = groupList.stream()
							.filter(selGroup -> selGroup.getInstitutionCode() != null
									&& selGroup.getInstitutionCode().equals(institueCode)
									&& selGroup.isFolioOnly() == 0)
							.collect(Collectors.toList());

					try {

						ReportModel report = new ReportModel(); // csvRoot.report;

						report.fileName = filePath;

						report.subReports = new ArrayList<SubReportModel>();

						for (PatronGroup group : selGroupList) {

//							SubReportModel subReport = new SubReportModel(institueCode, group.getFolioGroupName());
//
//							subReport.modifiedUsersInFolioAndCsvUserList = new ArrayList<String>();
//							subReport.modifiedUsersInFoliOnlyErrorUserList = new ArrayList<String>();
//
//							printScreen(group.getFolioGroupId() + "   " + group.getFolioGroupName(),
//									Constants.ErrorLevel.INFO);

							Root folioRoot = folioService.getUsersbyPatronGroup(group.getFolioGroupId());

//							List<CsvUserModel> users = csvRoot.users.stream()
//									.filter(selUser -> selUser.getMainUserGroup().equals(group.getFolioGroupName()))
//									.collect(Collectors.toList());
//
							printScreen(
									"Folio Users count - " + group.getFolioGroupName() + " " + folioRoot.users.size(),
									Constants.ErrorLevel.INFO);
//

							for (FolioUser folioUser : folioRoot.users) {

								folioUser.active = true;
								folioUser.expirationDate = DateUtil.get9MonthsAfterTodayDate();

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

//									subReport.modifiedUsersInFoliOnlyErrorCount++;
//									subReport.modifiedUsersInFoliOnlyErrorUserList.add(folioUser.toString());
								}
							}

//							// **********************
//							// USERS IN BOTH CSV FILE AND FOLIO
//							// **********************
//
//							subReport.existingUserModified = new ArrayList<String>();
//
//							for (CsvUserModel csvUser : users) {
//
//								FolioUser folioUser = folioService.getUsersByExternalSystemId(csvUser.getBannerId());
//
//								if (folioUser != null) {
//
//									folioUser.active = true;
//									folioUser.expirationDate = DateUtil.get9MonthsAfterTodayDate();
//
//									CustomFields newCustommFields = new CustomFields();
//									newCustommFields.additionalPatronGroup_4 = csvUser.getUserGroup();
//									folioUser.customFields = newCustommFields;
//
//									folioUser.metadata = getMetadata(folioUser.metadata);
//
//									if (!folioService.updateUser(folioUser)) {
//										printScreen("Error modify only Folio User " + folioUser,
//												Constants.ErrorLevel.INFO);
//
//										subReport.modifiedUsersInFoliOnlyErrorCount++;
//										subReport.modifiedUsersInFoliOnlyErrorUserList.add(folioUser.toString());
//									} else {
//
//										PatronGroup foliogrgoup = groupList.stream()
//												.filter(g -> g.getFolioGroupId().equals(folioUser.patronGroup))
//												.findAny().orElse(null);
//
//										String message = csvUser.toString() + " FOLIO :- "
//												+ foliogrgoup.getFolioGroupName();
//
//										printScreen(message, Constants.ErrorLevel.INFO);
//
//										subReport.modifiedUsersInFolioAndCsvCount++;
//										subReport.modifiedUsersInFolioAndCsvUserList.add(message);
//									}
//
//								}
//
//							}
//
//							report.subReports.add(subReport);

						}

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

			}

			// emailUserInactiveReport(csvUserList);

		} catch (Exception e1) {
			// TODO Auto-generated catch blocks
			e1.printStackTrace();
		}

	}

//	public void manipulate(GroupService groupService, String cvsfilepath) {
//
//		startTime = DateUtil.getTodayDateAndTime();
//
//		ArrayList<CsvRoot> csvUserList = new ArrayList<CsvRoot>();
//
//		try {
//			List<PatronGroup> groupList = groupService.getGroupList();
//
//			for (CsvFileModel csvFileModel : Constants.csvFileModels) {
//
//				for (String instituteCode : csvFileModel.institueCodes) {
//
//					csvUserList.add(new CsvRoot(instituteCode));
//				}
//
//				String filePath = cvsfilepath + csvFileModel.csvFilePath;
//
//				csvUserList = getCsvUsers(csvUserList, filePath);
//
//				for (String institueCode : csvFileModel.institueCodes) {
//
//					CsvRoot csvRoot = csvUserList.stream().filter(selRoot -> selRoot.institution.equals(institueCode))
//							.findAny().orElse(null);
//
//					printScreen(institueCode + "  " + csvRoot.users.size(), Constants.ErrorLevel.INFO);
//
//					if (csvUserList == null || csvRoot.users.size() == 0) {
//
//						break;
//					}
//
//					List<PatronGroup> selGroupList = groupList.stream()
//							.filter(selGroup -> selGroup.getInstitutionCode() != null
//									&& selGroup.getInstitutionCode().equals(institueCode)
//									&& selGroup.isFolioOnly() == 0)
//							.collect(Collectors.toList());
//
//					try {
//
//						ReportModel report = csvRoot.report;
//
//						report.fileName = filePath;
//
//						report.subReports = new ArrayList<SubReportModel>();
//
//						for (PatronGroup group : selGroupList) {
//
//							SubReportModel subReport = new SubReportModel(institueCode, group.getFolioGroupName());
//
//							subReport.modifiedUsersInFolioAndCsvUserList = new ArrayList<String>();
//							subReport.modifiedUsersInFoliOnlyErrorUserList = new ArrayList<String>();
//
//							printScreen(group.getFolioGroupId() + "   " + group.getFolioGroupName(),
//									Constants.ErrorLevel.INFO);
//
//							Root folioRoot = folioService.getUsersbyPatronGroup(group.getFolioGroupId());
//
//							List<CsvUserModel> users = csvRoot.users.stream()
//									.filter(selUser -> selUser.getMainUserGroup().equals(group.getFolioGroupName()))
//									.collect(Collectors.toList());
//
//							printScreen("Folio Users count - " + folioRoot.users.size() + " CSV Users count - "
//									+ users.size(), Constants.ErrorLevel.INFO);
//
//							// **********************
//							// USERS IN BOTH CSV FILE AND FOLIO
//							// **********************
//
//							subReport.existingUserModified = new ArrayList<String>();
//
//							for (CsvUserModel csvUser : users) {
//
//								FolioUser folioUser = folioService.getUsersByExternalSystemId(csvUser.getBannerId());
//
//								if (folioUser != null) {
//
//									folioUser.active = true;
//									folioUser.expirationDate = DateUtil.get9MonthsAfterTodayDate();
//
//									CustomFields newCustommFields = new CustomFields();
//									newCustommFields.additionalPatronGroup_4 = csvUser.getUserGroup();
//									folioUser.customFields = newCustommFields;
//
//									folioUser.metadata = getMetadata(folioUser.metadata);
//
//									if (!folioService.updateUser(folioUser)) {
//										printScreen("Error modify only Folio User " + folioUser,
//												Constants.ErrorLevel.INFO);
//
//										subReport.modifiedUsersInFoliOnlyErrorCount++;
//										subReport.modifiedUsersInFoliOnlyErrorUserList.add(folioUser.toString());
//									} else {
//
//										PatronGroup foliogrgoup = groupList.stream()
//												.filter(g -> g.getFolioGroupId().equals(folioUser.patronGroup))
//												.findAny().orElse(null);
//
//										String message = csvUser.toString() + " FOLIO :- "
//												+ foliogrgoup.getFolioGroupName();
//
//										printScreen(message, Constants.ErrorLevel.INFO);
//
//										subReport.modifiedUsersInFolioAndCsvCount++;
//										subReport.modifiedUsersInFolioAndCsvUserList.add(message);
//									}
//
//								}
//
//							}
//
//							report.subReports.add(subReport);
//
//						}
//
//					} catch (Exception e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//
//				}
//
//			}
//
//			emailUserInactiveReport(csvUserList);
//
//		} catch (Exception e1) {
//			// TODO Auto-generated catch blocks
//			e1.printStackTrace();
//		}
//
//	}

	private void emailUserInactiveReport(ArrayList<CsvRoot> csvRoots) {

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

					// Users in two Different user groups in Folio and CSV file
					if (subReport.modifiedUsersInFolioAndCsvUserList != null
							&& subReport.modifiedUsersInFolioAndCsvUserList.size() > 0) {

						strBuilder.append("<u>Users in both Folio and CSV file update status to active</u><br/>");
						strBuilder.append("<u>Count: " + subReport.modifiedUsersInFolioAndCsvCount + "</u><br/>");

						for (String str : subReport.modifiedUsersInFolioAndCsvUserList) {
							strBuilder.append(str + "<br/>");
						}
						strBuilder.append("<br/><br/>");

					}

					if (subReport.modifiedUsersInFoliOnlyErrorUserList != null
							&& subReport.modifiedUsersInFoliOnlyErrorUserList.size() > 0) {

						strBuilder.append(
								"<u>Users in both Folio and CSV file update status to active - error users</u><br/>");
						strBuilder.append("<u>Count: " + subReport.modifiedUsersInFoliOnlyErrorCount + "</u><br/>");

						for (String str : subReport.modifiedUsersInFoliOnlyErrorUserList) {
							strBuilder.append(str + "<br/>");
						}
						strBuilder.append("<br/><br/>");

					}

				}
			}

		}

		strBuilder.append("End time: " + DateUtil.getTodayDateAndTime());

		this.sendEmaill("UserInactiveRepor", strBuilder.toString());

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

}
