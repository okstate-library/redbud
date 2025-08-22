package com.okstatelibrary.redbud.service.external;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.DateUtil;

public class FolioServiceToken {

	/**
	 * 
	 */
	private RestTemplate restTemplate = new RestTemplate();

	/**
	 * 
	 */
	public static String authToken;

	/**
	 * construct method
	 */
	public FolioServiceToken() {
		getToken();
	}

	/**
	 * scheduler
	 */
	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	/**
	 * set the token and check every 9 minutes
	 */
	public void getToken() {

		scheduler.scheduleAtFixedRate(() -> {
			setTokens();

		}, 0, 9, TimeUnit.MINUTES);
	}

	/**
	 * get the relevant http headers relevant to the FOLIO tenant
	 * 
	 * @return http headers
	 */
	private HttpHeaders getHttpHeaders() {

		HttpHeaders headers = new HttpHeaders();

		headers.add("x-okapi-tenant", AppSystemProperties.FolioTenant);

		return headers;

	}

	/**
	 * sets the token by accessing the FOLIO Api and save it in the variable.
	 * 
	 * @throws URISyntaxException
	 */
	public void setTokens() {

		int loopCount = 1;

		boolean success = false;

		while (!success) {

			com.okstatelibrary.redbud.folio.entity.User user = new com.okstatelibrary.redbud.folio.entity.User();

			user.username = AppSystemProperties.FolioUsername;

			user.password = AppSystemProperties.FolioPassword;

			HttpEntity<?> request = new HttpEntity<Object>(user, getHttpHeaders());

			try {

				//System.out.println("Loop count " + loopCount);

				loopCount++;

				//URI uri = new URI(AppSystemProperties.FolioURL + "/authn/login-with-expiry");

				 URI uri = new
				 URI("https://okapi-okstate.folio.ebsco.com/authn/login-with-expiry");

				ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, request,
						String.class);

				String[] vavlues = responseEntity.getHeaders().get("Set-Cookie").toString().split(";");

				for (String value : vavlues) {

					if (value.contains("[folioAccessToken=")) {

						String token = value.split("=")[1];

						authToken = token;

						//System.out.println("Get FOLIO Token at : " + DateUtil.getTodayDateAndTime());

						success = true; // If no exception, mark success
					}
				}

			} catch (Exception e) {

				e.getMessage();
				e.printStackTrace();

			}

		}
	}

}
