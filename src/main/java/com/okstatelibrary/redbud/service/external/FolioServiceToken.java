package com.okstatelibrary.redbud.service.external;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.DateUtil;

public class FolioServiceToken {

	private RestTemplate restTemplate = new RestTemplate();

	private static long currentTimeStap;

	private static long expireTimeStap;

	public static String authToken;

	public FolioServiceToken() {
		getToken();
	}

	private HttpHeaders getHttpHeaders() {

		HttpHeaders headers = new HttpHeaders();

		headers.add("x-okapi-tenant", AppSystemProperties.FolioTenant);

		return headers;

	}

	public String getToken() {

		com.okstatelibrary.redbud.folio.entity.User user = new com.okstatelibrary.redbud.folio.entity.User();

		user.username = AppSystemProperties.FolioUsername;

		user.password = AppSystemProperties.FolioPassword;

		HttpEntity<?> request = new HttpEntity<Object>(user, getHttpHeaders());

		try {

			if (authToken == null || DateUtil.getCurretTimeStamp() > expireTimeStap) {

				String url = AppSystemProperties.FolioURL + "authn/login-with-expiry";

				ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request,
						String.class);

				String[] vavlues = responseEntity.getHeaders().get("Set-Cookie").toString().split(";");

				for (String value : vavlues) {

					if (value.contains("[folioAccessToken=")) {

						String token = value.split("=")[1];

						authToken = token;

						currentTimeStap = DateUtil.getCurretTimeStamp();

						expireTimeStap = currentTimeStap + 600;

//						System.out.println("Get Token at : " + currentTimeStap);
//
//						System.out.println("Token is : " + token);

						return authToken;
					}
				}

			}

			return authToken;

		} catch (Exception e) {

			// System.out.println("updateLoan - " + payload.id);

			e.getMessage();
			e.printStackTrace();

			// printScreen("Folio Update User - " + payload.externalSystemId,
			// Constants.ErrorLevel.ERROR);

			// printScreen(e.getMessage(), Constants.ErrorLevel.ERROR);
//
			// System.out.println(e.getMessage());

			return null;
		}
	}

}
