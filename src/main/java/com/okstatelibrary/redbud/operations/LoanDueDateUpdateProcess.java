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

/**
 * @author Damith
 * 
 *         // Tasks run the code // 1. Get some sample user details from Megan
 *         relevant to the User group // 2. Use of the user id try to get the
 *         loan records from FOLIO // 3. Get the relevant due date and and put
 *         in the relevant format to get the records // 4. Run the code with the
 *         relevant user id and compare with the FOLIO records // 5. Get the new
 *         due date in correct format , modify the records for test. // 6. Run
 *         the entire code and get the report
 * 
 */
public class LoanDueDateUpdateProcess extends MainProcess {

	protected String startTime;

	public void manipulate(String userGroup) throws JsonParseException, JsonMappingException, RestClientException,
			IOException, ParseException, InterruptedException {

		System.out.println("Started - LoanDueDateUpdateProcess");

		Root userRoot = folioService.getUsersByPatronGroupIdForLoans(userGroup);

		ArrayList<Request> requests = folioService.getOpenRequests();

		System.out.println("Users Size : " + userRoot.users.size());

		System.out.println("Request Size : " + requests.size());

		System.out.println("userid,email, first and last name , loans count, modified loans");

// 		Code  segment 1 - User details

		// String str = "0194023a-0025-5f51-baec-e91281d1c4ec";

		// List<String> items = Arrays.asList(str.split("\\s*,\\s*"));

		// for (String userId : items) {

		for (FolioUser user : userRoot.users) {

			// FolioUser user = folioService.getUsersById(userId);

			ArrayList<Loan> loans = folioService.getLoansByUser(user.id);

			ArrayList<Loan> sortedLoans = new ArrayList<Loan>();

			for (Loan loan : loans) {
//
				// System.out.println("Loaan due date - " + loan.getDueDate());

				// System.out.println("Loaan due date - " + loan.getDueDate());
				String dateTime = DateUtil.getShortDate(loan.getDueDate());

				// LocalDateTime dateTime = LocalDateTime.parse(loan.getDueDate());

//				System.out.println("Loan due date - " + loan.getDueDate() + "  " + user.username + "  "
//						+ loan.loanPolicyId + "  " + loan.itemEffectiveLocationIdAtCheckOut + loan.item.title);

				if (!loan.item.materialType.name.equals("equipment")
						&& loan.loanPolicyId.equals("7abd2943-08a0-4ca1-8cc8-6a1f116e8763")//
						&& !loan.itemEffectiveLocationIdAtCheckOut.equals("7abd2943-08a0-4ca1-8cc8-6a1f116e8763")
						&& dateTime.equals("2025-03-01")) {

					//System.out.println("Loan due date - " + dateTime + "  " + user.username);

					boolean isIn = false;

					for (Request request : requests) {

						if (request.itemId.equals(loan.itemId)) {
							isIn = true;
						}
					}

					if (!isIn) {

						// System.out.println("Loaan due date - " + dateTime);

						sortedLoans.add(loan);

					}

				} else {
					System.out.println("Loan due date - " + dateTime + " " + user.username);
				}

			}

//  Sending the email with replacing to a lib-dls and after sending add the replace to old email.

			if (sortedLoans.size() > 0) {

				String userEmail = user.personal.email;

//  Task 4. To remove the comments before running.

//				user.personal.email = "lib-dls@okstate.edu";
//
//				folioService.updateUser(user);
//
//				Thread.sleep(30000);
//
//				for (Loan loan : sortedLoans) {
//
//					// System.out.println("loan " + loan.id + " - due Date - " + loan.getDueDate());
//
//					loan.actionComment = "faculty auto-renewal spring 2024";
//					loan.setDueDate("2025-03-01T04:59:59.000+00:00");
//
//					loan.loanPolicyId = "7abd2943-08a0-4ca1-8cc8-6a1f116e8763";
//					folioService.updateLoan(loan);
//				}
//
//				Thread.sleep(30000);
//
//				user.personal.email = userEmail;
//				folioService.updateUser(user);
//
//				System.out.println(user.id + "," + user.personal.email + "," + user.personal.firstName + " "
//						+ user.personal.lastName + "," + loans.size() + "," + +sortedLoans.size());

			}

		}

		System.out.println("End");

	}

}
