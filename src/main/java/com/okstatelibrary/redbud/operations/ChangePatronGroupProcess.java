package com.okstatelibrary.redbud.operations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ChangePatronGroupProcess extends MainProcess {

	protected String startTime;

	public void changeUserPatronGroup() throws IOException {

		try {

			ArrayList<String> idList = getValues("/Users/library-mac/Desktop/osu_projs/folio/extra/osu-staff.csv");

			// folioService = new FolioService();

			for (String id : idList) {

				System.out.println("ID" + id);

//				FolioUser exist_user = folioService.getUserByExternalSystemId(id);
//
//				if (exist_user != null) {
//					exist_user.patronGroup = "c88e6e42-9544-4e5e-ae94-a50c07b9dfbf";
//					exist_user.metadata = getMetadata(exist_user.metadata);
//
//					folioService.updateUser(exist_user);
//
//				} else {
//					System.out.println("Null user ids " + id);
//				}

			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}

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
