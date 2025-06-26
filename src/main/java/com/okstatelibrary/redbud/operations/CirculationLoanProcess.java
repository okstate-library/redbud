package com.okstatelibrary.redbud.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.enums.LoanAction;
import com.okstatelibrary.redbud.folio.entity.loan.Loan;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;
import com.okstatelibrary.redbud.service.CirculationLoanService;
import com.okstatelibrary.redbud.service.CirculationLogService;
import com.okstatelibrary.redbud.service.LocationService;

public class CirculationLoanProcess extends MainProcess {

	protected final String tab = " ---- ";

	protected String startTime;

	private CirculationLogService circulationLogService;

	private CirculationLoanService circulationLoanService;

	String[] openLoanActions = { "checkedout", "renewed", "checkedOutThroughOverride" };

	List<String> openLoanActionsList = Arrays.asList(openLoanActions);

	StringBuilder lines = new StringBuilder();

	public CirculationLoanProcess(CirculationLogService circulationLogService,
			CirculationLoanService circulationLoanService) {

		this.circulationLogService = circulationLogService;
		this.circulationLoanService = circulationLoanService;
	}

	public void manipulate(LocationService locationService, boolean isDailyProcessd)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		String timePeriod = "Time period - " + DateUtil.getYesterdayDate(true) + "  -   "
				+ DateUtil.getYesterdayDate(false) + " Is Daily process - ";// + isDailyProcess;

		printScreen(timePeriod, Constants.ErrorLevel.INFO);

		// Get all the locations
		List<Location> locations = locationService.getLocationList();

		String endDateTime = DateUtil.getYesterdayDate(false);
		String startDateTime = DateUtil.getYesterdayDate(true);

		lines.append("Start time:" + DateUtil.getTodayDateAndTime() + "<br/>");

		lines.append(timePeriod).append("<br/>");

		lines.append("Location").append(tab).append("Close Loan record count").append(tab).append("unique records")
				.append(tab).append(" open loans").append("<br/>");

//		if (libraryId.equals("0")) {
//
//			// Run the loan execution for all locations.
//
//			printScreen(" Location is 0  ----------", Constants.ErrorLevel.INFO);
//
//			for (Location location : locations) {
//
//				executeLoans(endDateTime, startDateTime, openLoans, location, isDailyProcess);
//			}
//
//		} else {
//
//			// Selecting a specific locations from related to library id and do the
//			// operations.
//
		//locations = locationService.getLocationListByLibraryId("665c49b9-ad54-483e-bc2c-d549e5b6e865");

		for (Location location : locations) {

			printScreen(" Location  " + location.getLocation_name(), Constants.ErrorLevel.INFO);

//			ArrayList<Loan> openLocationLoans = (ArrayList<Loan>) openLoans.stream()
//					.filter(l -> l.itemEffectiveLocationIdAtCheckOut.equals(location.getLocation_id()))
//					.collect(Collectors.toList());

			executeOpenLoans(location.getLocation_id());

			//executeLoans(endDateTime, startDateTime, location.getLocation_id(), false);

		}

//		}

		lines.append("End time:" + DateUtil.getTodayDateAndTime() + "<br/>");

		printScreen("End time:" + DateUtil.getTodayDateAndTime() + "<br/>", Constants.ErrorLevel.INFO);

