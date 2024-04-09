package com.okstatelibrary.redbud.operations;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.oltu.oauth2.client.HttpClient;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

//import java.util.logging.Logger;
//import java.util.logging.Level;
//import java.util.Base64;
//import java.util.Map;
//import java.util.HashMap;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.text.SimpleDateFormat;
//
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.util.EntityUtils;
//import org.apache.xmlbeans.impl.xb.xmlconfig.ConfigDocument.Config;
//import org.apache.http.HttpHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OCLCMetadataProcess {

	// private static final Logger log = Logger.getLogger(Oclc.class.getName());

	private static final String SERVICE_URL = "https://metadata.api.oclc.org/worldcat";
	private static final String TOKEN_URL = "https://oauth.oclc.org/token";
	private static final List<String> SCOPES = Arrays.asList("WorldCatMetadataAPI");
	private static final int SESSION_BUFFER = 60; // seconds

	// private final Config config;
	private String token;
	private long tokenExpirationTime;

	public OCLCMetadataProcess() throws OAuthSystemException, OAuthProblemException {
		// this.config = config;Config config
		// log.addHandler(this.config.getLogFileHandler());
		this.initConnection();
	}

	private void initConnection() throws OAuthSystemException, OAuthProblemException {

		try {

			String wsKey = "1en9hGcZZlKhFfv5RAjwAAJdgWoATjrTlB0l3nRyUB8XmErEgND1iW2WGdUw5SIdgC35AsJygu22DnVE";

			String secret = "2+t07RFacx0ph8ygxDgl1GCdeWTxfk41";

			OAuthClient client = new OAuthClient(new URLConnectionClient());

			OAuthClientRequest request = OAuthClientRequest
					.tokenLocation(TOKEN_URL)
					.setGrantType(GrantType.CLIENT_CREDENTIALS)
					.setClientId(wsKey)
					.setClientSecret(secret)
					.setScope("WorldCatMetadataAPI")
					.buildBodyMessage();

			// OAuthClient client = new OAuthClient(new URLConnectionClient());

			OAuthJSONAccessTokenResponse oauthResponse = client.accessToken(request, OAuth.HttpMethod.POST);

			token = client.accessToken(request, OAuth.HttpMethod.POST, OAuthJSONAccessTokenResponse.class)
					.getAccessToken();

			System.out.println("token" + oauthResponse.getBody());
			
		} catch (Exception e) {
			System.out.println(e.getMessage());

			//throw new RuntimeException(e);

		}

//String keys = wsKey+":Client secret goes here";
////		var URL = "http://localhost:8080/api/token"
//
//		HashMap<String, String> parameters = new HashMap<>();
//		parameters.put("grant_type", "client_credentials");
//		String form = parameters.keySet().stream()
//		        .map(key -> key + "=" + URLEncoder.encode(parameters.get(key), StandardCharsets.UTF_8))
//		        .collect(Collectors.joining("&"));
//
//		String encoding = Base64.getEncoder().encodeToString(keys.getBytes());
//		HttpClient client = HttpClient.newHttpClient();
//
//		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
//		        .headers("Content-Type", "application/x-www-form-urlencoded", "Authorization", "Basic "+encoding)
//		        .POST(BodyPublishers.ofString(form)).build();
//		HttpResponse<?> response = client.send(request, BodyHandlers.ofString());
//		System.out.println(response.statusCode() + response.body().toString());
//		

//		String wsKey = "1en9hGcZZlKhFfv5RAjwAAJdgWoATjrTlB0l3nRyUB8XmErEgND1iW2WGdUw5SIdgC35AsJygu22DnVE";
//		// this.config.get("Oclc",
//		// "ws_key");
//		String secret = "2+t07RFacx0ph8ygxDgl1GCdeWTxfk41"; // this.config.get("Oclc", "secret");
//
//		String basicAuthHeader = "Basic " + Base64.getEncoder().encodeToString((wsKey + ":" + secret).getBytes());
//
//		this.token = "";
//		this.tokenExpirationTime = 0;
//
//		try {
//			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
//
//			HttpResponse httpResponse = httpClientBuilder.build().execute(new HttpPost(TOKEN_URL) {
//				{
//					setHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader);
//					setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
//					setEntity(new StringEntity("grant_type=client_credentials"));
//				}
//			});
//
//			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//
//				@SuppressWarnings("unchecked")
//				Map<String, Object> responseMap = new ObjectMapper()
//						.readValue(EntityUtils.toString(httpResponse.getEntity()), Map.class);
//
//				this.token = (String) responseMap.get("access_token");
//				long expiresIn = ((Number) responseMap.get("expires_in")).longValue();
//				this.tokenExpirationTime = new Date().getTime() + expiresIn * 1000;
//				System.out.println("Got token." + this.token);
//
//			} else {
////				log.log(Level.SEVERE,
////						"Failed to get token. Status code: " + httpResponse.getStatusLine().getStatusCode());
//
//				System.out.println("AASASA" + httpResponse.getStatusLine());
//				// throw new RuntimeException("Failed to get token.");
//			}
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//
//			throw new RuntimeException(e);
//		}
	}

