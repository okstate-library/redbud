
package com.okstatelibrary.redbud.operations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.okstatelibrary.redbud.entity.CirculationLog;
import com.okstatelibrary.redbud.folio.entity.holding.HoldingsRecord;
import com.okstatelibrary.redbud.folio.entity.instance.Instance;
import com.okstatelibrary.redbud.folio.entity.inventory.Item;
import com.okstatelibrary.redbud.service.CirculationLogService;
import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.DateUtil;
import com.okstatelibrary.redbud.util.StringHelper;

public class AlmaLoanCountUpdateProcess extends MainProcess {

	protected String startTime;

	String empty_item_file = "empty_item_file.txt";

	String empty_instance_file = "empty_instance_file.txt";

	String empty_holdings_file = "empty_holdings_file.txt";

	public void manipulate(CirculationLogService circulationLogService) throws IOException {

		try {

			// circulationLogService = new CirculationLogService();

			ArrayList<String[]> recordList = getValues(
					AppSystemProperties.Almaloancsvfilepath);

			// Barcode,Number of loans,Last loan
			// column name of the csv file.

			for (String[] record : recordList) {

				String recordLine = Arrays.toString(record);
				
				try {
				
					System.out.println(recordLine);

					String barcode = record[0];

					int numLoans = Integer.parseInt(record[1]);

					String dateColumn = record[2];

					System.out.println("Recodr -  " + barcode + "-" + numLoans + " - " + dateColumn);

					ArrayList<Item> items = folioService.getItembyBarcode(barcode);

					System.out.println("items count " + items.size());

					if (items != null && items.size() > 0) {

						for (Item item : items) {

							System.out.println("item.itemId - " + item.holdingsRecordId);

							if (item != null && !StringHelper.isStringNullOrEmpty(item.id)) {

								// Find the item from the database and update the relevant filed.

								CirculationLog circulationLog = circulationLogService
										.getCirculationLogByItemId(item.id);

								System.out.println("item.holdingsRecordId - " + item.holdingsRecordId);

								HoldingsRecord holdingRecord = folioService
										.getHoldingRecordByHoldingId(item.holdingsRecordId);

								if (holdingRecord != null) {

									ArrayList<Instance> instances = folioService
											.getInstanceByInstanceId(holdingRecord.instanceId);

									if (instances != null && instances.size() > 0) {

										if (circulationLog != null) {

											System.out.println("Found a records to udpate");

											circulationLog.setAuthor(item.getContributorNames());

											if (instances != null && instances.size() > 0) {

												Instance specficIstance = instances.get(0);

												if (specficIstance.publication.size() > 0) {
													circulationLog.setPublishYear(
															specficIstance.publication.get(0).dateOfPublication);
												}

												if (specficIstance.editions.size() > 0) {
													circulationLog
															.setEdition(String.join(" ", specficIstance.editions));
												}

											}

											circulationLog.setAlmaNumLoans(numLoans);

											if (!dateColumn.contentEquals("0")) {
												Date loanDate = DateUtil.getShortDate2(record[2]);

												circulationLog.setLoanDate(loanDate);
											} else {
												System.out.println("Found date null column");

											}

											System.out.println("Update num loans in existing record");

										} else {

											System.out.println("Found not found to update.");

											circulationLog = new CirculationLog();

											circulationLog.setLocation(item.effectiveLocation.id);
											circulationLog.setItemId(item.id);
											circulationLog.setBarcode(item.barcode);
											circulationLog.setCallNumber(item.callNumber);

											circulationLog.setMaterialType(item.materialType.name);
											circulationLog.setTitle(item.title);

											circulationLog.setAuthor(item.getContributorNames());

											if (instances != null && instances.size() > 0) {

												Instance specficIstance = instances.get(0);

												if (specficIstance.publication.size() > 0) {
													circulationLog.setPublishYear(
															specficIstance.publication.get(0).dateOfPublication);
												}

												if (specficIstance.editions.size() > 0) {
													circulationLog
															.setEdition(String.join(" ", specficIstance.editions));
												}

											}

											circulationLog.setAlmaNumLoans(numLoans);

											if (!dateColumn.contentEquals("0")) {
												Date loanDate = DateUtil.getShortDate2(record[2]);

												circulationLog.setLoanDate(loanDate);
											} else {
												System.out.println("Found date null column");

											}

										}

										circulationLogService.saveCirculationLog(circulationLog);

										System.out.println("Updated Circulation Log");

									} else {
										System.out.println("Instance Record is null");

										writeToFile(empty_instance_file, recordLine);

									}

								} else {
									System.out.println("Holding Record is null");

									writeToFile(empty_holdings_file, recordLine);

								}

							}

						}
					} else {
						System.out.println("Item not found ");

						writeToFile(empty_item_file, recordLine);
					}

					System.out.println("------------------------------------------------------");

				} catch (Exception e) {

					// Handle the exception (e.g., log it)
					System.out.println("Error " + recordLine + " at : " + e.getMessage());
					// Optionally, continue to the next iteration
				}

			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}

	}

	// Method to write content to a file

	public static void writeToFile(String filePath, String content) {

		System.out.println("write file");

		try (FileWriter fileWriter = new FileWriter(filePath, true); // 'true' for append mode
				PrintWriter printWriter = new PrintWriter(fileWriter)) {

			// Write the content to the file
			printWriter.println(content);

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	// Get the users reading the csv file.
	public ArrayList<String[]> getValues(String filePath) throws IOException {

		ArrayList<String[]> idList = new ArrayList<String[]>();

		String line = "";

		// parsing a CSV file into BufferedReader class constructor
		@SuppressWarnings("resource")

		BufferedReader br = new BufferedReader(new FileReader(filePath));

		while ((line = br.readLine()) != null) // returns a Boolean value
		{
			idList.add(line.split(","));
		}

		return idList;
	}

}
