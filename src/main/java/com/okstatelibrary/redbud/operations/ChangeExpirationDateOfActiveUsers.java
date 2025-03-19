package com.okstatelibrary.redbud.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

public class ChangeExpirationDateOfActiveUsers extends MainProcess {

	protected String startTime;

	private ArrayList<String> messageList;

	public void manipulate(GroupService groupService) {

		startTime = DateUtil.getTodayDateAndTime();

		messageList = new ArrayList<>();

		messageList.add("Change Expiration Date Of Active Users" + "<br/>");

		messageList.add("Start Time " + DateUtil.getTodayDateAndTime() + "<br/>");

		try {
			List<PatronGroup> groupList = groupService.getGroupList();

			for (CsvFileModel csvFileModel : Constants.csvFileModels) {

				for (String institueCode : csvFileModel.institueCodes) {

					List<PatronGroup> selGroupList = groupList.stream()
							.filter(selGroup -> selGroup.getInstitutionCode() != null
									&& selGroup.getInstitutionCode().equals(institueCode) && selGroup.isFolioOnly() == 0)
							.collect(Collectors.toList());

					try {

						for (PatronGroup group : selGroupList) {

							printScreen(group.getFolioGroupId() + "   " + group.getFolioGroupName(),
									Constants.ErrorLevel.INFO);

							messageList.add(group.getFolioGroupId() + "   " + group.getFolioGroupName());

							Root folioRoot = folioService.getUsersbyPatronGroup(group.getFolioGroupId());

							printScreen(
									"Folio Users count - " + group.getFolioGroupName() + " " + folioRoot.users.size(),
									Constants.ErrorLevel.INFO);

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

									messageList.add("Error modify only Folio User " + folioUser);
								}
							}

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

	private void createAndSendEmail() {

		StringBuilder strBuilder = new StringBuilder();

		for (String message : messageList) {

			strBuilder.append(message + "<br/>");
		}

		strBuilder.append("<br/> End time: " + DateUtil.getTodayDateAndTime());

		this.sendEmaill("End of change Expiration Date Of Active Users ", strBuilder.toString());

	}

}