//	private void checkConnection() {
//		if (this.tokenExpirationTime - new Date().getTime() <= SESSION_BUFFER * 1000) {
//			this.initConnection();
//		}
//	}
//
//	public CheckHoldingResult checkHolding(String oclcNumber) {
//		this.checkConnection();
//		try {
//			String url = SERVICE_URL + "/manage/institution/holdings/current?oclcNumbers=" + oclcNumber;
//			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(new HttpGet(url) {
//				{
//					setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
//					setHeader(HttpHeaders.ACCEPT, "application/json");
//				}
//			});
//			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
//				// log.log(Level.INFO, "Record was not found in OCLC: " + oclcNumber);
//				return new CheckHoldingResult(false, null);
//			} else if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//				Map<String, Object> result = new ObjectMapper()
//						.readValue(EntityUtils.toString(httpResponse.getEntity()), Map.class);
//				Map<String, Object> holdings = (Map<String, Object>) ((List<Object>) result.get("holdings")).get(0);
//				boolean isHoldingSet = (boolean) holdings.get("holdingSet");
//				String currentOclcNumber = (String) holdings.get("currentControlNumber");
//				if (currentOclcNumber == null || currentOclcNumber.isEmpty()) {
//					// log.log(Level.INFO, "Record was not found in OCLC: " + oclcNumber);
//					return new CheckHoldingResult(false, null);
//				} else if (!isHoldingSet) {
//					// log.log(Level.INFO, "Checked holdings for " + oclcNumber + ": Not set.");
//					return new CheckHoldingResult(false, currentOclcNumber);
//				} else {
//					if (currentOclcNumber.equals(oclcNumber)) {
//						// log.log(Level.INFO, "Checked holdings for " + oclcNumber + ": Set.");
//					} else {
//						// log.log(Level.INFO, "Checked holdings for " + oclcNumber + ": Set with new
//						// OCLC number: "
//						// + currentOclcNumber + ".");
//					}
//					return new CheckHoldingResult(true, currentOclcNumber);
//				}
//			} else {
////				log.log(Level.SEVERE, "Failed to check holdings for " + oclcNumber + ". Unexpected status code: "
////						+ httpResponse.getStatusLine().getStatusCode() + ".");
//				throw new RuntimeException("Failed to check holdings for " + oclcNumber + ". Unexpected status code: "
//						+ httpResponse.getStatusLine().getStatusCode() + ".");
//			}
//		} catch (Exception e) {
//			// log.log(Level.SEVERE, "Error when trying to check holdings for " + oclcNumber
//			// + ": " + e.getMessage());
//			throw new RuntimeException(e);
//		}
//	}

	// Other methods...

	public static class CheckHoldingResult {
		private final boolean isSet;
		private final String currentOclcNumber;

		public CheckHoldingResult(boolean isSet, String currentOclcNumber) {
			this.isSet = isSet;
			this.currentOclcNumber = currentOclcNumber;
		}

		// Getters...
	}
}
