package com.okstatelibrary.redbud.operations;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.folio.entity.loan.Loan;
import com.okstatelibrary.redbud.folio.entity.request.Request;
import com.okstatelibrary.redbud.util.DateUtil;

/**
 * @author Damith
 * 
 *         // Tasks run the code // 1. Get some sample user details from
 *         Supervisor relevant to the User group // 2. Use of the user id try to
 *         get the loan records from FOLIO // 3. Get the relevant due date and
 *         and put in the relevant format to get the records // 4. Run the code
 *         with the relevant user id and compare with the FOLIO records // 5.
 *         Get the new due date in correct format , modify the records for test.
 *         // 6. Run the entire code and get the report
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

		// String userId = "38aee6d1-15a0-41ae-be50-d4286634770e"; //
		// "e5483ea8-6ad8-5602-8365-7abf255a0825"; // Known user
		// id

		// FolioUser user = folioService.getUsersById(userId);

		for (FolioUser user : userRoot.users) {

			// System.out.println(" Username " + user.username);

			ArrayList<Loan> loans = folioService.getLoansByUser(user.id);

			// System.out.println("Loaan Count- " + loans.size());

			ArrayList<Loan> sortedLoans = new ArrayList<Loan>();

			for (Loan loan : loans) {

				if (loan.loanPolicyId.equals("7abd2943-08a0-4ca1-8cc8-6a1f116e8763")//
						&& !loan.itemEffectiveLocationIdAtCheckOut.equals("7abd2943-08a0-4ca1-8cc8-6a1f116e8763")) {

					boolean isIn = false;

					for (Request request : requests) {

						if (request.itemId.equals(loan.itemId)) {
							isIn = true;
						}
					}

					if (!isIn) {
						sortedLoans.add(loan);
					}

				}

			}

			if (sortedLoans.size() > 0) {

				// Task 3. Get the list of users having loan detais.

				System.out.println(user.id + "," + user.personal.email + "," + user.personal.firstName + " "
						+ user.personal.lastName + "," + loans.size() + "," + +sortedLoans.size());

				// Task 4. To remove the comments before running.
				// Sending the email with replacing to a lib-dls and after sending add the
				// replace to old email.

				String userEmail = user.personal.email;

				user.personal.email = "lib-dls@okstate.edu";

				folioService.updateUser(user);

				// Thread.sleep(3000);

				for (Loan loan : sortedLoans) {

					//System.out.println(loan.id);// + " - " + loan.getDueDate());

					loan.actionComment = "faculty auto-renewal spring 2026";
					loan.setDueDate("2027-03-01T04:59:59.000+00:00");

					if (!folioService.updateLoan(loan)) {
						System.out.println("Error with updating loan");
					}

					// Loan updateedLaon = folioService.getLoansByLoanId(loan.id);

					// System.out.println("new duedate" + updateedLaon.getDueDate());
				}

				Thread.sleep(3000);

				user.personal.email = userEmail;

				folioService.updateUser(user);

			}

		}

		System.out.println("End");

	}

}
