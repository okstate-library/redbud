package com.okstatelibrary.redbud.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

/**
 * @author Damith
 * User fields update while reading a different csv format file.
 * This CSV file is not the normal CSV uploaded to the main feed.
 */
public class UserFieldsUpdateProcess extends MainProcess {

	// Stores the Start time
	protected String startTime;

	//
	public void manipulate(GroupService groupService) {

		startTime = DateUtil.getTodayDateAndTime();

		try {

			String filePath = "/Users/library-mac/Desktop/osu_projs/extra/";

			File folder = new File(filePath);

			File[] listOfFiles = folder.listFiles();

			List<PatronGroup> groupList = groupService.getGroupList();

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
							String barCode = line.split(",")[0];

							FolioUser folioUser = folioService.getUsersByBarcode(barCode);

							// System.out.println("********************************");

							if (folioUser != null) {

								PatronGroup selectedGroup = groupList.stream()
										.filter(selGroup -> selGroup.getInstitutionCode() != null
												&& selGroup.getFolioGroupId().equals(folioUser.patronGroup))
										.findFirst().get();

								// System.out.println("Folio Group Name" + selectedGroup.getFolioGroupName());

								String birthDate = line.split(",")[2];

								String middleName = line.split(",")[3];

								String email = line.split(",")[6];

								String phoneNumber = "";

								if (line.split(",").length == 8) {
									phoneNumber = line.split(",")[7];
								}

//								System.out.println("Folio User " + folioUser.personal.dateOfBirth + " : "
//										+ folioUser.personal.middleName + " : " + folioUser.personal.email + " : "
//										+ folioUser.personal.phone);
//
//								System.out.println("CSV User " + birthDate + " : " + middleName + " : " + email + " : "
//										+ phoneNumber);

								if (birthDate != null && !birthDate.trim().isEmpty()) {

									String date = DateUtil.getDate(birthDate).toString();

									folioUser.personal.dateOfBirth = date;
								}

								folioUser.personal.middleName = middleName;
								folioUser.personal.email = email;
								folioUser.personal.phone = phoneNumber;

								if (folioUser.customFields == null) {
									CustomFields newCustommFields = new CustomFields();
									newCustommFields.additionalPatronGroup_4 = selectedGroup.getFolioGroupName();
									folioUser.customFields = newCustommFields;
								} else {
									folioUser.customFields.additionalPatronGroup_4 = selectedGroup.getFolioGroupName();
								}

								boolean isUpdated = folioService.updateUser(folioUser);

								if (!isUpdated) {
									System.out.println("Not Updated : " + barCode);
								}

//								else {
//									System.out.println("Updated : " + barCode);
//								}

							} else {

								System.out.println("Null folio Users :" + barCode);
							}

						}

					} catch (Exception e) {

						e.printStackTrace();
					}

				}
			}

		} catch (Exception e1) {
			// LOG.error(e1.getMessage());
		}

	}

	/**
	 * Read the csv file and update the new bar code of the selected uses
	 */
	public void manipulateBarcode() {

		startTime = DateUtil.getTodayDateAndTime();

		try {

			String filePath = "/Users/library-mac/Desktop/osu_projs/folio/extra_userbarcode";

			File folder = new File(filePath);

			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {

				File csvFile = listOfFiles[i];

				System.out.println("csf file name " + csvFile.getName());

				if (csvFile.isFile() && csvFile.getName().contains(".csv")) {

					String line = "";

					try {

						// parsing a CSV file into BufferedReader class constructor
						@SuppressWarnings("resource")
						BufferedReader br = new BufferedReader(new FileReader(csvFile));

						while ((line = br.readLine()) != null) // returns a Boolean value
						{

							String barCode = line.split(",")[0];

							FolioUser folioUser = folioService.getUsersByBarcode(barCode);

							// System.out.println("********************************");

							if (folioUser != null) {

								System.out.println(
										barCode + " " + folioUser.barcode + " " + folioUser.personal.firstName);

								String newBarCode = line.split(",")[2];

								folioUser.barcode = newBarCode;

								boolean isUpdated = folioService.updateUser(folioUser);

								if (!isUpdated) {
									System.out.println("Not Updated : " + barCode);
								}

							} else {

								System.out.println("Null folio Users :" + barCode);
							}

						}

					} catch (Exception e) {

						e.printStackTrace();
					}

				}
			}

		} catch (Exception e1) {
			// LOG.error(e1.getMessage());
		}

	}

}
