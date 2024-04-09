package com.okstatelibrary.redbud.service.external;

import java.io.IOException;
import java.util.ArrayList;
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
import com.okstatelibrary.redbud.folio.entity.inventory.Inventory;
import com.okstatelibrary.redbud.folio.entity.inventory.Item;
import com.okstatelibrary.redbud.folio.entity.loan.Loan;
import com.okstatelibrary.redbud.folio.entity.loan.LoanRoot;
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

		headers.add("x-okapi-token", this.getToken());

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

			e.getMessage();

			e.printStackTrace();

			return false;
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

	public ArrayList<Loan> getClosedLoansCountByDate(String startDateTime, String endDateTime)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

//			String url = AppSystemProperties.FolioURL + "loan-storage/loans?query=(status.name==\"closed\" and returnDate>=" + startDateTime
//					+ " AND returnDate<=" + endDateTime + ")&limit=1";
//
//			ResponseEntity<CirculationRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
//					CirculationRoot.class);
//
//			System.out.println("Total records- " + response.getBody().totalRecords);
//
//			return response.getBody().totalRecords;

			String url = AppSystemProperties.FolioURL
					+ "loan-storage/loans?query=(status.name==\"closed\" and returnDate>=" + startDateTime
					+ " AND returnDate<=" + endDateTime + ")&limit=1";

			System.out.println("url- " + url);

			ResponseEntity<CirculationRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					CirculationRoot.class);

			int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);

			ArrayList<Loan> loans = new ArrayList<>();

			for (int iterations = 0; iterations < totalIterations; iterations++) {

				int offset = iterations * apiRecordlimit;

				url = AppSystemProperties.FolioURL
						+ "loan-storage/loans?query=(status.name==\"closed\" and returnDate>=" + startDateTime
						+ " AND returnDate<=" + endDateTime + ")&limit=" + apiRecordlimit + "& offset=" + offset;

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

	public ArrayList<Loan> getClosedLoans()
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "loan-storage/loans?limit=1&query=(status.name==closed)";

			ResponseEntity<CirculationRoot> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(),
					CirculationRoot.class);

			int totalIterations = (int) Math.ceil((double) response.getBody().totalRecords / apiRecordlimit);

			ArrayList<Loan> loans = new ArrayList<>();

			for (int iterations = 0; iterations < totalIterations; iterations++) {

				response = null;

				int offset = iterations * apiRecordlimit;

				url = AppSystemProperties.FolioURL + "loan-storage/loans?limit=" + apiRecordlimit
						+ "&query=(status.name==closed)&offset=" + offset;

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

	public FolioUser getUserByExternalSystemId(String externalSystemId)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

			String url = AppSystemProperties.FolioURL + "users?query=(externalSystemId==" + externalSystemId + ")";

			ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);

			return response.getBody().users.get(0);

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
	public ArrayList<Account> getOverDueAccounts(String feeFineOwner)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try {

//			System.out.println("getOverDueAccounts");
//
//			FolioServiceToken token = new FolioServiceToken();
//			token.getToken();

			String url = AppSystemProperties.FolioURL + "accounts?query=(status.name==\"open\" and feeFineOwner=\""
					+ feeFineOwner + "  \")";

			// System.out.println("url - " + url);

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

	public Inventory getInventoryLoanDetails(String location)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {
		try {

			String url = "https://okapi-okstate.folio.ebsco.com/circulation/loans?limit=1000&query=(status.name==\"open\" and "
					+ "itemEffectiveLocationIdAtCheckOut==\"" + location + "\")";

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

			String url = AppSystemProperties.FolioURL + "users?active=true&patronGroup=" + userGroupId;

			ResponseEntity<Root> response = restTemplate.exchange(url, HttpMethod.GET, getHttpRequest(), Root.class);

			return response.getBody();

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

			return response.getBody().users.get(0);

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

			return response.getBody().users.get(0);

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
