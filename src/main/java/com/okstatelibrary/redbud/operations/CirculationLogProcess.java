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
import com.okstatelibrary.redbud.service.LocationService;

public class CirculationLogProcess extends MainProcess {

	protected final String tab = " ---- ";

	protected String startTime;

	private CirculationLogService circulationLogService;

	StringBuilder lines = new StringBuilder();

	public CirculationLogProcess(CirculationLogService circulationLogService) {
		this.circulationLogService = circulationLogService;
	}

	public void manipulate(LocationService locationService, boolean isDailyProcess, String locationId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		String timePeriod = "Time period - " + DateUtil.getYesterdayDate(true) + "  -   "
				+ DateUtil.getYesterdayDate(false) + " Is Daily process - " + isDailyProcess;

		printScreen(timePeriod, Constants.ErrorLevel.INFO);

		// Get all the locations
		List<Location> locations = locationService.getLocationList();

		String endDateTime = DateUtil.getYesterdayDate(false);
		String startDateTime = DateUtil.getYesterdayDate(true);

		lines.append("Start time:" + DateUtil.getTodayDateAndTime() + "<br/>");

		ArrayList<Loan> openLoans = folioService.getOpenedLoans();

		lines.append(timePeriod).append("<br/>");

		lines.append("Number of open loans").append(tab).append(openLoans.size()).append("<br/><br/>");

		lines.append("Location").append(tab).append("Close Loan record count").append(tab).append("unique records")
				.append(tab).append(" open loans").append("<br/>");

		if (locationId.equals("0")) {

			// Run the loan execution for all locations.

			printScreen(" Location is 0  ----------", Constants.ErrorLevel.INFO);

			for (Location location : locations) {

				executeLoans(endDateTime, startDateTime, openLoans, location, isDailyProcess);
			}

		} else {

			// Selecting a specific location send from the UI.

			printScreen(" Location is selcted " + locationId , Constants.ErrorLevel.INFO);

			Location location = locations.stream().filter(l -> l.getLocation_id().equals(locationId)).findFirst().get();

			executeLoans(endDateTime, startDateTime, openLoans, location, isDailyProcess);
		}

		lines.append("End time:" + DateUtil.getTodayDateAndTime() + "<br/>");

		sendEmaill("Circulation log daily data migration " + timePeriod, lines.toString());

	}

	private void executeLoans(String endDateTime, String startDateTime, ArrayList<Loan> openLoans, Location location,
			boolean isDailyProcess) throws JsonParseException, JsonMappingException, IOException {

		printScreen(" Location " + location.getLocation_name(), Constants.ErrorLevel.INFO);

		// Execute Closed Loans

		ArrayList<Loan> closedLocationLoans = folioService.getClosedLoansByLocation(location.getLocation_id(),
				startDateTime, endDateTime, isDailyProcess);

		int cloasedLoansCount = 0;

		if (closedLocationLoans != null && closedLocationLoans.size() > 0) {
			cloasedLoansCount = processClosedLoans(closedLocationLoans, isDailyProcess);
		}

		// Execute Open loans

		ArrayList<Loan> openLocationLoans = (ArrayList<Loan>) openLoans.stream()
				.filter(l -> l.itemEffectiveLocationIdAtCheckOut.equals(location.getLocation_id()))
				.collect(Collectors.toList());

		int openLoansCount = processOpenLoans(openLocationLoans);

		printScreen("Closed loan size " + cloasedLoansCount + " open loans size - " + openLoansCount, Constants.ErrorLevel.INFO);
		
		lines.append(location.getLocation_name()).append(tab).append(closedLocationLoans.size()).append(tab)
				.append(cloasedLoansCount).append(tab).append(openLoansCount).append("<br/>");
	}

	private int processClosedLoans(ArrayList<Loan> locationLoans, boolean isDailyProcess)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		// Adding the loans for a new object list

		ArrayList<CirculationLog> newLogs = new ArrayList<CirculationLog>();

		for (Loan loan : locationLoans) {

			CirculationLog circulationLog = new CirculationLog();

			circulationLog.setOpen(false);
			circulationLog.setLoanId(loan.id);
			circulationLog.setRenewalCount(loan.renewalCount);
			circulationLog.setItemId(loan.itemId);
			circulationLog.setLoanDate(DateUtil.getShortDate2(loan.getLoanDate()));
			circulationLog.setLocation(loan.itemEffectiveLocationIdAtCheckOut);

			newLogs.add(circulationLog);
		}

		// Adding number of same item loans in to an Array
		// group by item id.

		Map<String, List<CirculationLog>> circulationLogsByItems = new HashMap<>();

		for (CirculationLog p : newLogs) {

			if (!circulationLogsByItems.containsKey(p.getItemId())) {
				circulationLogsByItems.put(p.getItemId(), new ArrayList<>());
			}

			circulationLogsByItems.get(p.getItemId()).add(p);
		}

