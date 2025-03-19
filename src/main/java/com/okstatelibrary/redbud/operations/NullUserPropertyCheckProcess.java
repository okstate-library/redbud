package com.okstatelibrary.redbud.operations;

import java.util.List;
import java.util.stream.Collectors;

import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

public class NullUserPropertyCheckProcess extends MainProcess {

	// Stores the Start time
	protected String startTime;

	//
	public void manipulate(GroupService groupService) {

		startTime = DateUtil.getTodayDateAndTime();

		// ArrayList<CsvRoot> csvUserList = new ArrayList<CsvRoot>();

		try {
			List<PatronGroup> groupList = groupService.getGroupList();

			System.out.println("Start");

			
			System.out.println("User id , username,  name , status");
			
			for (CsvFileModel csvFileModel : Constants.csvFileModels) {

				for (String institueCode : csvFileModel.institueCodes) {

					List<PatronGroup> selGroupList = groupList.stream()
							.filter(selGroup -> selGroup.getInstitutionCode() != null
									&& selGroup.getInstitutionCode().equals(institueCode))
							.collect(Collectors.toList());

					try {

//						ReportModel report = new ReportModel(); // csvRoot.report;
//
//						report.fileName = filePath;
//
//						report.subReports = new ArrayList<SubReportModel>();
//
//						System.out.println(
//								"Folio Users count - " + group.getFolioGroupName() + " " + folioRoot.users.size());
////
						

						for (PatronGroup group : selGroupList) {

							Root folioRoot = folioService.getUsersbyPatronGroup(group.getFolioGroupId());

							for (FolioUser folioUser : folioRoot.users) {

								// Boolean isCustomFieldNull = false;
//								Boolean isBarcodeNull = false;
//								Boolean isUsernameNull = false;
//								Boolean isExternalSystemId = false;

								if (folioUser.customFields == null
										|| folioUser.customFields.additionalPatronGroup_4 == null
										|| folioUser.customFields.additionalPatronGroup_4.isEmpty()) {

									// isCustomFieldNull = true;

									System.out.println(folioUser.id + ", " + folioUser.username + ", "
											+ folioUser.personal.firstName + " " + folioUser.personal.lastName + ", "
											+ folioUser.active);
								}

//								if (folioUser.barcode == null || folioUser.barcode.trim().isEmpty()) {
//									isBarcodeNull = true;
//								}
//
//								if (folioUser.username == null || folioUser.username.trim().isEmpty()) {
//									isUsernameNull = true;
//								}
//
//								if (folioUser.externalSystemId == null || folioUser.externalSystemId.trim().isEmpty()) {
//									isExternalSystemId = true;
//								}
//
//								if (isCustomFeildNull || isBarcodeNull || isUsernameNull || isExternalSystemId) {
//
//									System.out.println(folioUser.id + "," + folioUser.personal.firstName + " "
//											+ folioUser.personal.lastName + "," + folioUser.active + ","
//											+ isCustomFeildNull + "," + isBarcodeNull + "," + isUsernameNull + ","
//											+ isExternalSystemId);
//								}

							}

						}

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

			}

			System.out.println("End");

			// emailUserInactiveReport(csvUserList);

		} catch (Exception e1) {
			// TODO Auto-generated catch blocks
			e1.printStackTrace();
		}

	}

};