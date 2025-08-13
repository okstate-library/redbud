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
 * @author Damith User fields update while reading a different csv format file.
 *         This CSV file is not the normal CSV uploaded to the main feed.
 */
public class CheckUserStatusProcess extends MainProcess {

	// Stores the Start time
	protected String startTime;

	//
	public void manipulate(GroupService groupService) {

		startTime = DateUtil.getTodayDateAndTime();

		System.out.println("***************CheckUserStatusProcess*****************");

		try {

			String filePath = "/Users/library-mac/Desktop/osu_projs/extra/";

			File folder = new File(filePath);

			File[] listOfFiles = folder.listFiles();

			List<String> csvUsers = new ArrayList<>();

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

							userGroupUserCount++;

							if (exterNalSystemId == null || exterNalSystemId.isEmpty()) {

							} else {
								csvUsers.add(exterNalSystemId);
							}

							if (barCode == null || barCode.isEmpty()) {

							} else {
								csvUsers.add(barCode);
							}

						}

					} catch (Exception e) {

						e.printStackTrace();
					}

				}
			}

//			for (String user : csvUsers) {
//				printScreen(user);
//			}

			int totalUserCount = 0;

			List<PatronGroup> groupList = groupService.getGroupList();

			for (CsvFileModel csvFileModel : Constants.csvFileModels) {

				for (String institueCode : csvFileModel.institueCodes) {

					List<PatronGroup> selGroupList = groupList.stream()
							.filter(selGroup -> selGroup.getInstitutionCode() != null
									&& selGroup.getInstitutionCode().equals(institueCode)
									&& selGroup.isFolioOnly() == 0)
							.collect(Collectors.toList());

					try {

						for (PatronGroup group : selGroupList) {

							Root folioRoot = folioService.getUsersbyPatronGroup(group.getFolioGroupId());

							printScreen("Folio Users count - " + group.getFolioGroupName() + " - "
									+ folioRoot.users.size());

							int userCount = 0;

							for (FolioUser folioUser : folioRoot.users) {

								// FolioUser folioUser =
								// folioService.getUsersByExternalSystemId(exterNalSystemId);

								if (!csvUsers.contains(folioUser.externalSystemId)) {
									printScreen(folioUser.barcode + "," + folioUser.externalSystemId + ","
											+ folioUser.username);

									userCount++;
								}

							}

							totalUserCount += userCount;

							printScreen("Numer of  of user active in FOLIO and not in CSV file - " + userCount);

							printScreen("");

						}

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

			}

			printScreen("Total numer of  of user active in FOLIO and not in CSV file - " + totalUserCount);

			printScreen("Done");

		} catch (Exception e1) {

			e1.printStackTrace();
		}

	}

}