		// sendEmaill("Circulation log daily data migration " + timePeriod,
		// lines.toString());

	}

	private void executeLoans(String endDateTime, String startDateTime, String location, boolean isDailyProcess)
			throws JsonParseException, JsonMappingException, IOException {

		ArrayList<Loan> closedLocationLoans = folioService.getClosedLoansByLocation(location, startDateTime,
				endDateTime, isDailyProcess);

		if (closedLocationLoans != null && closedLocationLoans.size() > 0) {

			printScreen(" Close loan count  ----------" + closedLocationLoans.size(), Constants.ErrorLevel.INFO);

			for (Loan loan : closedLocationLoans) {

				CirculationLog selectedCirculationLog = circulationLogService.getCirculationLogByItemId(loan.itemId);

				if (selectedCirculationLog != null) {

					CirculationLoan circulationLoan = this.circulationLoanService.getCirculationLoanByRowId(loan.id);

					if (circulationLoan == null) {

						printScreen(" loan.action -- " + loan.action, Constants.ErrorLevel.ERROR);

						saveCirculationLoan(loan, selectedCirculationLog, false);

						printScreen(" Loan ID Closed Loan For new record  " + loan.id + " -- " + loan.action,
								Constants.ErrorLevel.ERROR);
					}

					// Already exists record in the table.

				} else {

					selectedCirculationLog = saveCirculationLog(loan, false);

					if (selectedCirculationLog != null) {

						if (openLoanActionsList.contains(loan.action)) {

							saveCirculationLoan(loan, selectedCirculationLog, true);

							printScreen(" Loan ID Open Loan For new record  " + loan.id + " -- " + loan.action,
									Constants.ErrorLevel.ERROR);

						}

					}
				}
			}
		}
	}

	private int executeOpenLoans(String locationId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		ArrayList<Loan> openLoans = folioService.getOpenedLoans(locationId);

		printScreen(" Open loan count  ----------" + openLoans.size(), Constants.ErrorLevel.INFO);

		for (Loan loan : openLoans) {

			CirculationLog selectedCirculationLog = circulationLogService.getCirculationLogByItemId(loan.itemId);

			if (selectedCirculationLog != null) {

				if (openLoanActionsList.contains(loan.action)) {

					CirculationLoan circulationLoan = this.circulationLoanService.getCirculationLoanByRowId(loan.id);

					if (circulationLoan == null) {

						saveCirculationLoan(loan, selectedCirculationLog, true);

						printScreen(" Loan ID Open Loan For new record  " + loan.id + " -- " + loan.action,
								Constants.ErrorLevel.ERROR);
					}

				}

			} else {

				printScreen("selectedCirculationLog is null  ", Constants.ErrorLevel.ERROR);

				selectedCirculationLog = saveCirculationLog(loan, true);

				if (selectedCirculationLog != null) {

					if (openLoanActionsList.contains(loan.action)) {

						saveCirculationLoan(loan, selectedCirculationLog, true);

						printScreen(" Loan ID Open Loan For new record  " + loan.id + " -- " + loan.action,
								Constants.ErrorLevel.ERROR);

					}

				}
			}

		}

		return openLoans.size();
	}

	private CirculationLog saveCirculationLog(Loan loan, boolean isOpen)
			throws JsonParseException, JsonMappingException, IOException {

		try {
			Loan specificLoan = folioService.getLoansByLoanId(loan.id);

			CirculationLog selectedCirculationLog = new CirculationLog();

			if (specificLoan.item != null) {

				selectedCirculationLog.setOpen(isOpen);
				selectedCirculationLog.setTitle(specificLoan.item.title);
				selectedCirculationLog.setMaterialType(specificLoan.item.materialType.name);
				selectedCirculationLog.setBarcode(specificLoan.item.barcode);
				selectedCirculationLog.setCallNumber(specificLoan.item.callNumber);

				selectedCirculationLog.setLoanDate(DateUtil.getShortDate2(specificLoan.getLoanDate()));
				selectedCirculationLog.setRenewalCount(0);
				selectedCirculationLog.setNumLoans(0);

				return this.circulationLogService.saveCirculationLog(selectedCirculationLog);

			} else {
				printScreen(" close loan not found " + selectedCirculationLog.getLoanId(), Constants.ErrorLevel.ERROR);

				lines.append(" close loan not found " + selectedCirculationLog.getLoanId()).append("<br/>");

				return null;
			}
		} catch (

		Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}

	}

	private void saveCirculationLoan(Loan loan, CirculationLog selectedCirculationLog, boolean isOpen) {
		try {
			CirculationLoan circulationLoan = new CirculationLoan();

			circulationLoan.setAction(LoanAction.toCode(loan.action));
			circulationLoan.setCirculationLogId(selectedCirculationLog.getId());
			circulationLoan.setDate(DateUtil.getShortDate2(loan.getLoanDate()));
			circulationLoan.setRenewalCount(loan.renewalCount);
			circulationLoan.setOpen(isOpen);
			circulationLoan.setRowId(loan.id);

			this.circulationLoanService.saveCirculationLoan(circulationLoan);

		} catch (

		Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
		}
	}

}