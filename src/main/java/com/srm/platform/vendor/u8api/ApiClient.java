package com.srm.platform.vendor.u8api;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@ComponentScan(basePackageClasses = AppProperties.class)
@EnableConfigurationProperties({ AppProperties.class })
public class ApiClient {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AppProperties appProperties;

	@Autowired
	private HttpSession httpSession;

	private String token_id = null;

	public ApiClient() {
		disableCertificateValidation();
		// checkToken();
	}

	private String checkToken() {

		Long tokenExpireTime = (Long) httpSession.getAttribute("token_expire_time");
		token_id = (String) httpSession.getAttribute("token_id");

		logger.info("session expire=" + tokenExpireTime + " token_id=" + token_id);
		if (tokenExpireTime == null || token_id == null || System.currentTimeMillis() >= tokenExpireTime) {
			Token token = getToken();

			logger.info("new token=" + token.getId() + " expire=" + token.getExpiresIn());
			if (token != null) {
				token_id = token.getId();
				httpSession.setAttribute("token_expire_time", System.currentTimeMillis() + token.getExpiresIn() * 1000);
				httpSession.setAttribute("token_id", token_id);
			}

		}

		return token_id;
	}

	private Token getToken() {
		logger.info("=====================start getToken api");
		ObjectMapper objectMapper = new ObjectMapper();

		String url = appProperties.getSystem().getToken();

		try {
			TokenResponse response = objectMapper.readValue(new URL(url), TokenResponse.class);
			logger.info(response.getErrmsg());

			if (response.getErrcode() == appProperties.getError_code_success())
				return response.getToken();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
		}

		return null;

	}

