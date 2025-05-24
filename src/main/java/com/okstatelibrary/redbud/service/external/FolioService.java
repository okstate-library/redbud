package com.okstatelibrary.redbud.service.external;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.folio.entity.holding.HoldingsRecord;
import com.okstatelibrary.redbud.folio.entity.holding.HoldingsRecord2;
import com.okstatelibrary.redbud.folio.entity.holding.HoldingsStatement;
import com.okstatelibrary.redbud.folio.entity.instance.Identifier;
import com.okstatelibrary.redbud.folio.entity.instance.Instance;
import com.okstatelibrary.redbud.folio.entity.inventory.Inventory;
import com.okstatelibrary.redbud.folio.entity.inventory.Item;
import com.okstatelibrary.redbud.folio.entity.loan.Loan;
import com.okstatelibrary.redbud.folio.entity.loan.LoanRoot;
import com.okstatelibrary.redbud.folio.entity.manualblock.ManualBlock;
import com.okstatelibrary.redbud.folio.entity.request.Request;
import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.Constants;

@Service
public class FolioService extends FolioServiceToken {

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(FolioService.class);

	private RestTemplate restTemplate = new RestTemplate();

	ObjectMapper mapper = new ObjectMapper();

	private HttpHeaders getHttpHeaders() {

		HttpHeaders headers = new HttpHeaders();

		headers.add("x-okapi-tenant", AppSystemProperties.FolioTenant);

		headers.add("x-okapi-token", FolioServiceToken.authToken);

		return headers;

	}

	private HttpEntity<String> getHttpRequest() {

		return new HttpEntity<String>(getHttpHeaders());
	}

	private int apiRecordlimit = 1000;

	public boolean updateLoan(Loan payload) {

		HttpEntity<?> request = new HttpEntity<Object>(payload, getHttpHeaders());

		try {

			String url = AppSystemProperties.FolioURL + "circulation/loans/" + payload.id;

			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

			return responseEntity.getStatusCode().toString().equals("204 NO_CONTENT") ? true : false;

		} catch (Exception e) {

			System.out.println("updateLoan - " + payload.id);

			System.err.println("Error: FolioService : updateLoan " + e.getMessage());

			return false;
		}
	}

	public ArrayList<Item> getItembyBarcode(String barcode)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "inventory/items?query=(barcode ==" + barcode + ")";

