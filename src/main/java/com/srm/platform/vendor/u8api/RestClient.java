package com.srm.platform.vendor.u8api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestClient {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private RestTemplate rest;
	private HttpHeaders headers;
	private HttpStatus status;

	public RestClient() {

		SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
		rf.setReadTimeout(0);
		rf.setConnectTimeout(0);
		this.rest = new RestTemplate(rf);

		this.headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "*/*");
	}

	public String get(String url) {
		logger.info(String.format("url=>%s", url));
		HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
		ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
		this.setStatus(responseEntity.getStatusCode());
		logger.info(String.format("response=>%s", responseEntity.getBody()));
		return responseEntity.getBody();
	}

	public String post(String url, String json) {
		HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
		ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.POST, requestEntity, String.class);
		this.setStatus(responseEntity.getStatusCode());

		logger.info(String.format("url=>%s", url));
		logger.info(String.format("post=>%s", json));
		logger.info(String.format("response=>%s", responseEntity.getBody()));

		return responseEntity.getBody();
	}

	public void put(String url, String json) {
		HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
		ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.PUT, requestEntity, String.class);
		this.setStatus(responseEntity.getStatusCode());
	}

	public void delete(String url) {
		HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
		ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
		this.setStatus(responseEntity.getStatusCode());
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}
}
