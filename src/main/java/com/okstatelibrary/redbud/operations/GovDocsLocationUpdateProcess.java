package com.okstatelibrary.redbud.operations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.folio.entity.holding.HoldingsRecord;
import com.okstatelibrary.redbud.folio.entity.inventory.Item;
import com.okstatelibrary.redbud.folio.entity.inventory.PermanentLocation;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.DateUtil;

/**
 * @author Damith
 * 
 *         Replacing the Permanent location of the
 *
 */
public class GovDocsLocationUpdateProcess extends MainProcess {

	// Stores the Start time
	protected String startTime;

	public void manipulate(GroupService groupService)
			throws JsonParseException, JsonMappingException, RestClientException, IOException, InterruptedException {

		System.out.println("Start Time " + DateUtil.getTodayDateAndTime());

		Map<String, String> map = getMaps();

		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println("Available cores: " + cores);

		// Create a thread pool
		ExecutorService executorService = Executors.newFixedThreadPool(cores);

		for (Map.Entry<String, String> entry : map.entrySet()) {

			executorService.submit(() -> processItem(entry));
		}

		// Shutdown the thread pool and wait for all tasks to complete
		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.HOURS); // Adjust time if needed

		System.out.println("Done the  process" + DateUtil.getTodayDateAndTime());

	}

	private Object processItem(Entry<String, String> entry)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String locationName = entry.getKey();
			String locationId = entry.getValue();

			HoldingRoot holdingRoot = folioService.getHoldingsStorageByLocationId(entry.getValue());

			int counter = 0;

			int emptyRecords = 0;

			for (HoldingsRecord holding : holdingRoot.holdingsRecords) {

				counter++;

				ItemRoot itemRoot = folioService.getInventoryItemById(holding.id);

				for (Item item : itemRoot.items) {

					Item realItem = folioService.getItem(item.id);

					if (realItem.permanentLocation == null) {

						emptyRecords++;

						System.out.println(locationName + " - " + locationId + " - " + holdingRoot.totalRecords);

						System.out.println("--- " + counter + " - item id " + item.id + " Call Number "
								+ item.callNumber + " barcode " + item.barcode);

						realItem.permanentLocation = new PermanentLocation();

						realItem.permanentLocation.id = locationId;
						realItem.permanentLocation.name = locationName;
						folioService.updateItem(realItem);

						// Thread.sleep(1000);

					}

				}

				System.out.println("--- Holding.instanceId " + holding.id + " Total records - " + itemRoot.totalRecords
						+ " Empty records - " + emptyRecords);

				emptyRecords = 0;

			}

		} catch (Exception ex) {

		}

		return null;
	}

	private Map<String, String> getMaps() {

		Map<String, String> map = new HashMap<>();

		map.put("OKS-OSU Gov Docs Import", "d6280c83-26a3-474b-9d46-768fef94a929");
		map.put("OKS-OSU Main Damaged Tech", "e58f52ad-c24d-48b6-99d4-a91c8c8d9ae1");
		map.put("OKS-OSU LAB Intransit", "5e9a0916-28ec-4932-a09d-d797b545fe8f");
		map.put("OKS-OSU ETL Current Periodicals", "47bcae7b-47ad-4830-9f1d-678bf1cf0cf8");
		map.put("OKS-OSU VM Well-Being", "3de6d76e-4bd6-40e7-9059-64718e692a18");
		map.put("OKS-OSU Main Office - Mending", "a5f90eee-5a66-4a7e-ba4c-73ac21f88247");
		map.put("OKS-OSU Main Office - Systems", "f7623748-c7da-4d6b-bece-19f353fb94e2");
		map.put("OKS-OSU LAB Search", "36b2206f-d922-4225-b4eb-6d30ede82bc3");
		map.put("OKS-OSU Main Office - Copy Cataloging", "509ab3d8-dd9f-438d-97c2-592c12d62a31");
		map.put("OKS-OSU Main Ebooks", "7626f4de-b24e-4f0a-9897-bd6580cf4111");
		map.put("OKS-OSU Main Office - Acquisitions", "c1a7565f-4cb8-4461-b6aa-3cc97ed8b708");
		map.put("OKS-OSU VM Conference Room", "d26456d0-0641-41b0-b410-3695d8687f3f");
		map.put("OKS-OSU VM Circulation Desk", "ee2e159c-eac7-4885-977d-bca625ef98c8");
		map.put("OKS-OSU ETL Circulation Desk", "4b2dbb09-67dd-4887-8873-71728e3c674e");
		map.put("OKS-OSU Main Circulation Desk", "eb44aac5-ce60-4948-956b-69ebbd36b80f");
		map.put("OKS-OSU VM Office", "c0658685-827e-409a-a17c-14968933df71");
		map.put("OKS-OSU NCB Circulation Desk", "a86b7061-c5cd-4c94-9916-7e2bb8532404");
		map.put("OKS-OSU Annex SCUA Rare Books", "a9963fab-2f83-40f7-aff1-c560c7c0982d");
		map.put("OKS-OSU Annex Review", "289edaa8-546d-4e2d-b414-9bcae4006546");
		map.put("OKS-OSU Annex Conservation Lab", "46e46a52-69bc-41b4-ae74-a14ac553245f");
		map.put("OKS-OSU Architecture Current Periodicals", "4b1e36aa-5cac-47bd-a7cf-55f59903516e");
		map.put("OKS-OSU Main Office - Cataloging", "89bcdbbb-d9bd-4317-a512-b61beb2a5dd9");
		map.put("OKS-OSU VM Current Periodicals", "6e6b6729-c595-47cb-aaca-f9e52fae561a");
		map.put("OKS-OSU SCUA Bennett Collection", "1d879965-3074-4224-a0a9-68b09cb7e1be");

		map.put("OKS-OSU VM Equipment", "fe1d4a4a-5ef7-447e-8db5-5ec8f1df8a8e");
		map.put("OKS-OSU Gov Docs Legal Reference", "59123119-f149-422f-a231-d8de99f7f355");
		map.put("OKS-OSU Architecture Reserves", "e5a093a5-3c40-4393-82cc-7bff17986235");
		map.put("OKS-OSU Annex SCUA OSU Collection", "9ec12edf-0f5f-4926-8332-30f654102bf1");
		map.put("OKS-OSU Main Reading Room", "0791e251-6389-449a-a740-bc126a813a7f");
		map.put("OKS-OSU NCB Classroom Building", "c14f6834-c82f-44a2-9dd5-4a4a39aca065");
		map.put("OKS-OSU SCUA Reference", "21efdc69-f3bf-4e0b-891c-2f518b528257");
		map.put("OKS-OSU SCUA Action Adventure and Mystery Collection", "43e8c947-587b-4d85-a966-d97be2d40aba");
		map.put("OKS-OSU ETL Office", "e4781fc3-d1d8-41ca-9c48-c5466551a212");
		map.put("OKS-OSU Architecture Reference", "e4396641-8e77-47b0-98e5-dee46393a5fc");
		map.put("OKS-OSU Architecture Periodicals", "c0db55f8-8ed6-4a6a-af78-efdb1f196940");
		map.put("OKS-OSU Annex Gov Docs Legal Reference", "3b5a107c-bb93-4fdb-9fe0-f8f6c5d7a5b7");
		map.put("OKS-OSU Main Study Room or Support Item", "09186332-635c-4f18-82b9-21ce384a9752");
		map.put("OKS-OSU VM Reserves", "6e953323-317a-458f-9ad1-bea8d9393166");
		map.put("OKS-OSU ETL Reference", "446f9059-0f9b-43be-a4b0-b9349abd14de");
		map.put("OKS-OSU Annex SCUA Special Collections", "4b65ac91-31d9-4815-ab53-e038c4708c5a");
		map.put("OKS-OSU VM CD-ROM", "e813aedb-6d81-44fe-ae49-70fab01a9720");

		map.put("OKS-OSU Maps & Spatial Data Maps Accompanying", "a4da8aed-fca8-4a5c-8aa4-74951a9ce8fd");
		map.put("OKS-OSU Main Legal Reference", "de85c113-0226-431c-80f5-c1623f05c66b");
		map.put("OKS-OSU ETL Caldecott Collection", "52efa817-1b97-4d9f-9770-ad013fc1f4e5");
		map.put("OKS-OSU Main Current Periodicals", "c0e22bee-2175-4339-a53d-b81ee64ca0e5");
		map.put("OKS-OSU Main Relax and Read Collection", "04e11c81-5dad-4495-9c4f-500fdbe5960f");
		map.put("OKS-OSU ETL Newberry Collection", "dc06f228-65d7-406c-9abb-51ea2fe64059");
		map.put("OKS-OSU ETL Sequoyah Collection", "d56aabb3-f0dc-431a-9492-2152c2a2961d");
		map.put("OKS-OSU SCUA Thesis", "211e270d-60f4-4ad6-8d52-6da95bb0525e");
		map.put("OKS-OSU ETL Periodicals", "2c285640-e3bd-4208-b2bb-7b35c7a0746e");
		map.put("OKS-OSU Gov Docs Books Patents", "d2cfa16a-0681-441e-9291-dfe67b6f9d92");
		map.put("OKS-OSU Main Reserves", "9abceb10-19aa-4e7e-8c79-366a126e83a6");
		map.put("OKS-OSU Architecture Display", "3d630213-22df-4385-a8da-5e02be5268b7");
		map.put("OKS-OSU Gov Docs Oversize", "240b5123-8b7f-43ce-bc9e-4dc34f1f0ce1");
		map.put("OKS-OSU Gov Docs Reference", "bb4ba5bf-7443-4a07-a034-20348999267d");
		map.put("OKS-OSU ETL Reserves", "afaa601b-ba30-48da-9c3c-5d471e4eba9a");
		map.put("OKS-OSU Gov Docs Office", "89e25bd4-94b6-4bd2-8b14-a6a06dea091f");
		map.put("OKS-OSU Main Browsing Room", "222f991f-ad0f-43ac-98bd-342c16c39588");
		map.put("OKS-OSU Architecture Reading Room", "82b5e09f-4200-46cb-af1e-054116bd6ad6");
		map.put("OKS-OSU Gov Docs Books Patents Main", "8dbf28fd-cfc9-4d64-8119-05f20abb9388");
		map.put("OKS-OSU Main Equipment", "129dec9a-10f5-4ac2-9228-5d091e817116");

		map.put("OKS-OSU Annex Unaccessioned", "88c9267d-7169-4ccb-a45f-ce3a1d754894");
		map.put("OKS-OSU Gov Docs Reference", "8ef9df9d-0b62-40a8-ac14-4d4e0fc33e17");
		map.put("OKS-OSU SCUA Book Rare", "96b181d9-1036-448e-a9f0-2556e7800341");
		map.put("OKS-OSU Main Action Adventure & Mystery Collection", "3efd9021-5a63-479e-9502-c499d5b62326");
		map.put("OKS-OSU ETL WP", "09f2d19d-d4ce-4504-8543-ce5c32acb488");
		map.put("OKS-OSU SCUA Angie Debo Collection", "76fafe84-f080-421b-9187-c0efaafa8697");
		map.put("OKS-OSU Annex Gov Docs Oversized", "6c19e331-4b2b-4790-8372-eb887cabf483");
		map.put("OKS-OSU Main Creative Studios", "efa727cb-339c-41bd-8d86-920065dfec37");
		map.put("OKS-OSU VM Periodicals", "26f39cc8-a269-4a36-8012-26d2ba8f7078");
		map.put("OKS-OSU ETL Languages", "e7babc53-1c24-4573-916e-feefac3c272a");
		map.put("OKS-OSU ETL Stacks", "60a0fc7b-af62-425b-991d-4aa96fbb24d8");
		map.put("OKS-OSU Maps & Spatial Data Books", "4442d03f-67ad-48ca-bd31-5215ce334410");
		map.put("OKS-OSU ETL Textbooks", "a6c173c3-6af8-4259-b965-434d58123c0f");

		map.put("OKS-OSU VM Media", "c8c051ba-883a-46c9-97b1-82fe9a4e88d5");
		map.put("OKS Migration", "576a2539-cd0f-4b0d-8a73-28981bcb9622");
		map.put("OKS-OSU Annex Unbound Periodical", "acde1d6c-fc2a-4f78-84a3-069b08698a02");
		map.put("OKS-OSU Gov Docs Media", "b440b227-581d-4301-8eb8-b1cf54d01cd0");
		map.put("OKS-OSU Main Creative Studios Media", "78e595ab-290d-4b67-b720-b88d71175f9a");
		map.put("OKS-OSU Main Office - ILS", "f6626630-6766-4c09-bda8-bbf0281d39ef");
		map.put("OKS-OSU ETL Media", "d308342c-47dc-47b3-a907-100bfaff2227");
		map.put("OKS-OSU Annex Microprint", "efb9910b-8a19-4324-97d8-c05ae62f753c");

		map.put("OKS-OSU Gov Docs Periodical", "30576e1f-4fdb-4d3d-b195-b8dbd075f5b0");
		map.put("OKS-OSU Annex Architecture Collection", "c915029e-30b5-489d-a8ab-ee3234401dbe");

		map.put("OKS-OSU ETL Professional Books", "fcb87952-0344-4e15-bc73-b44d8da38861");
		map.put("OKS-OSU ETL Juvenile Fiction", "03de4f93-9d20-4c5b-890f-2cf784a72d09");
		map.put("OKS-OSU ETL Historical", "4ca9957d-4975-4ba3-9354-e457bc581bf1");
		map.put("OKS-OSU Annex ETL Collection", "f9f9c6ab-d549-4dd3-b464-6f6b34b15a07");
		map.put("OKS-OSU ETL Picture Books", "3a477524-3584-427b-80d4-07ecaa2d3305");
		map.put("OKS-OSU Architecture Stacks", "6964f2d3-e1f9-4582-9b6e-ccdf0abb3445");
		map.put("OKS-OSU ETL Juvenile", "1a8bf01a-d98b-4a06-a227-59b2c620fcda");
		map.put("OKS-OSU VM Stacks", "7510f504-5702-4d8a-b92c-63ba5c9d2971");
		map.put("OKS-OSU Maps & Spatial Data Maps", "211478f6-b9b7-4935-92e4-723ebc31bda6");
		map.put("OKS-OSU SCUA Special Collection", "4afefd31-0149-44c1-a58d-c075091b66a1");

		map.put("OKS-OSU Annex Gov Docs Microfiche", "c6f76a75-1ce6-4e98-b3ec-2e501e302b66");
		map.put("OKS-OSU Annex Main Collection", "85507468-47be-46b0-8c29-c7c0688818cc");
		map.put("OKS-OSU LAB SCUA", "31573130-18ce-4c66-b449-25b0a7144488");
		map.put("OKS-OSU Annex Microfilm", "026f776e-748b-4daf-9fa2-8e0f5a1cbd56");
		map.put("OKS-OSU SCUA OSU Collection", "7765f7da-a35f-4e5c-a32c-a78f12fa03f8");
		map.put("OKS-OSU Annex Microfiche", "fa228eda-445f-4ea5-8545-6735fd38f59d");

		map.put("OKS-OSU Gov Docs Microform", "cb738b69-e3a1-42cb-8b37-83b12f47cb97");
		map.put("OKS-OSU Annex Gov Docs Book Main", "24473012-c99d-4e6a-ae50-dc1b9fc9c3a9");
		map.put("OKS-OSU Gov Docs Books", "5786bece-3dae-4d56-85a3-3a5402bf1921");

		map.put("OKS-OSU LAB", "4e4331ec-d652-4591-af7f-4c0dc6ddf485");
		map.put("OKS-OSU Main Stacks", "912064a8-6296-4d35-8c91-48722c5ddc59");

		return map;
	}

}
