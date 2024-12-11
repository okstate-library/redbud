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

	protected int numberClosedLoans;

	private CirculationLogService circulationLogService;

	public CirculationLogProcess(CirculationLogService circulationLogService) {
		this.circulationLogService = circulationLogService;
	}

	public void manipulate() throws JsonParseException, JsonMappingException, RestClientException, IOException {

		String timePersiod = "Time period - " + DateUtil.getYesterdayDate(true) + "-"
				+ DateUtil.getYesterdayDate(false);

		printScreen(timePersiod, Constants.ErrorLevel.INFO);

		int cloasedLoans = processClosedLoans();

		int openLoans = processOpenLoans();

		printScreen("Total circulationLogsByItems- " + cloasedLoans, Constants.ErrorLevel.INFO);

		sendEmaill("Circulation log daily data migration " + timePersiod, "Loan record count - " + numberClosedLoans
				+ " unique records - " + cloasedLoans + " open loans-" + openLoans);
	}

	private int processClosedLoans() throws JsonParseException, JsonMappingException, RestClientException, IOException {
		// Get all the records from yesterday.
		ArrayList<Loan> loans = folioService.getClosedLoansCountByDate(DateUtil.getYesterdayDate(true),
				DateUtil.getYesterdayDate(false));

		numberClosedLoans = loans.size();

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

		return circulationLogsByItems.size();
	}

	private int processOpenLoans() throws JsonParseException, JsonMappingException, RestClientException, IOException {

		ArrayList<Loan> openLoans = folioService.getOpenedLoans();

		printScreen("openLoans.size() " + openLoans.size(), Constants.ErrorLevel.ERROR);

		int number = 0;

		for (Loan loan : openLoans) {

			number++;

			printScreen(" Loan ID " + loan.id, Constants.ErrorLevel.ERROR);

			CirculationLog selectedCirculationLog = circulationLogService.getCirculationLogByItemId(loan.itemId);

			if (selectedCirculationLog != null) {

				selectedCirculationLog.setOpen(true);
				selectedCirculationLog.setLoanDate(DateUtil.getShortDate2(loan.getLoanDate()));

				this.circulationLogService.saveCirculationLog(selectedCirculationLog);

				printScreen(" Update open loan in existing record" + selectedCirculationLog.getId(),
						Constants.ErrorLevel.ERROR);

			} else {

				Loan specificLoan = folioService.getLoansByLoanId(loan.id);

				if (specificLoan.item != null) {

					printScreen(" Circulation entry" + loan.itemId, Constants.ErrorLevel.ERROR);

					selectedCirculationLog = new CirculationLog();

					selectedCirculationLog.setLocation(loan.itemEffectiveLocationIdAtCheckOut);
					selectedCirculationLog.setItemId(loan.itemId);
					selectedCirculationLog.setBarcode(specificLoan.item.barcode);
					selectedCirculationLog.setCallNumber(specificLoan.item.callNumber);

					selectedCirculationLog.setMaterialType(specificLoan.item.materialType.name);
					selectedCirculationLog.setTitle(specificLoan.item.title);
					selectedCirculationLog.setLoanDate(DateUtil.getShortDate2(specificLoan.getLoanDate()));

					selectedCirculationLog.setOpen(true);

					this.circulationLogService.saveCirculationLog(selectedCirculationLog);

					printScreen(" Update item in Circulation Log id " + selectedCirculationLog.getId(),
							Constants.ErrorLevel.ERROR);

				} else {
					printScreen(" Loan not found " + loan.id, Constants.ErrorLevel.ERROR);
				}

			}

			if (number % 1000 == 0) {
				System.out.println(" number  " + number);
			}

		}

		System.out.println(" number  " + number);
		
		return openLoans.size();
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
