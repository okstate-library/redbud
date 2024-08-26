package com.okstatelibrary.redbud.operations;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.Constants;

/**
 * @author Damith
 * 
 *         Remove the comments in "Code segment 2" and run the script. It will
 *         update the user patron group as mentioned in the
 *         "additionalPatronGroup_4" field.
 */
public class UserPatronGroupReportProcess extends MainProcess {

	public void manipulate(GroupService groupService)
			throws JsonParseException, JsonMappingException, RestClientException, IOException, InterruptedException {

		System.out.println("User Patron Group Report Process - Start");

		System.out.println("externalSystemId" + " - " + "barcode" + " - " + "tusername" + " - " + "Name " + "  -  "
				+ "Custom field");

		List<PatronGroup> groups = groupService.getGroupList().stream().filter(c -> c.isFolioOnly() == 0)
				.collect(Collectors.toList());

		int userCount = 0;

		for (PatronGroup userGroup : groups) {

			System.out.println("\n");

			Root folioUserGroup = folioService.getUsersByPatronGroupId(userGroup.getFolioGroupId());

			System.out.println(userGroup.getFolioGroupId() + " : " + userGroup.getFolioGroupName() + " - "
					+ folioUserGroup.totalRecords);

			for (FolioUser folioUser : folioUserGroup.users) {

				String customField = "";

				if (folioUser.customFields != null && folioUser.customFields.additionalPatronGroup_4 != null
						&& !folioUser.customFields.additionalPatronGroup_4.trim().isEmpty()) {

					// System.out.println(folioUser.customFields.additionalPatronGroup_4);

					customField = folioUser.customFields.additionalPatronGroup_4;

					int customFieldLength = customField.split(";").length;

					if (customFieldLength == 1 && !customField.contains(Constants.expired_user_cutom_field)) {

						if (!customField.toLowerCase().contains(userGroup.getFolioGroupName().toLowerCase())) {

							// System.out.println("Custom groupID - " + customField);

							String ss = customField;

							try {

								String groupID = groups.stream().filter(c -> c.getFolioGroupName().contentEquals(ss))
										.findFirst().get().getFolioGroupId();

								// System.out.println("new groupID - " + folioUser.toString());

//								folioUser.patronGroup = groupID;
//								folioService.updateUser(folioUser);
//
//								Thread.sleep(2000);

							} catch (Exception e) {

								System.out.println("Error " + folioUser.toString());

							}

						}
					} else if (customFieldLength == 1 && customField.contains(Constants.expired_user_cutom_field)) {
						System.out.println("Expired user " + folioUser.toString());

//						folioUser.active = false;
//						folioService.updateUser(folioUser);
//
//						Thread.sleep(2000);
					} else if (customField.split(";").length > 1) {

						if (!customField.toLowerCase().contains(userGroup.getFolioGroupName().toLowerCase())) {
							System.out.println("Two Groups " + folioUser.toString());
						}

						customField = customField.toLowerCase().split(";")[0];

					}

//					else 

				}

			}

		}

		System.out.println("Done user count " + userCount);

	}

}