			ResponseEntity<ItemRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					ItemRoot.class);

			// System.out.println("Total records- " + response.getBody());

			return response.getBody().items;

		} catch (Exception e) {

			System.err.println("Error: FolioService : getItembyBarcode " + e.getMessage());

			return null;
		}
	}

	public ArrayList<Item> getItemsByLocationId(String locationID, String locationName)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String mainUrl = AppSystemProperties.FolioURL + "inventory/items?limit=%s&query=(effectiveLocationId=="
					+ locationID + ")sortby id";

			// System.out.println("mainUrl - " + String.format(mainUrl, "0"));

			ResponseEntity<ItemRoot> response = restTemplate.exchange(String.format(mainUrl, "0"), HttpMethod.GET,
					getHttpRequest(), ItemRoot.class);

			apiRecordlimit = 100;

			int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);

			System.out.println("totalIterations" + totalIterations);

			ArrayList<Item> items = new ArrayList<>();

			String fileName = locationName + ".csv";

			try (FileWriter writer = new FileWriter(fileName)) {

				writer.append("ID##CallNUmber##BarCode##Title");

				for (int iterations = 0; iterations < totalIterations; iterations++) {

					int offset = iterations * apiRecordlimit;

					String url = String.format(mainUrl, "100") + "&offset=" + offset;

					// System.out.println("url - " + url);

					System.out.println("iterations - " + iterations);

					response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), ItemRoot.class);

					// items.addAll(response.getBody().items);

					for (Item item : response.getBody().items) {

						int loanCount = this.getLoanCountByItemId(item.id);

						if (loanCount == 0 && item.lastCheckIn == null) {

							// Item item folioService.getItemByItemId(item.id);

							writer.append(
									item.id + "##" + item.callNumber + "##" + item.barcode + "##" + item.title + "\n");

						}

//					if (itemCount % 1000 == 0) {
//						System.out.println("Item count " + itemCount);
//					}
//
//					itemCount++;

					}

					Thread.sleep(5000);

				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;

		} catch (Exception e) {

			System.err.println("Error: FolioService : getItemsByLocationId " + e.getMessage());

			return null;
		}

	}

	public Item getItemByItemId(String itemId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {
		try {
			String url = AppSystemProperties.FolioURL + "item-storage/items/" + itemId + ")";

			ResponseEntity<Item> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Item.class);

			return response.getBody();

		} catch (Exception e) {

			System.err.println("Error: FolioService : getItemByItemId " + e.getMessage());

			return null;
		}

	}

	public int getItemCountByLocationId(String locationID)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {
		try {
			String url = AppSystemProperties.FolioURL + "inventory/items?query=(effectiveLocationId=" + locationID
					+ ")&limit=0";

			ResponseEntity<HoldingRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					HoldingRoot.class);

			return response.getBody().totalRecords;

		} catch (Exception e) {

			System.err.println("Error: FolioService : getItemCountByLocationId " + e.getMessage());

			return 0;
		}

	}

	/**
	 * Reads from the inventory items FOLIO API relevant holdings record
	 * 
	 * @param holdingRecordId
	 * @return relevant holding record
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws RestClientException
	 * @throws IOException
	 */
	public ItemRoot getItemByHoldingRecordId(String holdingRecordId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "inventory/items?query=(holdingsRecordId == " + holdingRecordId
					+ " )";

			ResponseEntity<ItemRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					ItemRoot.class);

			return response.getBody();

		} catch (Exception e) {

			System.err.println("Error: FolioService : getItemByHoldingRecordId " + e.getMessage());

			return null;
		}
	}

	/**
	 * finds the relevant holding records relevant to the holding record id
	 * 
	 * @param holdingRecordId
	 * @return relevant holding record
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws RestClientException
	 * @throws IOException
	 */
	public HoldingsRecord getHoldingRecordByHoldingId(String holdingRecordId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "holdings-storage/holdings/" + holdingRecordId;

			ResponseEntity<HoldingsRecord> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					HoldingsRecord.class);

			return response.getBody();

		} catch (Exception e) {

			System.err.println("Error: FolioService : getHoldingRecordByHoldingId " + e.getMessage());

			return null;
		}
	}

	/**
	 * 
	 * @param instanceId
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws RestClientException
	 * @throws IOException
	 */
	public ArrayList<Instance> getInstanceByInstanceId(String instanceId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "inventory/instances?query=(id= " + instanceId + ")";

			ResponseEntity<InstanceRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					InstanceRoot.class);

			// System.out.println("Total records- " + response.getBody());

			return response.getBody().instances;

		} catch (Exception e) {

			System.err.println("Error: FolioService : getHoldingRecordByHoldingId " + e.getMessage());
			
			return null;
		}
	}

	public int getLoanCountByItemId(String itemId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "circulation/loans?limit=0&query=(itemId==\"" + itemId + "\")";

			// System.out.println("url " + url);

			ResponseEntity<LoanRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					LoanRoot.class);

			// System.out.println("Total records- " + response.getBody());

			return response.getBody().totalRecords;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return 0;
		}
	}

	public Loan getLoansByLoanId(String loadId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "circulation/loans?query=(id==\"" + loadId + "\")";

			ResponseEntity<LoanRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					LoanRoot.class);

			// System.out.println("Total records- " + response.getBody());

			return response.getBody().loans.get(0);

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Request> getOpenRequests()
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL
					+ "circulation/requests?query=(requestType==\"Hold\" and  status ==\"Open - Not yet filled\")&limit=100";

			ResponseEntity<RequestRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					RequestRoot.class);

			// System.out.println("Total records- " + response.getBody().totalRecords);

			return response.getBody().requests;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Loan> getLoansByUser(String userId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "circulation/loans?limit=1000&query=(userId==\"" + userId
					+ "\" and status.name = \"open\")";

			ResponseEntity<LoanRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					LoanRoot.class);

			// System.out.println("Total records- " + response.getBody().totalRecords);

			return response.getBody().loans;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public int getClosedLoansCount() throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "loan-storage/loans?query=(status.name==\"closed\" )&limit=1";

			ResponseEntity<CirculationRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					CirculationRoot.class);

			System.out.println("Total records- " + response.getBody().totalRecords);

			return response.getBody().totalRecords;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return 0;
		}
	}

	public ArrayList<Loan> getOpenedLoans()
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String mainUrl = AppSystemProperties.FolioURL + "loan-storage/loans?query=(status.name==\"open\")&limit=";

			ResponseEntity<CirculationRoot> response = restTemplate.exchange(mainUrl + "1", HttpMethod.GET,
					getHttpRequest(), CirculationRoot.class);

			int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);

			System.out.println("totalIterations" + totalIterations);

			ArrayList<Loan> loans = new ArrayList<>();

			for (int iterations = 0; iterations < totalIterations; iterations++) {

				int offset = iterations * apiRecordlimit;

				String url = mainUrl + apiRecordlimit + "&offset=" + offset;

				// System.out.println("url- " + url);

				response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), CirculationRoot.class);

				loans.addAll(response.getBody().loans);
			}

			return loans;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Loan> getClosedLoansByLocation(String locationId, String startDateTime, String endDateTime,
			boolean isDailyProcess) throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {
			String mainUrl = "";

			if (isDailyProcess) {

				mainUrl = AppSystemProperties.FolioURL
						+ "loan-storage/loans?query=(status.name==\"closed\" and returnDate>=" + startDateTime
						+ " AND returnDate<=" + endDateTime + " and itemEffectiveLocationIdAtCheckOut==" + locationId
						+ ")&limit=";

			} else {
				mainUrl = AppSystemProperties.FolioURL
						+ "loan-storage/loans?query=(status.name==\"closed\" and itemEffectiveLocationIdAtCheckOut=="
						+ locationId + ")&limit=";
			}

			System.out.println("url- " + mainUrl);

			ResponseEntity<CirculationRoot> response = restTemplate.exchange(mainUrl + "0", HttpMethod.GET,
					getHttpRequest(), CirculationRoot.class);

			System.out.println("TotalRecords - " + response.getBody().totalRecords);

			int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);

			// System.out.println("ClosedLoans totalIterations- " + totalIterations);

			ArrayList<Loan> loans = new ArrayList<>();

			apiRecordlimit = 10000;

			for (int iterations = 0; iterations < totalIterations; iterations++) {

				int offset = iterations * apiRecordlimit;

				String url = mainUrl + apiRecordlimit + "&offset=" + offset;

				// System.out.println("url- " + url);

				response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), CirculationRoot.class);

				// Collections.addAll(folioUsers, response.getBody());

				loans.addAll(response.getBody().loans);
			}

			return loans;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Loan> getClosedLoans(boolean isClosed, String servicePoint)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = "";

			if (isClosed) {
				url = AppSystemProperties.FolioURL + "loan-storage/loans?limit=1&query=(status.name==closed)";

			} else {
				// loan-storage/loans?limit=1&query=(status.name==open)";

				url = AppSystemProperties.FolioURL + "loan-storage/loans?query=(status.name==\"open\" AND "
						+ "checkoutServicePointId == \"" + servicePoint + "\")&limit=1";
			}

			System.out.println(url);

			ResponseEntity<CirculationRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					CirculationRoot.class);

			int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);

			ArrayList<Loan> loans = new ArrayList<>();

			for (int iterations = 0; iterations < totalIterations; iterations++) {

				response = null;

				int offset = iterations * apiRecordlimit;

				if (isClosed) {

					url = AppSystemProperties.FolioURL + "loan-storage/loans?limit=" + apiRecordlimit
							+ "&query=(status.name==closed)&offset=" + offset;

				} else {
					// loan-storage/loans?limit=1&query=(status.name==open)";

					url = AppSystemProperties.FolioURL + "loan-storage/loans?limit=" + apiRecordlimit
							+ "&query=(status.name==\"open\" AND checkoutServicePointId == \"" + servicePoint + "\")";
				}

				response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), CirculationRoot.class);

				loans.addAll(response.getBody().loans);
			}

			return loans;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<HoldingsRecord2> getInventoryHoldingsByLocation(String locationID)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {
			String url = AppSystemProperties.FolioURL + "holdings-storage/holdings?query=(effectiveLocationId="
					+ locationID + ")&limit=0";

			ResponseEntity<HoldingRoot2> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					HoldingRoot2.class);

			int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);

			ArrayList<HoldingsRecord2> pairList = new ArrayList<>();

			System.out.println("totalIterations - " + totalIterations);

			// totalIterations = 20;

			for (int iterations = 0; iterations < totalIterations; iterations++) {

				int offset = iterations * apiRecordlimit;

				url = AppSystemProperties.FolioURL + "holdings-storage/holdings?query=(effectiveLocationId="
						+ locationID + ")&limit=" + apiRecordlimit + "&offset=" + offset;

				response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), HoldingRoot2.class);

				pairList.addAll(response.getBody().holdingsRecords);
			}

			return pairList;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}

	}

	public int getInventoryHoldingsCount(String locationID)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {
		try {
			String url = AppSystemProperties.FolioURL + "holdings-storage/holdings?query=(effectiveLocationId= "
					+ locationID + ")&limit=0";

			ResponseEntity<HoldingRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					HoldingRoot.class);

			return response.getBody().totalRecords;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return 0;
		}

	}

	public ArrayList<HoldingsRecord> getInventoryHoldings(String locationID, String startDateTime, String endDateTime)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {
		try {
			String url = AppSystemProperties.FolioURL + "holdings-storage/holdings?query=(effectiveLocationId= "
					+ locationID + ")&limit=0";

			ResponseEntity<HoldingRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					HoldingRoot.class);

			System.out.print("" + response.getBody().totalRecords);

			int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);

			ArrayList<HoldingsRecord> pairList = new ArrayList<>();

			System.out.println("totalIterations - " + totalIterations);

			for (int iterations = 0; iterations < totalIterations; iterations++) {

				int offset = iterations * apiRecordlimit;

				url = AppSystemProperties.FolioURL + "holdings-storage/holdings?query=( effectiveLocationId= "
						+ locationID + " )&limit=" + apiRecordlimit + "& offset=" + offset;

//						+ "query=(returnDate>=" + startDateTime
//						+ " AND returnDate<=" + endDateTime + ")&limit=" + 10 + "

				response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), HoldingRoot.class);

				pairList.addAll(response.getBody().holdingsRecords);
			}

			return pairList;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}

	}

	public Set<String> getInventoryInstance(String instanceId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "inventory/instances?query=(id=" + instanceId + ")";

			ResponseEntity<InstanceRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					InstanceRoot.class);

			ArrayList<Instance> Instance = response.getBody().instances;

			Set<String> oclcNumbers = new LinkedHashSet<>();

			for (Instance instance : Instance) {

				for (Identifier identifier : instance.identifiers) {

					String originalText = identifier.value;

					if (originalText.toLowerCase().contains("(ocolc)")
							&& identifier.identifierTypeId.equals("439bfbae-75bc-4f74-9fc7-b2a2d47ce3ef")) {

						oclcNumbers.add(originalText.replaceAll("\\D+", ""));
					}

				}
			}

			return oclcNumbers;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public List<HashMap<String, List<String>>> getInventoryIdentifiers(String startDateTime, String endDateTime)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "inventory/instances?limit=1";
