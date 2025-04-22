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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.okstatelibrary.redbud.util.AppSystemProperties;

/*
 * 
 */
public class OCLCServiceToken {

	public static String authToken;

	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public OCLCServiceToken() throws ClientProtocolException, IOException {

	}

	public void setToken() {

		scheduler.scheduleAtFixedRate(() -> {
			try {
				getToken();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}, 0, 18, TimeUnit.MINUTES);
	}

	public void dropToken() {
		scheduler.shutdown();
	}

	private static HttpPost getHttpPost() throws UnsupportedEncodingException {

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

	/*
	 * 
	 */
	public static void getToken() throws ClientProtocolException, IOException, InterruptedException {

		System.out.println("Get OCLC Token at : " + DateUtil.getTodayDateAndTime());

		// Create HTTP client
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

			// Execute the request
			HttpResponse response = httpClient.execute(getHttpPost());

			// Parse the response
			HttpEntity entity = response.getEntity();

			if (entity != null) {

				try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
					String line;

					StringBuilder result = new StringBuilder();

					while ((line = bufferedReader.readLine()) != null) {
						result.append(line);
					}

					String[] vavlues = result.toString().split(",");

					for (String value : vavlues) {

						if (value.contains("access_token")) {

							String token = value.split(":")[1];

							String replaceToken = token.replace("\"", "");

							authToken = replaceToken;

						}

					}
				}
			}

		}

	}

}
