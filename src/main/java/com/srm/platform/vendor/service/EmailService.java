package com.srm.platform.vendor.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Value("${spring.mail.username}")
	private String from;

	public void sendEmail(SimpleMailMessage mail, Map<String, Object> model) {
		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());

			Context context = new Context();
			context.setVariables(model);
			String html = templateEngine.process("email/resetpassword", context);

			helper.setTo(mail.getTo());
			helper.setText(html, true);
			helper.setSubject(mail.getSubject());
			helper.setFrom(from);

			emailSender.send(message);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void sendSyncErrorEmail(String syncName, Map<String, Object> model) {
		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());

			Context context = new Context();
			context.setVariables(model);
			String html = templateEngine.process("email/error", context);

			helper.setTo("w249043358@126.com");			
			helper.setText(html, true);
			helper.setSubject(syncName + "同步错误");
			helper.setFrom(from);

			emailSender.send(message);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}