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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.repository.AccountRepository;

@Component
@ComponentScan(basePackageClasses = AppProperties.class)
@EnableConfigurationProperties({ AppProperties.class })
public class ApiClient {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AppProperties appProperties;

	@Autowired
	private AccountRepository accountRepository;

	private String to_account = null;
	private String token_id = null;

	public ApiClient() {
		disableCertificateValidation();
		// checkToken();
	}

	private String checkToken() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		Account account = accountRepository.findOneByUsername(username);

		logger.info(account.getExpire_time() + ">" + System.currentTimeMillis());
		if (account.getToken_id() == null || System.currentTimeMillis() >= account.getExpire_time()) {
			Token token = getToken();

			if (token != null) {
				account.setToken_id(token.getId());
				account.setExpire_time(System.currentTimeMillis() + token.getExpiresIn() * 1000);
				accountRepository.save(account);
			}

		}

		token_id = account.getToken_id();
		to_account = account.getTo_account();

		return account.getToken_id();
	}

	private String getErrorMsg(String responseStr) {
		logger.info("start getToken api");
		ObjectMapper objectMapper = new ObjectMapper();

		String url = appProperties.getSystem().getToken();

		// String url = env.getProperty("u8api.system_token");

		try {
			Response response = objectMapper.readValue(responseStr, Response.class);
			logger.info(response.getErrmsg());

			if (response.getErrcode() != appProperties.getError_code_success())
				return response.getErrmsg();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
		}

		return null;
	}

	private Token getToken() {
		logger.info("start getToken api");
		ObjectMapper objectMapper = new ObjectMapper();

		String url = appProperties.getSystem().getToken();

		// String url = env.getProperty("u8api.system_token");

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
		String url = String.format(appProperties.getVendor().getGet(), to_account, token_id, id);
		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));

		return response;

	}

	public String getBatchVendor(Map<String, String> requestParams) {

		checkToken();

		String rows_per_page = requestParams.getOrDefault("rows_per_page", "10");
		String page_index = requestParams.getOrDefault("page_index", "1");
		String ds_sequence = requestParams.getOrDefault("ds_sequence", "1");
		String name = requestParams.getOrDefault("name", "");

		logger.info("start getBatchVendor api");
		RestClient client = new RestClient();
		String url = String.format(appProperties.getVendor().getBatch_get(), to_account, token_id, rows_per_page,
				page_index, ds_sequence, name);
		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	public String getBatchVenPriceAdjust(Map<String, String> requestParams) {

		checkToken();

		String rows_per_page = requestParams.getOrDefault("rows_per_page", "10");
		String page_index = requestParams.getOrDefault("page_index", "1");
		String ds_sequence = requestParams.getOrDefault("ds_sequence", "1");
		String personname = requestParams.getOrDefault("personname", "");

		logger.info("start getBatchVenPriceAdjust api");
		RestClient client = new RestClient();
		String url = String.format(appProperties.getVenPriceAdjust().getBatch_get(), to_account, token_id,
				rows_per_page, page_index, ds_sequence, personname);
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
		String ds_sequence = requestParams.getOrDefault("ds_sequence", "1");

		RestClient client = new RestClient();
		String url = String.format(appProperties.getInventory().getBatch_get(), to_account, token_id, rows_per_page,
				page_index, ds_sequence);
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
		String ds_sequence = requestParams.getOrDefault("ds_sequence", "1");

		RestClient client = new RestClient();
		String url = String.format(appProperties.getPurchaseIn().getBatch_get(), to_account, token_id, rows_per_page,
				page_index, ds_sequence);
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
		String ds_sequence = requestParams.getOrDefault("ds_sequence", "1");

		RestClient client = new RestClient();
		String url = String.format(appProperties.getPurInvoice().getBatch_get(), to_account, token_id, rows_per_page,
				page_index, ds_sequence);
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
		String ds_sequence = requestParams.getOrDefault("ds_sequence", "1");

		RestClient client = new RestClient();
		String url = String.format(appProperties.getPurchaseOrder().getBatch_get(), to_account, token_id, rows_per_page,
				page_index, ds_sequence);
		logger.info(String.format("url=>%s", url));
		String response = client.get(url);
		logger.info(String.format("response=>%s", response));
		return response;
	}

	public String getPurchaseOrder(String id) {

		checkToken();
		logger.info("start getPurchaseOrder api");
		RestClient client = new RestClient();
		String url = String.format(appProperties.getPurchaseOrder().getGet(), to_account, token_id, id);
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
