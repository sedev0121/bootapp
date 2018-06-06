package com.srm.platform.vendor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	private static final String VIEWS = "/WEB-INF/views/";
	private static final String ASSETS_LOCATION = "/assets/";
	private static final String ASSETS_HANDLER = ASSETS_LOCATION + "**";
	private static final String DATA_LOCATION = "/assets/";
	private static final String DATA_HANDLER = DATA_LOCATION + "**";

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("home/index");
		registry.addViewController("/login").setViewName("signin");
	}

	@Bean
	public ITemplateResolver templateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setPrefix(VIEWS);
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setCharacterEncoding("UTF-8");
		resolver.setCacheable(false);
		return resolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.addTemplateResolver(new UrlTemplateResolver());
		templateEngine.addTemplateResolver(templateResolver());
		templateEngine.addDialect(new SpringSecurityDialect());
		templateEngine.addDialect(new LayoutDialect());
		return templateEngine;
	}

	@Bean
	public ViewResolver viewResolver() {
		ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
		thymeleafViewResolver.setTemplateEngine(templateEngine());
		thymeleafViewResolver.setCharacterEncoding("UTF-8");
		return thymeleafViewResolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(ASSETS_HANDLER).addResourceLocations(ASSETS_LOCATION);
		registry.addResourceHandler(DATA_HANDLER).addResourceLocations(DATA_LOCATION);
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
