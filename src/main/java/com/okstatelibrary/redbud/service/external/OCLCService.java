package com.okstatelibrary.redbud.service.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.okstatelibrary.redbud.oclc.entity.HoldingRoot;
import com.okstatelibrary.redbud.util.DateUtil;

@Service
public class OCLCService extends OCLCServiceToken {

	public OCLCService() throws ClientProtocolException, IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(OCLCService.class);

	private HttpGet getHttpGet(String url) throws ClientProtocolException, IOException, InterruptedException {

		// Create HTTP POST request to obtain token
		HttpGet httpGet = new HttpGet(url);

		httpGet.addHeader("Authorization", "Bearer " + getToken());

		httpGet.addHeader("Accept", "application/json");

		return httpGet;

	}

	private HttpPost getHttpPost(String url) throws ClientProtocolException, IOException, InterruptedException {

		// Create HTTP POST request to obtain token
		HttpPost httpPost = new HttpPost(url);

		httpPost.addHeader("Authorization", "Bearer " + getToken());

		httpPost.addHeader("Accept", "application/json");

		return httpPost;

	}

	public HoldingRoot getOCLCItems(String oclcNumbers)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

			String queryString = "?oclcNumbers=" + oclcNumbers;

			String url = "https://metadata.api.oclc.org/worldcat/manage/institution/holdings/current"
					+ queryString.trim();

			// Execute the request
			HttpResponse response = httpClient.execute(getHttpGet(url));

			// Parse the response
			HttpEntity entity = response.getEntity();

			if (entity != null) {

				try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()))) {

					String line;

					StringBuilder result = new StringBuilder();

					while ((line = bufferedReader.readLine()) != null) {

						result.append(line);

					}

					Gson gson = new Gson();

					if (result.toString().contains("Unauthorized")) {

						System.out.println("oclcNumber :  " + oclcNumbers + " result : " + result.toString());

						System.out.println("Unauthorized " + DateUtil.getTodayDateAndTime());
					}

					return gson.fromJson(result.toString(), HoldingRoot.class);

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
//			e.getMessage();
			e.printStackTrace();
			// return null;

			System.out.println("Invalid number " + oclcNumbers);
		}

		return null;

	}

	public String setOCLCItems(String oclcNumber)
			throws JsonParseException, JsonMappingException, RestClientException, IOException, InterruptedException {

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

			String url = "https://metadata.api.oclc.org/worldcat/manage/institution/holdings/" + oclcNumber + "/set";

			// Create a POST request
			HttpPost httpPost = getHttpPost(url);

			// Execute the POST request and get the response
			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {

				// Print the status code
				// System.out.println("Response Status: " +
				// response.getStatusLine().getStatusCode());

				// Get the response body as a string
				return response.getStatusLine().getStatusCode() + "";

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	public String unSetOCLCItems(String oclcNumber)
			throws JsonParseException, JsonMappingException, RestClientException, IOException, InterruptedException {

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

			String url = "https://metadata.api.oclc.org/worldcat/manage/institution/holdings/" + oclcNumber + "/unset";

			// Create a POST request
			HttpPost httpPost = getHttpPost(url);

			// Execute the POST request and get the response
			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				// Print the status code
				// System.out.println("Response Status: " +
				// response.getStatusLine().getStatusCode());

				// Get the response body as a string
				return response.getStatusLine().getStatusCode() + "";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

}
