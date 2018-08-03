package com.srm.platform.vendor.u8api;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

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

	public String sendSMS(String toPhoneNumber, String message) {

		String url = appProperties.getSystem().getSms();
		url += "&phone=" + toPhoneNumber + "&msg=" + message;
		RestClient client = new RestClient();
		String response = client.get(url);

		return response;

	}

	public String getLinkU8BatchWeiwai(Map<String, String> requestParams) {
		String url = appProperties.getLinku8().getBatch_get_weiwai() + getUrlSuffix(requestParams);
		return get(url);
	}

	public String getLinkU8BatchBasic(Map<String, String> requestParams) {
		String url = appProperties.getLinku8().getBatch_get() + getUrlSuffix(requestParams);
		return get(url);
	}

	public String generatePurchaseInvoice(Map<String, String> getParams, String json) {
		String url = appProperties.getPurInvoice().getAdd() + getUrlSuffix(getParams);
		return post(url, json);
	}

	public String generateVenpriceadjust(Map<String, String> getParams, String json) {
		String url = appProperties.getVenPriceAdjust().getAdd() + getUrlSuffix(getParams);
		return post(url, json);
	}

	public String getBatchVendor(Map<String, String> requestParams) {
		String url = appProperties.getVendor().getBatch_get() + getUrlSuffix(requestParams);
		return get(url);
	}

	public String getBatchMeasurementUnit(Map<String, String> requestParams) {
		String url = appProperties.getMeasurementUnit().getBatch_get() + getUrlSuffix(requestParams);
		return get(url);
	}

	public String getBatchInventoryClass(Map<String, String> requestParams) {
		String url = appProperties.getInventoryClass().getBatch_get() + getUrlSuffix(requestParams);
		return get(url);
	}

	public String getBatchInventory(Map<String, String> requestParams) {
		String url = appProperties.getInventory().getBatch_get() + getUrlSuffix(requestParams);
		return get(url);
	}

	public String getBatchPurchaseOrder(Map<String, String> requestParams) {
		String url = appProperties.getPurchaseOrder().getBatch_get() + getUrlSuffix(requestParams);
		return get(url);
	}

	public String getPurchaseOrder(Map<String, String> requestParams) {
		String url = appProperties.getPurchaseOrder().getGet() + getUrlSuffix(requestParams);
		return get(url);
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

	private String checkToken() {

		Long tokenExpireTime = (Long) httpSession.getAttribute("token_expire_time");
		token_id = (String) httpSession.getAttribute("token_id");

		logger.info("session expire=" + tokenExpireTime + " token_id=" + token_id);

		if (tokenExpireTime != null)
			logger.info("session remain seconds=" + (tokenExpireTime - System.currentTimeMillis()) / 1000);

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

		ObjectMapper objectMapper = new ObjectMapper();

		String url = appProperties.getSystem().getToken();

		try {
			TokenResponse response = objectMapper.readValue(new URL(url), TokenResponse.class);

			if (response.getErrcode() == appProperties.getError_code_success())
				return response.getToken();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
		}

		return null;

	}

	private String getUrlSuffix(Map<String, String> requestParams) {
		checkToken();
		requestParams.put("token", token_id);
		String urlSuffix = "";
		for (Entry<String, String> entry : requestParams.entrySet()) {
			urlSuffix += "&" + entry.getKey() + "=" + entry.getValue();
		}
		return urlSuffix;
	}

	private String get(String url) {
		httpSession.setMaxInactiveInterval(0);
		RestClient client = new RestClient();
		String response = client.get(url);
		return response;
	}

	private String post(String url, String postJson) {
		RestClient client = new RestClient();
		String response = client.post(url, postJson);
		return response;
	}

}
