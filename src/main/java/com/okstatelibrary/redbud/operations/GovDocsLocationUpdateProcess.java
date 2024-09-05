package com.okstatelibrary.redbud.operations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.folio.entity.holding.HoldingsRecord;
import com.okstatelibrary.redbud.folio.entity.inventory.Item;
import com.okstatelibrary.redbud.folio.entity.inventory.PermanentLocation;
import com.okstatelibrary.redbud.service.*;

/**
 * @author Damith Replacing the Permanent location of the
 *
 */
public class GovDocsLocationUpdateProcess extends MainProcess {

	// Stores the Start time
	protected String startTime;

	public void manipulate(GroupService groupService)
			throws JsonParseException, JsonMappingException, RestClientException, IOException, InterruptedException {

		Map<String, String> map = new HashMap<>();

		map.put("OKS-OSU Gov Docs Legal Reference", "59123119-f149-422f-a231-d8de99f7f355");
		map.put("OKS-OSU Gov Docs Oversize", "240b5123-8b7f-43ce-bc9e-4dc34f1f0ce1");
		map.put("OKS-OSU Gov Docs Reference,Main", "bb4ba5bf-7443-4a07-a034-20348999267d");
		map.put("OKS-OSU Gov Docs Reference", "8ef9df9d-0b62-40a8-ac14-4d4e0fc33e17");
		map.put("OKS-OSU Gov Docs Periodical", "30576e1f-4fdb-4d3d-b195-b8dbd075f5b0");
		map.put("OKS-OSU Gov Docs Books", "5786bece-3dae-4d56-85a3-3a5402bf1921");

		for (Map.Entry<String, String> entry : map.entrySet()) {

			String locationName = entry.getKey();
			String locationId = entry.getValue();

			System.out.println("Location name: " + locationName + ", location Id: " + locationId);

			HoldingRoot holdingRoot = folioService.getHoldingsStorageByLocationId(entry.getValue());

			System.out.println("Total Holdings Records " + holdingRoot.totalRecords);

			int counter = 0;

			int emptyRecords = 0;
			int notEmptyRecords = 0;

			for (HoldingsRecord holding : holdingRoot.holdingsRecords) {

				counter++;

				System.out.println(" holding.instanceId " + holding.id);

				ItemRoot itemRoot = folioService.getInventoryItemById(holding.id);

				System.out.println("Total item records " + itemRoot.totalRecords);

				for (Item item : itemRoot.items) {

					Item realItem = folioService.getItem(item.id);

					if (realItem.permanentLocation == null) {

						emptyRecords++;

						System.out.println(counter + " -             permanent Location Null item id " + item.id
								+ " Call Number " + item.callNumber + " barcode " + item.barcode);

						realItem.permanentLocation = new PermanentLocation();

						realItem.permanentLocation.id = locationId;
						realItem.permanentLocation.name = locationName;
						folioService.updateItem(realItem);

						//Thread.sleep(1000);

					} else {
						notEmptyRecords++;
//						System.out.println(counter + " -               permanent Location Not null item id " + item.id
//								+ " Call Number " + item.callNumber);
					}
				}

				System.out.println("emptyRecords-" + emptyRecords + "notEmptyRecords-" + notEmptyRecords);

				System.out.println("");

				emptyRecords = 0;
				notEmptyRecords = 0;

			}

		}

		System.out.println("Done the  process");

	}

}