//					+ "query=(returnDate>=" + startDateTime
//					+ " AND returnDate<=" + endDateTime + ")&limit=1";

			System.out.println("url- " + url);

			ResponseEntity<InstanceRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					InstanceRoot.class);

			// System.out.println("response.getBody().totalRecords- " +
			// response.getBody().totalRecords);

			int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);

			List<HashMap<String, List<String>>> pairList = new ArrayList<>();

			System.out.println("totalIterations - " + totalIterations);

			for (int iterations = 0; iterations < 5; iterations++) {

				int offset = iterations * apiRecordlimit;

				url = AppSystemProperties.FolioURL + "inventory/instances?limit=" + apiRecordlimit + "& offset="
						+ offset;
//						+ "query=(returnDate>=" + startDateTime
//						+ " AND returnDate<=" + endDateTime + ")&limit=" + 10 + "

				response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), InstanceRoot.class);

				ArrayList<Instance> Instance = response.getBody().instances;

				// System.out.println("Instance.size - " + Instance.size());

				for (Instance instance : Instance) {

					HashMap<String, List<String>> map = new HashMap<>();

					List<String> list = new ArrayList<String>();

					for (Identifier identifier : instance.identifiers) {

						String originalText = identifier.value;

						if (originalText.toLowerCase().contains("(ocolc)")) {

							list.add(originalText + "#" + originalText.replaceAll("\\D+", ""));
						}

					}

					map.put(instance.id, list);

					pairList.add(map);
				}

			}

			System.out.println("pairList.size - " + pairList.size());

			return pairList;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public ItemRoot getInventoryItemById(String holdingsRecordId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "inventory/items-by-holdings-id?query=(holdingsRecordId=="
					+ holdingsRecordId + ")";

			// System.out.println("url" + url);

			ResponseEntity<ItemRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					ItemRoot.class);

			// System.out.println(response);

			return response.getBody();

		} catch (Exception e) {

			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public Item getItem(String itemId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "inventory/items/" + itemId;

			ResponseEntity<Item> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Item.class);

			// System.out.println("Total records- " + response.getBody().totalRecords);

			return response.getBody();

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public boolean updateItem(Item payload) {

		HttpEntity<?> request = new HttpEntity<Object>(payload, getHttpHeaders());

		try {

			String url = AppSystemProperties.FolioURL + "inventory/items/" + payload.id;

			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

			return responseEntity.getStatusCode().toString().equals("204 NO_CONTENT") ? true : false;

		} catch (Exception e) {

			System.out.println("Error update Item - " + payload.id);

			e.getMessage();

			e.printStackTrace();

			return false;
		}
	}

	public boolean updateInventoryItem(Item payload) {

		HttpEntity<?> request = new HttpEntity<Object>(payload, getHttpHeaders());

		try {

			String url = AppSystemProperties.FolioURL + "inventory/holdings/" + payload.id;

			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

			return responseEntity.getStatusCode().toString().equals("204 NO_CONTENT") ? true : false;

		} catch (Exception e) {

			System.out.println("Error update Item - " + payload.id);

			e.getMessage();

			e.printStackTrace();

			return false;
		}
	}

	public ArrayList<HoldingsRecord> getHoldingsStorageByLocationIdAndHoldingStaement(String permanentLocationId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL
					+ "holdings-storage/holdings?limit=1&query=(holdingsStatements <> [] AND permanentLocationId =="
					+ permanentLocationId + ")";

			// System.out.println("url- " + url);

			ResponseEntity<HoldingRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					HoldingRoot.class);

			// System.out.println("response.getBody().totalRecords- " +
			// response.getBody().totalRecords);

			int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);

			ArrayList<HoldingsRecord> holdings = new ArrayList<>();

			// System.out.println("totalIterations - " + totalIterations);

			for (int iterations = 0; iterations < totalIterations; iterations++) {

				int offset = iterations * apiRecordlimit;

				url = AppSystemProperties.FolioURL + "holdings-storage/holdings?limit=" + apiRecordlimit
						+ "&query=(holdingsStatements <> []  AND permanentLocationId ==" + permanentLocationId
						+ ")& offset=" + offset;

				// System.out.println("url- " + url);

				response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), HoldingRoot.class);

				ArrayList<HoldingsRecord> instances = response.getBody().holdingsRecords;

				for (HoldingsRecord holdingRecord : instances) {

					if (holdingRecord.holdingsStatements != null && holdingRecord.holdingsStatements.size() > 0) {

						HoldingsStatement holdingsStatement = holdingRecord.holdingsStatements.get(0);

//						if (!StringHelper.isStringNullOrEmpty(holdingsStatement.statement)
//								|| !StringHelper.isStringNullOrEmpty(holdingsStatement.staffNote)) {

						// System.out.println("holdingRecord.id " + holdingRecord.id);

						holdings.add(holdingRecord);
						// }
					}

				}
			}

			return holdings;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}

	}

	public HoldingsRecord getHoldingsByHoldingsId(String holdingInstanceId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "holdings-storage/holdings/" + holdingInstanceId;

			ResponseEntity<HoldingsRecord> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					HoldingsRecord.class);

			return response.getBody();

		} catch (Exception e) {

			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public HoldingRoot getHoldingsStorageByLocationId(String permanentLocationId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL
					+ "holdings-storage/holdings?limit=10000&query=(permanentLocationId==" + permanentLocationId + ")";

			ResponseEntity<HoldingRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					HoldingRoot.class);

			return response.getBody();

		} catch (Exception e) {

			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public HoldingRoot getHoldingsStorageWithHoldingsStatemxents()
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL
					+ "holdings-storage/holdings?limit=10000&query=(permanentLocationId==)";

			ResponseEntity<HoldingRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					HoldingRoot.class);

			return response.getBody();

		} catch (Exception e) {

			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public FolioUser getUserByExternalSystemId(String externalSystemId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "users?query=(externalSystemId==" + externalSystemId + ")";

			ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);

			if (response.getBody().users.size() > 0) {

				return response.getBody().users.get(0);

			} else {

				System.out.println("externalSystemId is null for" + externalSystemId);

				return null;
			}

		} catch (Exception e) {

			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	///
	// Get Patron Group By name
	///
	public FolioPatronGroup getPatronGroups()
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "groups?limit=1000";

			ResponseEntity<FolioPatronGroup> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					FolioPatronGroup.class);

			return response.getBody();

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	///
	// Get Patron Group By Id
	///
	public FolioPatronGroup getPatronGroupById(String id)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "groups?query=(id=" + id + ")";

			ResponseEntity<FolioPatronGroup> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					FolioPatronGroup.class);

			return response.getBody();

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	///
	// Get all the available categories.
	///
	public FolioPatronGroup getPatronGroup(String url)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			ResponseEntity<FolioPatronGroup> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					FolioPatronGroup.class);

			return response.getBody();

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public boolean updateUser(FolioUser payload) {

		HttpEntity<?> request = new HttpEntity<Object>(payload, getHttpHeaders());

		try {

//			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//			String json = ow.writeValueAsString(payload);
//			System.out.println(json);

			String url = AppSystemProperties.FolioURL + "users/" + payload.id;

			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

			return responseEntity.getStatusCode().toString().equals("204 NO_CONTENT") ? true : false;

		} catch (Exception e) {

			System.out.println("Folio Update User - " + payload.externalSystemId);

			e.getMessage();
			e.printStackTrace();

			printScreen("Folio Update User - " + payload.externalSystemId, Constants.ErrorLevel.ERROR);
			printScreen(e.getMessage(), Constants.ErrorLevel.ERROR);
//
			System.out.println(e.getMessage());

			return false;
		}
	}

	public String createUser(FolioUser payload) throws JsonMappingException, JsonProcessingException {

		HttpHeaders headers = getHttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<?> request = new HttpEntity<Object>(payload, headers);

		try {

			Gson gson = new Gson();
			String jsonString = gson.toJson(payload);
			System.out.println("jsonString" + jsonString);

			String url = AppSystemProperties.FolioURL + "users";

			this.restTemplate.exchange(url, HttpMethod.POST, request, FolioUser.class);

			return null;

		} catch (Exception e) {

			System.out.println("Error in Folio Create User");
			e.getMessage();
			e.printStackTrace();

			printScreen("Error in Folio Create User", Constants.ErrorLevel.ERROR);
			printScreen(e.getMessage(), Constants.ErrorLevel.ERROR);

			if (e instanceof HttpStatusCodeException) {
				String errorResponse = ((HttpStatusCodeException) e).getResponseBodyAsString();

				ErrorRoot map = mapper.readValue(errorResponse, ErrorRoot.class);

				if (map.errors != null && map.errors.size() > 0) {

					String error_message = map.errors.get(0).message;

					if (error_message.contains("value already exists in table")) {
						return "user_exists";
					}

					return error_message;
				}

				return null;
			}
		}

		return null;
	}

