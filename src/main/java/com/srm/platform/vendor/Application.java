package com.srm.platform.vendor;

import java.util.Date;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Chongqing")); // It will set UTC timezone
		System.out.println("Spring boot application running in UTC timezone :" + new Date()); // It will print UTC
																								// timezone
	}

	public static void main(String[] args) throws Throwable {
		SpringApplication.run(Application.class, args);
	}

}
