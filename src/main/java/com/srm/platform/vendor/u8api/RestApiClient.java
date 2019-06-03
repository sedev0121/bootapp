package com.srm.platform.vendor.u8api;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.utility.Constants;

@Component
@ComponentScan(basePackageClasses = AppProperties.class)
@EnableConfigurationProperties({ AppProperties.class })
public class RestApiClient {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AppProperties appProperties;

	@Autowired
	private HttpSession httpSession;

	@Value("${srm.sms.api}")
	private String smsAPI;

	private String token_id = null;

	public String sendSMS(String toPhoneNumber, String message) {

		String url = String.format(smsAPI, toPhoneNumber, message);
		RestClient client = new RestClient();
		String response = client.get(url);

		return response;

	}

	public RestApiResponse postForInventoryClass() {
		return postForData("SRM_inventoryclass", "inventoryclass");
	}

	public RestApiResponse postConfirmForInventoryClass(List<String> codes) {
		return postForConfirm("SRM_inventoryclass", codes);
	}

	public RestApiResponse postForInventory() {
		return postForData("SRM_inventory", "inventory");
	}
	
	public RestApiResponse postConfirmForInventory(List<String> codes) {
		return postForConfirm("SRM_inventory", codes);
	}

	public RestApiResponse postForVendorClass() {
		return postForData("SRM_vendorclass", "vendorclass");
	}
	
	public RestApiResponse postConfirmForVendorClass(List<String> codes) {
		return postForConfirm("SRM_vendorclass", codes);
	}

	public RestApiResponse postForVendor() {
		return postForData("SRM_vendor", "vendor");
	}
	
	public RestApiResponse postConfirmForVendor(List<String> codes) {
		return postForConfirm("SRM_vendor", codes);
	}
	
	public RestApiResponse postForOrder() {
		return postForData("SRM_pomo", "pomo");
	}
	
	public RestApiResponse postConfirmForOrder(List<String> pocodes, List<String> mocodes) {
		Map<String, List<String>> content = new HashMap<>();
		content.put("pocodes", pocodes);
		content.put("mocodes", mocodes);
		
		return postForConfirm("SRM_pomo", content);
	}
	
	public RestApiResponse postForArrivalVouch(Map<String, Object> content) {
		
		String url = appProperties.getData_url();

		Map<String, Object> postData = new HashMap<>();
		postData.put("classname", "SRM_ArrivalVouch");
		postData.put("method", "createArrivalVouch");
		postData.put("content", content);

		return post(url, postData, null, true);
		
	}
	
	public RestApiResponse getBoxMsg(Map<String, String> content) {
		
		String url = appProperties.getData_url();

		Map<String, Object> postData = new HashMap<>();
		postData.put("classname", "SRM_Box");
		postData.put("method", "getBoxMsg");
		postData.put("content", content);

		return post(url, postData, null, true);
		
	}

	private RestApiResponse postForData(String classname, String dataField) {
		String url = appProperties.getData_url();

		Map<String, Object> postData = new HashMap<>();
		postData.put("classname", classname);
		postData.put("method", "batch_get");
		postData.put("content", null);

		return post(url, postData, dataField, true);
	}
	
	private RestApiResponse postForConfirm(String classname, List<String> codes) {
		if (Constants.TEST) {
			return null;
		}
		
		String url = appProperties.getData_url();

		Map<String, Object> postData = new HashMap<>();

		Map<String, Object> content = new HashMap<>();
		content.put("codes", codes);

		postData.put("classname", classname);
		postData.put("method", "feedback");
		postData.put("content", content);

		return post(url, postData, null, true);
	}
	
	private RestApiResponse postForConfirm(String classname, Map<String, List<String>> content) {
		if (Constants.TEST) {
			return null;
		}
		
		String url = appProperties.getData_url();

		Map<String, Object> postData = new HashMap<>();

		postData.put("classname", classname);
		postData.put("method", "feedback");
		postData.put("content", content);

		return post(url, postData, null, true);
	}
	
	private RestApiResponse post(String url, Map<String, Object> postData, String dataField, boolean checkToken) {
		if (checkToken) {
			checkToken();
		}

		RestClient client = new RestClient();
		String postJson = createPostJson(postData, checkToken);
		String responseString = client.post(url, postJson);
		return parseResponseJson(responseString, dataField);
	}

	private String createPostJson(Map<String, Object> postData, boolean checkToken) {
		String jsonString = "{}";
		if (checkToken) {
			postData.put("accesstoken", token_id);
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			jsonString = objectMapper.writeValueAsString(postData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return jsonString;
	}

	private RestApiResponse parseResponseJson(String jsonString, String dataField) {

		RestApiResponse response = new RestApiResponse();

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> map = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
			});

			response.setOriginalMap(map);
			response.setStatus(String.valueOf(map.get("status")));
			response.setErrmsg(String.valueOf(map.get("errmsg")));

			if (map.get("errmsg") == null && map.get("msg") != null) {
				response.setErrmsg(String.valueOf(map.get("msg")));
			}
			
			if (dataField != null) {
				List<LinkedHashMap<String, Object>> dataList = (List<LinkedHashMap<String, Object>>) map.get(dataField);
				response.setData(dataList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	private String checkToken() {

		token_id = (String) httpSession.getAttribute("token_id");

		if (token_id == null) {
			String token = getToken();

			if (token != null) {
				token_id = token;
				httpSession.setAttribute("token_id", token_id);
			}
		}

		return token_id;
	}

	private String getToken() {

		String token = null;
		String url = appProperties.getToken_url();

		Map<String, Object> postData = new HashMap<>();
		postData.put("from_account", appProperties.getAccount());

		RestApiResponse response = post(url, postData, null, false);

		token = response.getValue("token");

		return token;

	}
}
