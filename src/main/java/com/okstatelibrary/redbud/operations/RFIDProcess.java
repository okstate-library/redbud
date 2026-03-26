
package com.okstatelibrary.redbud.operations;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.okstatelibrary.redbud.folio.entity.inventory.Item;
import com.okstatelibrary.redbud.folio.entity.inventory.Item4Rfid;
import com.okstatelibrary.redbud.service.CampusService;
import com.okstatelibrary.redbud.service.CirculationLogService;
import com.okstatelibrary.redbud.service.InstitutionService;
import com.okstatelibrary.redbud.service.LibraryService;
import com.okstatelibrary.redbud.service.LocationService;
import com.okstatelibrary.redbud.service.ServicePointService;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class RFIDProcess extends MainProcess {

	protected String startTime;

	public void manipulate(InstitutionService institutionService, CampusService campusService,
			LibraryService libraryService, LocationService locationService, ServicePointService servicePointService,
			CirculationLogService circulationLogService) throws IOException {

		try {			
			String folderPath = "/Users/library-mac/Desktop/osu_projs/folio/Jenny Request/csv";

			try {
				
				Files.list(Paths.get(folderPath)).filter(p -> p.toString().endsWith(".csv"))
						.forEach(RFIDProcess::processCsvFile);

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Done");

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}

	}

	public static void processCsvFile(Path csvFile) {

		System.out.println("Processing: " + csvFile.getFileName());

		List<String[]> updatedRows = new ArrayList<>();

		try (CSVReader reader = new CSVReader(new FileReader(csvFile.toFile()))) {

			String[] header = reader.readNext();

			updatedRows.add(header);

			String[] row;

			int rowCount = 0;

			while ((row = reader.readNext()) != null) {

				rowCount++;

				// Call API using the first column or any needed value
				String barCode = row[0];

				// String apiValue = callApi(columnValue);

				ArrayList<Item> items = folioService.getItembyBarcode(barCode);

				if (rowCount % 500 == 0) {
					System.out.println(" rowCount  :" + rowCount + "columnValue : " + barCode);
				}

				if (items != null && items.size() > 0) {

					for (Item item : items) {

						//System.out.println("procccedded barcode " + item.barcode);

						ModelMapper mapper = new ModelMapper();

						mapper.getConfiguration().setFieldMatchingEnabled(true)
								.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PUBLIC);

						Item4Rfid rfidItem = mapper.map(item, Item4Rfid.class);

						if (rfidItem.statisticalCodeIds != null && !rfidItem.statisticalCodeIds.isEmpty()) {
							System.out.println("Already statisticalCodeIds " + rfidItem.barcode);
							
							break;
						}

						ArrayList<String> statisticalCodeIds = new ArrayList<>();

						statisticalCodeIds.add("22c8e7bc-27bf-485a-a265-99d2c763ddd7");

						rfidItem.statisticalCodeIds = statisticalCodeIds;

						//printJson(rfidItem);

						//System.out.println("Item  " + item.barcode);
						
						if (!folioService.updateItem4Rfid(rfidItem)) {
							System.out.println("Failed to update RFID item: " + rfidItem.barcode);
						} else {
							//System.out.println("Successfully updated RFID item: " + rfidItem.barcode);
						}

					}

//					if (rowCount == 100) {
//						break;
//					}
				} else {
					System.out.println("no" + barCode);

					updatedRows.add(row);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Sort by the new column (last index)
		// updatedRows.subList(1, updatedRows.size()).sort(Comparator.comparing(a ->
		// a[a.length - 1]));

		// Write new file
		Path newFile = Paths.get(csvFile.getParent().toString(), "NoItemsFound_" + csvFile.getFileName());

		try (CSVWriter writer = new CSVWriter(new FileWriter(newFile.toFile()))) {
			writer.writeAll(updatedRows);
			System.out.println("Saved updated CSV: " + newFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}