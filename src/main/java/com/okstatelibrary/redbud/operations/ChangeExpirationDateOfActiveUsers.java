package com.okstatelibrary.redbud.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.enums.UserStatusCheck;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

public class ChangeExpirationDateOfActiveUsers extends MainProcess {

	protected String startTime;

	public void manipulate(GroupService groupService) {

		startTime = DateUtil.getTodayDateAndTime();

		try {

			printScreen("New expiry date" + DateUtil.getActiveUserExpireDate(), Constants.ErrorLevel.INFO);

			List<PatronGroup> groupList = groupService.getGroupList();

			printScreen("External System Id , barcode Username , Expiration Date ");

			for (CsvFileModel csvFileModel : Constants.csvFileModels) {

				for (String institueCode : csvFileModel.institueCodes) {

					// && selGroup.getFolioGroupId().equals("02609d66-4b2a-47f6-988a-cf7b5b2932c7")

					List<PatronGroup> selGroupList = groupList.stream()
							.filter(selGroup -> selGroup.getInstitutionCode() != null
									&& selGroup.getInstitutionCode().equals(institueCode)
									&& selGroup.isFolioOnly() == 0)
							// && selGroup.getFolioGroupId().equals("c88e6e42-9544-4e5e-ae94-a50c07b9dfbf"))
							.collect(Collectors.toList());

					try {

						for (PatronGroup group : selGroupList) {

							Root folioRoot = folioService.getUsersbyPatronGroup(group.getFolioGroupId(),
									UserStatusCheck.TRUE);

//							printScreen(
//									"Folio Users count - " + group.getFolioGroupName() + " " + folioRoot.users.size());

							int userCount = 0;

							for (FolioUser folioUser : folioRoot.users) {

								String expiryDate = folioUser.expirationDate != null
										? folioUser.expirationDate.split("T")[0]
										: "date null";

								printScreen(folioUser.externalSystemId + ", " + folioUser.barcode + ", "
										+ folioUser.username + ", " + expiryDate);

//								folioUser.expirationDate = DateUtil.getActiveUserExpireDate(); // get9MonthsAfterTodayDate();
//
//								folioUser.metadata = getMetadata(folioUser.metadata);
//
//								if (!folioService.updateUser(folioUser)) {
//
//									printScreen("Error modify only Folio User " + folioUser, Constants.ErrorLevel.INFO);
//
//								} else {
//									userCount++;
//								}

								// break;
							}

//							printScreen("Number of users updated of expiry date " + userCount,
//									Constants.ErrorLevel.INFO);

							// break;
						}

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// break;
				}

			}

			printScreen("##Done");

		} catch (Exception e1) {
			// TODO Auto-generated catch blocks
			e1.printStackTrace();
		}

	}

}
