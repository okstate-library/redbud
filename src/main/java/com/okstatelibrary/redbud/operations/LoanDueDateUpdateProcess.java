package com.okstatelibrary.redbud.operations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.folio.entity.loan.Loan;
import com.okstatelibrary.redbud.folio.entity.request.Request;
import com.okstatelibrary.redbud.util.DateUtil;

public class LoanDueDateUpdateProcess extends MainProcess {

	protected String startTime;

	// FolioService folioService = new FolioService();

	public void manipulate(String userGroup) throws JsonParseException, JsonMappingException, RestClientException,
			IOException, ParseException, InterruptedException {

		System.out.println("Started");

		System.out.println("userid,email, first and last name , loans count, modified loans");

		folioService.getUsersByPatronGroupIdForLoans(userGroup);

		ArrayList<Request> requests = folioService.getOpenRequests();

		String str = "31be75f0-f880-592e-8966-7ba59727b51c,9a689972-08ac-50b0-abff-aa200cc09ad3,f8b5f8b7-69ca-565b-a739-c25b27c37136,75dfad23-82e7-5f5f-b16d-56cae63b9e1f,0194023a-0025-5f51-baec-e91281d1c4ec,1b5159b2-2860-5326-b5d6-45341dfb3925";

		List<String> items = Arrays.asList(str.split("\\s*,\\s*"));

		for (String userId : items) {

			// for (FolioUser user : userRoot.users) {

			// System.out.println("User name- " + user.username);

			// String userId = "c4de33e9-ce30-5d21-b37b-13c67ddd5693";

			FolioUser user = folioService.getUsersById(userId);

			ArrayList<Loan> loans = folioService.getLoansByUser(user.id); // user.id);

			// "e5483ea8-6ad8-5602-8365-7abf255a0825");

//
//		
//		for (Request request : requests) {
//			System.out.println("Requuest itemid " + request.itemId);
//		}

			ArrayList<Loan> sortedLoans = new ArrayList<Loan>();

			for (Loan loan : loans) {

//			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//			String json = ow.writeValueAsString(loan);
//			System.out.println(json);
//
				// System.out.println("Loaan due date - " + loan.getDueDate());

				// loan.setDueDate("2024-11-21T00:00:00.000+00:00");
				// folioService.updateLoan(loan);

//				 dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.TAIWAN);
//				 System.out.println("Locale TAIWAN = " + dateFormat.format(loan.dueDate));

				// System.out.println("Loaan due date - " + loan.getDueDate());
				String dateTime = DateUtil.getShortDate(loan.getDueDate());

				// LocalDateTime dateTime = LocalDateTime.parse(loan.getDueDate());

				// System.out.println("Loaan due date - " + dateTime);

				if (!loan.item.materialType.name.equals("equipment")
						&& loan.loanPolicyId.equals("7abd2943-08a0-4ca1-8cc8-6a1f116e8763")//
						&& !loan.itemEffectiveLocationIdAtCheckOut.equals("7abd2943-08a0-4ca1-8cc8-6a1f116e8763")
						&& dateTime.equals("2024-02-29")) {

					// System.out.println("Loaan due date - " + loan.getDueDate());

					boolean isIn = false;

					for (Request request : requests) {

						if (request.itemId.equals(loan.itemId)) {
							isIn = true;
						}
					}

					if (!isIn) {

						// System.out.println("Loaan due date - " + dateTime);

						sortedLoans.add(loan);

//					
////					loan.setLoanDate("2019-08-29T17:33:21+00:00");
////					loan.setDueDate("2024-11-21T00:00:00.000+00:00");// java.sql.Date.valueOf("2014-03-14");
////																	// //"2024-03-05T05:59:00.000+00:00";//
//																	// DateUtil.getLongDate("2014/03/14"); // Fri Mar 15
//					loan.metadata = getMetadata(loan.metadata); // 00:59:00 CDT 2024
//
//					folioService.updateLoan(loan);

					}

				}

			}

			if (sortedLoans.size() > 0) {

				String userEmail = user.personal.email;

				// System.out.println("User email " + userEmail);

				user.personal.email = "lib-dls@okstate.edu";

				folioService.updateUser(user);

				Thread.sleep(30000);

				for (Loan loan : sortedLoans) {

					// System.out.println("loan " + loan.id + " - due Date - " + loan.getDueDate());

					loan.actionComment = "faculty-retired auto-renewal spring 2024";
					loan.setDueDate("2024-09-02T04:59:59.000+00:00");

					loan.loanPolicyId = "7abd2943-08a0-4ca1-8cc8-6a1f116e8763";
					folioService.updateLoan(loan);
				}

				Thread.sleep(30000);

				user.personal.email = userEmail;
				folioService.updateUser(user);

				System.out.println(

						user.id + "," + user.personal.email + "," + user.personal.firstName + " "
								+ user.personal.lastName + "," + loans.size() + "," + +sortedLoans.size());
			}

		}

		System.out.println("End");

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
