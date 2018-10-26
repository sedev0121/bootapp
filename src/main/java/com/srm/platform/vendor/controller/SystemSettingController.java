package com.srm.platform.vendor.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.srm.platform.vendor.model.SystemConfig;

@Controller
@RequestMapping(path = "/config")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SystemSettingController extends CommonController {
	@GetMapping({ "/", "" })
	public String index(Model model, Principal principal) throws IOException {
		SystemConfig config = this.readConfig();
		model.addAttribute("config", config);
		return "admin/config/index";
	}
	
	@PostMapping("/update")
	public @ResponseBody Boolean list_ajax(@RequestParam Map<String, String> requestParams) throws FileNotFoundException, IOException {
		String pwdMinLength = requestParams.get("minpassword");
		
		String mailHostAddress = requestParams.get("mailhost");
		String mailAccount = requestParams.get("mailuser");
		String mailPassword = requestParams.get("mailpassword");
		String sessionTimeout = requestParams.get("sessiontimeout");
		
		String smsHost = requestParams.get("smshost");
		String smsUser = requestParams.get("smsuser");
		String smsPassword = requestParams.get("smspassword");
		
		FileInputStream in = new FileInputStream("./application.properties");
		Properties props = new Properties();
		props.load(in);
		in.close();

		FileOutputStream out = new FileOutputStream("./application.properties");
		props.setProperty("srm.password.minlength", pwdMinLength);
		
		props.setProperty("spring.mail.host", mailHostAddress);
		props.setProperty("spring.mail.username", mailAccount);
		props.setProperty("spring.mail.password", mailPassword);
		
		props.setProperty("server.servlet.session.timeout", sessionTimeout);
		
		props.setProperty("srm.sms.url", smsHost);
		props.setProperty("srm.sms.id", smsUser);
		props.setProperty("srm.sms.pwd", smsPassword);
		
		// props.setProperty("spring.sms.url", value)
		
		props.setProperty("server.servlet.session.timeout", sessionTimeout);
		props.store(out, null);
		out.close();
		
		return true;
	}
	
	private SystemConfig readConfig() throws FileNotFoundException, IOException  {
		SystemConfig config = new SystemConfig();
		
		FileInputStream in = new FileInputStream("./application.properties");
		Properties props = new Properties();
		props.load(in);
		in.close();
		
		config.minpassword = props.getProperty("srm.password.minlength");
		config.mailhost = props.getProperty("spring.mail.host");
		config.mailuser = props.getProperty("spring.mail.username");
		config.mailpassword = props.getProperty("spring.mail.password");
		config.sessiontimeout = props.getProperty("server.servlet.session.timeout");
		config.smshost = props.getProperty("srm.sms.url");
		config.smsuser = props.getProperty("srm.sms.id");
		config.smspassword = props.getProperty("srm.sms.pwd");
		
		return config;
	}
	
	/*
	@GetMapping ("/")
	public @ResponseBody Boolean checkPasswordMin_ajax()
	*/
}
