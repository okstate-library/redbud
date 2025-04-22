package com.okstatelibrary.redbud.service.external;

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

	private RestTemplate restTemplate = new RestTemplate();

	public static String authToken;

	public FolioServiceToken() {
		setToken();
	}

	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public void setToken() {

		scheduler.scheduleAtFixedRate(() -> {
			setTokens();
		}, 0, 18, TimeUnit.MINUTES);
	}

	private HttpHeaders getHttpHeaders() {

		HttpHeaders headers = new HttpHeaders();

		headers.add("x-okapi-tenant", AppSystemProperties.FolioTenant);

		return headers;

	}

	public void setTokens() {

		System.out.println("Get FOLIO Token at : " + DateUtil.getTodayDateAndTime());
		
		com.okstatelibrary.redbud.folio.entity.User user = new com.okstatelibrary.redbud.folio.entity.User();

		user.username = AppSystemProperties.FolioUsername;

		user.password = AppSystemProperties.FolioPassword;

		HttpEntity<?> request = new HttpEntity<Object>(user, getHttpHeaders());

		try {

			String url = AppSystemProperties.FolioURL + "authn/login-with-expiry";

			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

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
