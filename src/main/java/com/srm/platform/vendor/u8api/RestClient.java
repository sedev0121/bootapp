package com.srm.platform.vendor.u8api;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

	public String getGB2312(String url) {
		logger.info(String.format("url=>%s", url));
		rest.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("GB2312")));
		ResponseEntity<String> responseEntity = rest.getForEntity(url, String.class);
		this.setStatus(responseEntity.getStatusCode());
		logger.info(String.format("response=>%s", responseEntity.getBody()));
		return responseEntity.getBody();
	}

	public String post(String url, String json) {
		logger.info(String.format("post=>%s", json));

		rest.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
		ResponseEntity<String> responseEntity = rest.postForEntity(url, requestEntity, String.class);
		this.setStatus(responseEntity.getStatusCode());

		logger.info(String.format("response=>%s", responseEntity.getBody()));

		return responseEntity.getBody();
	}
	

	public String postGB2312(String url, String json) {
		logger.info(String.format("GB2312 url=>%s", url));
		logger.info(String.format("post=>%s", json));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("StrJson", json);

		rest.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("GB2312")));
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);
		ResponseEntity<String> responseEntity = rest.postForEntity(url, requestEntity, String.class);
		this.setStatus(responseEntity.getStatusCode());

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
