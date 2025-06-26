package com.okstatelibrary.redbud.service.external;

import java.net.URI;
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
		setToken();
	}

	/**
	 * scheduler
	 */
	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	/**
	 *  set the token and check every 9  minutes 
	 */
	public void setToken() {

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
	 */
	public void setTokens() {

		System.out.println("Get FOLIO Token at : " + DateUtil.getTodayDateAndTime());

		com.okstatelibrary.redbud.folio.entity.User user = new com.okstatelibrary.redbud.folio.entity.User();

		user.username = AppSystemProperties.FolioUsername;

		user.password = AppSystemProperties.FolioPassword;

		HttpEntity<?> request = new HttpEntity<Object>(user, getHttpHeaders());

		try {

			URI uri = new URI(AppSystemProperties.FolioURL + "authn/login-with-expiry");
						
			ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

			String[] vavlues = responseEntity.getHeaders().get("Set-Cookie").toString().split(";");

			for (String value : vavlues) {

				if (value.contains("[folioAccessToken=")) {

					String token = value.split("=")[1];

					authToken = token;

				}
			}

		} catch (Exception e) {

			e.getMessage();
			e.printStackTrace();

		}
	}

}