//	///
//		public ArrayList<FolioUser>  getUsersbyPatronGroup(String patronGroup)
//				throws JsonParseException, JsonMappingException, RestClientException, IOException {
//
//			try {
//
//
//				String url = AppSystemProperties.FolioURL + "users?query=patronGroup=" + patronGroup + "  and active==\"true\"&limit=1";
//
//				ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);
//
//				int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);
//
//				ArrayList<FolioUser> folioUsers = new ArrayList<>();
//
//				for (int iterations = 0; iterations < totalIterations; iterations++) {
//
//					int offset = iterations * apiRecordlimit;
//
//					url = AppSystemProperties.FolioURL + "users?query=patronGroup=" + patronGroup + "  and active==\"true\"&limit="
//							+ apiRecordlimit + "&offset=" + offset;
//
//					response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);
//
//					// Collections.addAll(folioUsers, response.getBody());
//
//					folioUsers.addAll((Collection<? extends FolioUser>) response.getBody());
//				}
//
//				return folioUsers;
//				
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.getMessage();
//				e.printStackTrace();
//				return null;
//			}
//		}
//
//		///
//		// Get users by the institute code -
//		///
//		public ArrayList<FolioUser> getInactiveUsersbyPatronGroup(String patronGroup)
//				throws JsonParseException, JsonMappingException, RestClientException, IOException {
//
//			try {
//
//				String url = AppSystemProperties.FolioURL + "users?query=patronGroup=" + patronGroup + "  and active==\"false\"&limit=1";
//
//				ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);
//
//				int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);
//
//				ArrayList<FolioUser> folioUsers = new ArrayList<>();
//
//				for (int iterations = 0; iterations < totalIterations; iterations++) {
//
//					int offset = iterations * apiRecordlimit;
//
//					url = AppSystemProperties.FolioURL + "users?query=patronGroup=" + patronGroup + "  and active==\"false\"&limit="
//							+ apiRecordlimit + "&offset=" + offset;
//
//					response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);
//
//					// Collections.addAll(folioUsers, response.getBody());
//
//					folioUsers.addAll((Collection<? extends FolioUser>) response.getBody());
//				}
//
//				return folioUsers;
//
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.getMessage();
//				e.printStackTrace();
//				return null;
//			}
//		}
//		

	public Root getActiveUsers() throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "users?query=active==\"true\"&limit=100000";

			ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);

			return response.getBody();

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	///
	// Get users by the institute code -
	///
	public Root getUsersbyPatronGroup(String patronGroup)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "users?query=patronGroup=" + patronGroup
					+ "  and active==\"true\"&limit=100000";

			ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);

			return response.getBody();

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	///
	// Get users by the institute code -
	///
	public Root getInactiveUsersbyPatronGroup(String patronGroup)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "users?query=patronGroup=" + patronGroup
					+ "  and active==\"false\"&limit=100000";

			ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);

			return response.getBody();

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	///
	// Get over due accounts by the institute code -
	///
	public ArrayList<ManualBlock> getPatronsBlocks()
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "manualblocks";

			ResponseEntity<ManualBlockRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					ManualBlockRoot.class);

			int totalIterations = (int) Math.ceil((double) response.getBody().manualblocks.size() / apiRecordlimit);

			ArrayList<ManualBlock> blocks = new ArrayList<>();

			for (int iterations = 0; iterations < totalIterations; iterations++) {

				response = null;

				int offset = iterations * apiRecordlimit;

				url = AppSystemProperties.FolioURL + "manualblocks?limit=" + apiRecordlimit + "&offset" + offset;

				response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), ManualBlockRoot.class);

				blocks.addAll(response.getBody().manualblocks);
			}

			return blocks;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	///
	// Get over due accounts by the institute code -
	///
	public ArrayList<Account> getOverDueAccounts(String feeFineOwner)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "accounts?query=(status.name==\"open\" and feeFineOwner=\""
					+ feeFineOwner + "  \")";

			ResponseEntity<Accounts> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					Accounts.class);

			int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);

			ArrayList<Account> accounts = new ArrayList<>();

			for (int iterations = 0; iterations < totalIterations; iterations++) {

				response = null;

				int offset = iterations * apiRecordlimit;

				url = AppSystemProperties.FolioURL + "accounts?limit=" + apiRecordlimit
						+ "&query=(status.name==\"open\" and feeFineOwner=\"" + feeFineOwner + "  \")&offset" + offset;

				response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Accounts.class);

				accounts.addAll(response.getBody().accounts);
			}

			return accounts;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public PatronBlockRoot getAutomatedPatronBlocks(String userId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "automated-patron-blocks/" + userId;

			// System.out.println(url);

			ResponseEntity<PatronBlockRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					PatronBlockRoot.class);

			return response.getBody();

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	///
	// Get loans based on the location and the year
	///
	public Inventory getInventoryLoanDetails(String location, String year)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {
		try {

			String url = AppSystemProperties.FolioURL + "circulation/loans?limit=1000&query=(status.name==\"open\" and "
					+ "itemEffectiveLocationIdAtCheckOut==\"" + location + "\" AND dueDate >= '" + year + "-01-01' \n"
					+ "AND dueDate < '" + year + "-12-31')";

			ResponseEntity<Inventory> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					Inventory.class);

			return response.getBody();

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	///
	// Get users by the patron group id - GUID
	///
	public Root getUsersByPatronGroupIdForLoans(String userGroupId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "users?query=(active==true and patronGroup==" + userGroupId
					+ ")&limit=10000";

			ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);

			return response.getBody();

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	///
	// Get users by the patron group id - GUID
	///
	public Root getUsersByPatronGroupId(String userGroupId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "users?query=(active==true and patronGroup==" + userGroupId
					+ ")&limit=40000";

			// String url = AppSystemProperties.FolioURL + "users?active=true&patronGroup="
			// + userGroupId;

			ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);

			return response.getBody();

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	///
	// Get users by the patron group id - GUID
	///
	public ArrayList<FolioUser> getUsersByPatronGroupId2(String userGroupId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "users?query=(patronGroup==" + userGroupId + ")&limit=1";

			ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);

			int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);

			ArrayList<FolioUser> users = new ArrayList<>();

			for (int iterations = 0; iterations < totalIterations; iterations++) {

				response = null;

				int offset = iterations * apiRecordlimit;

				url = AppSystemProperties.FolioURL + "users?query=(patronGroup==" + userGroupId + ")&limit="
						+ apiRecordlimit + "&offset" + offset;

				response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);

				users.addAll(response.getBody().users);
			}

			return users;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public FolioUser getUsersByBarcode(String barCode)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "/users?query=barcode==" + barCode;

			ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);

			if (response.getBody().users.size() > 0) {
				return response.getBody().users.get(0);
			} else {
				return null;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public FolioUser getUsersById(String userId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "users?query=id==" + userId;

			ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);

			if (response.getBody().users.size() > 0) {
				return response.getBody().users.get(0);
			} else {
				return null;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public FolioUser getUsersByExternalSystemId(String userId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "users?query=(active=false and externalSystemId==  " + userId
					+ ")";

			ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);

			if (response.getBody() != null && response.getBody().users != null && response.getBody().users.size() > 0)
				return response.getBody().users.get(0);
			else
				return null;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public void printScreen(String msg, Constants.ErrorLevel errorLevel) {
		switch (errorLevel) {
		case ERROR:
			LOG.error(msg);
			break;
		case INFO:
			LOG.info(msg);
			break;
		case WARNING:
			LOG.warn(msg);
			break;
		default:
			LOG.warn(msg);
			break;

		}
	}

	public ArrayList<com.okstatelibrary.redbud.folio.entity.servicepoint.ServicePoint> getServicePoints() {

		try {

			String url = AppSystemProperties.FolioURL + "service-points?limit=100";

			ResponseEntity<ServicePointRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					ServicePointRoot.class);

			return response.getBody().servicepoints;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<com.okstatelibrary.redbud.folio.entity.institution.Institution> getInstitutions() {

		try {

			String url = AppSystemProperties.FolioURL + "location-units/institutions?limit=20";

			ResponseEntity<InstitutionRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					InstitutionRoot.class);

			return response.getBody().locinsts;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<com.okstatelibrary.redbud.folio.entity.campus.Campus> getCampuses() {

		try {

			String url = AppSystemProperties.FolioURL + "location-units/campuses?limit=20";

			ResponseEntity<CampusRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					CampusRoot.class);

			return response.getBody().loccamps;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<com.okstatelibrary.redbud.folio.entity.library.Library> getLibraries() {

		try {

			String url = AppSystemProperties.FolioURL + "location-units/libraries?limit=500";

			ResponseEntity<LibraryRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					LibraryRoot.class);

			return response.getBody().loclibs;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<com.okstatelibrary.redbud.folio.entity.location.Location> getLocations() {

		try {

			String url = AppSystemProperties.FolioURL + "locations?limit=500";

			ResponseEntity<LocationRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					LocationRoot.class);

			return response.getBody().locations;

		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

}