		printScreen("Size of Grouped loans " + circulationLogsByItems.size(), Constants.ErrorLevel.INFO);

		// Reading item by item to insert or modify the data in data base.

		for (Map.Entry<String, List<CirculationLog>> entry : circulationLogsByItems.entrySet()) {

			// Order by desc the loan date
			List<CirculationLog> result = entry.getValue().stream()
					.sorted((o1, o2) -> o2.getLoanDate().compareTo(o1.getLoanDate())).collect(Collectors.toList());

			CirculationLog selectedCirculationLog = circulationLogService.getCirculationLogByItemId(entry.getKey());

			int numberOfLoans = result.size();
			int renewalCounts = result.stream().mapToInt(CirculationLog::getRenewalCount).sum();
			Date lastLoanDate = DateUtil.getShortDate2(result.get(0).getLoanDate());

//			printScreen("itemid " + entry.getKey() + " number of closed loan size " + result.size() + " renewalCounts "
//					+ renewalCounts + " last loan date " + lastLoanDate, Constants.ErrorLevel.INFO);

			// If the circulation log exists in db, take it and update. Otherwise create a
			// new circulation log and saves to db.

			if (selectedCirculationLog != null) {

				selectedCirculationLog.setLoanDate(lastLoanDate);

				if (isDailyProcess) {
					selectedCirculationLog.setRenewalCount(selectedCirculationLog.getRenewalCount() + renewalCounts);
					selectedCirculationLog.setNumLoans(selectedCirculationLog.getNumLoans() + numberOfLoans);
				} else {
					selectedCirculationLog.setNumLoans(numberOfLoans);
					selectedCirculationLog.setRenewalCount(renewalCounts);
				}

				// printScreen("Already in DB" + entry.getKey(), Constants.ErrorLevel.INFO);

				this.circulationLogService.saveCirculationLog(selectedCirculationLog);

			} else {

				selectedCirculationLog = entry.getValue().get(0);

				Loan specificLoan = folioService.getLoansByLoanId(selectedCirculationLog.getLoanId());

				if (specificLoan.item != null) {

					selectedCirculationLog.setOpen(false);
					selectedCirculationLog.setTitle(specificLoan.item.title);
					selectedCirculationLog.setMaterialType(specificLoan.item.materialType.name);
					selectedCirculationLog.setBarcode(specificLoan.item.barcode);
					selectedCirculationLog.setCallNumber(specificLoan.item.callNumber);

					selectedCirculationLog.setLoanDate(lastLoanDate);
					selectedCirculationLog.setRenewalCount(renewalCounts);
					selectedCirculationLog.setNumLoans(numberOfLoans);

					this.circulationLogService.saveCirculationLog(selectedCirculationLog);

				} else {
					printScreen(" close loan not found " + selectedCirculationLog.getLoanId(),
							Constants.ErrorLevel.ERROR);

					lines.append(" close loan not found " + selectedCirculationLog.getLoanId()).append("<br/>");
				}

			}

			// printScreen("------------------------------", Constants.ErrorLevel.INFO);

		}

		return circulationLogsByItems.size();
	}

	private int processOpenLoans(ArrayList<Loan> openLoans)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		int number = 0;

		for (Loan loan : openLoans) {

			number++;

			// printScreen(" Loan ID " + loan.id, Constants.ErrorLevel.ERROR);

			CirculationLog selectedCirculationLog = circulationLogService.getCirculationLogByItemId(loan.itemId);

			if (selectedCirculationLog != null) {

				selectedCirculationLog.setOpen(true);
				selectedCirculationLog.setLoanDate(DateUtil.getShortDate2(loan.getLoanDate()));

//				printScreen("itemid " + selectedCirculationLog.getItemId() + " renewalCounts " + loan.renewalCount
//						+ " last loan date " + loan.getLoanDate(), Constants.ErrorLevel.INFO);

				this.circulationLogService.saveCirculationLog(selectedCirculationLog);

//				printScreen(" Update open loan in existing record" + selectedCirculationLog.getId(),
//						Constants.ErrorLevel.INFO);

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

//					printScreen(" Added item in Circulation Log id " + selectedCirculationLog.getId(),
//							Constants.ErrorLevel.INFO);

				} else {
					lines.append(" open loan not found -- " + loan.id).append("<br/>");

					printScreen(" open loan not found " + loan.id, Constants.ErrorLevel.ERROR);
				}

			}

//			if (number % 1000 == 0) {
//				System.out.println(" number  " + number);
//			}

			// printScreen("------------------------------", Constants.ErrorLevel.INFO);

		}

		//System.out.println(" number  " + number);

		return openLoans.size();
	}

}