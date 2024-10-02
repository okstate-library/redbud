package com.okstatelibrary.redbud.service.external;

import com.okstatelibrary.redbud.util.DateUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.okstatelibrary.redbud.util.AppSystemProperties;

public class OCLCServiceToken {

	private static long currentTimeStap;

	private static long expireTimeStap;

	private static String authToken;

	public OCLCServiceToken() throws ClientProtocolException, IOException {

//		try {
//			//getToken();
//		} catch (InterruptedException e) {
//			Thread.currentThread().interrupt();
//		}
	}

	private HttpPost getHttpPost() throws UnsupportedEncodingException {

		// OCLC OAuth 2.0 token endpoint
		String tokenEndpoint = AppSystemProperties.OclcTokenEndpoint;

		String wskey = AppSystemProperties.OclcKey;

		String wskeySecret = AppSystemProperties.OclcKeySecret;

		String scope = AppSystemProperties.OclcScope;

		HttpPost httpPost = new HttpPost(tokenEndpoint);

		// Set WSKey credentials
		httpPost.addHeader("Authorization",
				"Basic " + java.util.Base64.getEncoder().encodeToString((wskey + ":" + wskeySecret).getBytes()));

		// Set request parameters
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

		String requestBody = "grant_type=client_credentials&scope=" + scope;

		httpPost.setEntity(new StringEntity(requestBody));

		// Execute the request
		return httpPost;
	}

	public String getToken() throws ClientProtocolException, IOException, InterruptedException {

		if (authToken == null || DateUtil.getCurretTimeStamp() > expireTimeStap) {

			Thread.sleep(2000);

			System.out.println("Get OCLC Token at : " + DateUtil.getTodayDateAndTime());

			// Create HTTP client
			try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

				// Execute the request
				HttpResponse response = httpClient.execute(getHttpPost());

				// Parse the response
				HttpEntity entity = response.getEntity();

				if (entity != null) {

					try (BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(entity.getContent()))) {
						String line;

						StringBuilder result = new StringBuilder();

						while ((line = bufferedReader.readLine()) != null) {
							result.append(line);

							// System.out.println("Response: " + line);
						}

						// System.out.println("Response: " + result.toString());

						String[] vavlues = result.toString().split(",");

						for (String value : vavlues) {

							if (value.contains("access_token")) {

								String token = value.split(":")[1];

								String replaceToken = token.replace("\"", "");

								// System.out.println("Get Token at : " + replaceToken);

								authToken = replaceToken;

								currentTimeStap = DateUtil.getCurretTimeStamp();

								expireTimeStap = currentTimeStap + 1200;

//								System.out.println("Get Token at : " + currentTimeStap);
//
//								System.out.println("Token is : " + token);

							} else {
								return null;
							}

						}
					}
				} else {
					return null;
				}

			}
		}

		return authToken;
	}

}
