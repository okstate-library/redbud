package com.okstatelibrary.redbud.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.folio.entity.loan.Loan;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;
import com.okstatelibrary.redbud.service.CirculationLogService;

public class CirculationLogProcess extends MainProcess {

	protected String startTime;

	private CirculationLogService circulationLogService;

	public CirculationLogProcess(CirculationLogService circulationLogService) {
		this.circulationLogService = circulationLogService;
	}

	public void manipulate() throws JsonParseException, JsonMappingException, RestClientException, IOException {

		String timePersiod = "Time period - " + DateUtil.getYesterdayDate(true) + "-"
				+ DateUtil.getYesterdayDate(false);

		printScreen(timePersiod, Constants.ErrorLevel.INFO);

		// Get all the records from yesterday.
		ArrayList<Loan> loans = folioService.getClosedLoansCountByDate(DateUtil.getYesterdayDate(true),
				DateUtil.getYesterdayDate(false));

		printScreen("Loan size " + loans.size(), Constants.ErrorLevel.INFO);

		// Adding the loans for a new object list
		ArrayList<CirculationLog> newLogs = new ArrayList<CirculationLog>();

		for (Loan loan : loans) {

			CirculationLog circulationLog = new CirculationLog();

			circulationLog.setLoanId(loan.id);
			circulationLog.setItemId(loan.itemId);
			circulationLog.setLoanDate(DateUtil.getShortDate2(loan.getLoanDate()));
			circulationLog.setLocation(loan.itemEffectiveLocationIdAtCheckOut);

			newLogs.add(circulationLog);
		}

		// Adding number of same item loaned in to an Array for counting
		Map<String, List<CirculationLog>> circulationLogsByItems = new HashMap<>();

		for (CirculationLog p : newLogs) {

			if (!circulationLogsByItems.containsKey(p.getItemId())) {
				circulationLogsByItems.put(p.getItemId(), new ArrayList<>());
			}
			circulationLogsByItems.get(p.getItemId()).add(p);
		}

		for (Map.Entry<String, List<CirculationLog>> entry : circulationLogsByItems.entrySet()) {

			// Order by desc the loan date
			List<CirculationLog> result = entry.getValue().stream()
					.sorted((o1, o2) -> o2.getLoanDate().compareTo(o1.getLoanDate())).collect(Collectors.toList());

			printScreen("itemid " + entry.getKey() + " size " + result.size(), Constants.ErrorLevel.INFO);

			CirculationLog selectedCirculationLog = circulationLogService.getCirculationLogByItemId(entry.getKey());

			int numberOfLoans = result.size();
			Date lastLoanDate = DateUtil.getShortDate2(result.get(0).getLoanDate());

			// If the circulation log exists in db, take it and update. Otherwise create a
			// new circulation log and saves to db.

			if (selectedCirculationLog != null) {
				selectedCirculationLog.setLoanDate(lastLoanDate);

				selectedCirculationLog.setNumLoans(selectedCirculationLog.getNumLoans() + numberOfLoans);

				printScreen("Already in DB" + entry.getKey(), Constants.ErrorLevel.INFO);

				this.circulationLogService.saveCirculationLog(selectedCirculationLog);

			} else {

				selectedCirculationLog = entry.getValue().get(0);

				Loan specificLoan = folioService.getLoansByLoanId(selectedCirculationLog.getLoanId());

				if (specificLoan.item != null) {

					selectedCirculationLog.setTitle(specificLoan.item.title);
					selectedCirculationLog.setMaterialType(specificLoan.item.materialType.name);
					selectedCirculationLog.setBarcode(specificLoan.item.barcode);
					selectedCirculationLog.setCallNumber(specificLoan.item.callNumber);

					selectedCirculationLog.setLoanDate(lastLoanDate);
					selectedCirculationLog.setNumLoans(numberOfLoans);

					this.circulationLogService.saveCirculationLog(selectedCirculationLog);
				} else {
					printScreen(" Loan not found " + selectedCirculationLog.getLoanId(), Constants.ErrorLevel.ERROR);
				}

			}

		}

		printScreen("Total circulationLogsByItems- " + circulationLogsByItems.size(), Constants.ErrorLevel.INFO);

		sendEmaill("Circulation log daily data migration " + timePersiod,
				"Loan record count - " + loans.size() + " unique records - " + circulationLogsByItems.size());
	}

	public void initialManipulate() throws JsonParseException, JsonMappingException, RestClientException, IOException {

		ArrayList<Loan> loans = folioService.getClosedLoans(true, "");

		printScreen("Yesterday time - " + DateUtil.getYesterdayDate(true), Constants.ErrorLevel.INFO);

		ArrayList<CirculationLog> newLogs = new ArrayList<CirculationLog>();

		System.out.println("Loan size " + loans.size());

		// System.out.println("Yesterday date " + DateUtil.getYesterdayDate());

		for (Loan loan : loans) {

			int compare = DateUtil.compareDates(DateUtil.getYesterdayDate(),
					DateUtil.getShortDate2(loan.getLoanDate()));

			if (compare >= 0) {

				CirculationLog circulationLog = new CirculationLog();

				circulationLog.setLoanId(loan.id);
				circulationLog.setItemId(loan.itemId);
				circulationLog.setLoanDate(DateUtil.getShortDate2(loan.getLoanDate()));
				circulationLog.setLocation(loan.itemEffectiveLocationIdAtCheckOut);

				newLogs.add(circulationLog);
			}
		}

		System.out.println("newLogs.size " + newLogs.size());

		// Adding number of same item loaned in to an Array for counting

		Map<String, List<CirculationLog>> circulationLogsByItems = new HashMap<>();

		for (CirculationLog p : newLogs) {

			if (!circulationLogsByItems.containsKey(p.getItemId())) {
				circulationLogsByItems.put(p.getItemId(), new ArrayList<>());
			}

			circulationLogsByItems.get(p.getItemId()).add(p);
		}

		for (Map.Entry<String, List<CirculationLog>> entry : circulationLogsByItems.entrySet()) {

			// Order by desc the loan date
			List<CirculationLog> result = entry.getValue().stream()
					.sorted((o1, o2) -> o2.getLoanDate().compareTo(o1.getLoanDate())).collect(Collectors.toList());

			// If the circulation log exists in db, take it and update. Otherwise create a
			// new circulation log and saves to db.

			CirculationLog selectedCirculationLog = entry.getValue().get(0);

			Loan specificLoan = folioService.getLoansByLoanId(selectedCirculationLog.getLoanId());

			if (specificLoan.item != null) {

				selectedCirculationLog.setTitle(specificLoan.item.title);
				selectedCirculationLog.setMaterialType(specificLoan.item.materialType.name);
				selectedCirculationLog.setBarcode(specificLoan.item.barcode);
				selectedCirculationLog.setCallNumber(specificLoan.item.callNumber);

				selectedCirculationLog.setLoanDate(DateUtil.getShortDate2(result.get(0).getLoanDate()));
				selectedCirculationLog.setNumLoans(result.size());

				this.circulationLogService.saveCirculationLog(selectedCirculationLog);
			} else {
				printScreen(" Loan not found " + selectedCirculationLog.getLoanId(), Constants.ErrorLevel.ERROR);
			}

		}

		printScreen("Total circulationLogsByItems- " + circulationLogsByItems.size(), Constants.ErrorLevel.INFO);

		sendEmaill("Initial Circulation Log Data Extraction ",
				" Loan record count - " + loans.size() + "Unique record Count - " + circulationLogsByItems.size());

	}

}