	public String getVendor(String id) {

		checkToken();
		logger.info("start getVendor api");
		RestClient client = new RestClient();
		logger.info(appProperties.getVendor().getGet());
		String url = String.format(appProperties.getVendor().getGet(), token_id, id);
		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));

		return response;

	}

	public String getLinkU8BatchWeiwai(Map<String, String> requestParams) {

		String rows_per_page = requestParams.getOrDefault("rows_per_page", "10");
		String page_index = requestParams.getOrDefault("page_index", "1");
		String vendorCode = requestParams.getOrDefault("vendor_code", null);
		String startDate = requestParams.getOrDefault("start_date", null);

		RestClient client = new RestClient();
		String url = String.format(appProperties.getLinku8().getBatch_get_weiwai(), rows_per_page, page_index);

		if (vendorCode != null) {
			url += "&vendorcode=" + vendorCode;
		}

		if (startDate != null) {
			url += "&cChangAuditTime_begin=" + startDate + "&auditdate_begin=" + startDate;
		}

		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	public String getLinkU8BatchBasic(Map<String, String> requestParams) {

		String rows_per_page = requestParams.getOrDefault("rows_per_page", "10");
		String page_index = requestParams.getOrDefault("page_index", "1");
		String vendorCode = requestParams.getOrDefault("vendor_code", null);
		String startDate = requestParams.getOrDefault("start_date", null);

		RestClient client = new RestClient();
		String url = String.format(appProperties.getLinku8().getBatch_get(), rows_per_page, page_index);

		if (vendorCode != null) {
			url += "&vendorcode=" + vendorCode;
		}

		if (startDate != null) {
			url += "&cChangAuditTime_begin=" + startDate + "&auditdate_begin=" + startDate;
		}

		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	public String getBatchVendor(Map<String, String> requestParams) {

		checkToken();

		String rows_per_page = requestParams.getOrDefault("rows_per_page", "10");
		String page_index = requestParams.getOrDefault("page_index", "1");
		String name = requestParams.getOrDefault("name", "");

		logger.info("start getBatchVendor api");
		RestClient client = new RestClient();
		String url = String.format(appProperties.getVendor().getBatch_get(), token_id, rows_per_page, page_index, name);
		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	public String getBatchMeasurementUnit(Map<String, String> requestParams) {

		checkToken();

		String rows_per_page = requestParams.getOrDefault("rows_per_page", "10");
		String page_index = requestParams.getOrDefault("page_index", "1");
		String name = requestParams.getOrDefault("name", "");

		logger.info("start getBatchVendor api");
		RestClient client = new RestClient();
		String url = String.format(appProperties.getMeasurementUnit().getBatch_get(), token_id, rows_per_page,
				page_index, name);
		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	public String getBatchInventoryClass(Map<String, String> requestParams) {

		checkToken();

		String rows_per_page = requestParams.getOrDefault("rows_per_page", "10");
		String page_index = requestParams.getOrDefault("page_index", "1");
		String name = requestParams.getOrDefault("name", "");

		logger.info("start getBatchVendor api");
		RestClient client = new RestClient();
		String url = String.format(appProperties.getInventoryClass().getBatch_get(), token_id, rows_per_page,
				page_index, name);
		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	public String getBatchVenPriceAdjust(Map<String, String> requestParams) {

		checkToken();

		String rows_per_page = requestParams.getOrDefault("rows_per_page", "10");
		String page_index = requestParams.getOrDefault("page_index", "1");
		String personname = requestParams.getOrDefault("personname", "");

		logger.info("start getBatchVenPriceAdjust api");
		RestClient client = new RestClient();
		String url = String.format(appProperties.getVenPriceAdjust().getBatch_get(), token_id, rows_per_page,
				page_index, personname);
		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	public String getBatchInventory(Map<String, String> requestParams) {

		checkToken();

		logger.info("start getBatchInventory api");
		String rows_per_page = requestParams.getOrDefault("rows_per_page", "10");
		String page_index = requestParams.getOrDefault("page_index", "1");

		RestClient client = new RestClient();
		String url = String.format(appProperties.getInventory().getBatch_get(), token_id, rows_per_page, page_index);
		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	public String getBatchPurchaseIn(@RequestParam Map<String, String> requestParams) {

		checkToken();
		logger.info("start getBatchPurchaseIn api");
		String rows_per_page = requestParams.getOrDefault("rows_per_page", "10");
		String page_index = requestParams.getOrDefault("page_index", "1");

		RestClient client = new RestClient();
		String url = String.format(appProperties.getPurchaseIn().getBatch_get(), token_id, rows_per_page, page_index);
		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	public String getBatchPurInvoice(@RequestParam Map<String, String> requestParams) {

		checkToken();
		logger.info("start getBatchPurInvoice api");
		String rows_per_page = requestParams.getOrDefault("rows_per_page", "10");
		String page_index = requestParams.getOrDefault("page_index", "1");

		RestClient client = new RestClient();
		String url = String.format(appProperties.getPurInvoice().getBatch_get(), token_id, rows_per_page, page_index);
		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	public String getBatchPurchaseOrder(@RequestParam Map<String, String> requestParams) {

		checkToken();
		logger.info("start getBatchPurchaseOrder api");
		String rows_per_page = requestParams.getOrDefault("rows_per_page", "10");
		String page_index = requestParams.getOrDefault("page_index", "1");
		String date_begin = requestParams.getOrDefault("date_begin", null);

		RestClient client = new RestClient();
		String url = String.format(appProperties.getPurchaseOrder().getBatch_get(), token_id, rows_per_page,
				page_index);
		if (date_begin != null)
			url += "&date_begin=" + date_begin;

		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	public String getPurchaseOrder(String id) {

		checkToken();
		logger.info("start getPurchaseOrder api");
		RestClient client = new RestClient();
		String url = String.format(appProperties.getPurchaseOrder().getGet(), token_id, id);
		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	public String getPurchaseIn(String code) {

		checkToken();
		logger.info("start getPurchaseIn api");
		RestClient client = new RestClient();
		String url = String.format(appProperties.getPurchaseIn().getGet(), token_id, code);
		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	private void disableCertificateValidation() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		// Ignore differences between given hostname and certificate hostname
		HostnameVerifier hv = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception e) {
		}
	}

}
