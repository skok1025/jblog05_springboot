package com.cafe24.jblog.config;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cafe24.security.AuthAccessInterceptor;
import com.cafe24.security.AuthLoginInterceptor;
import com.cafe24.security.AuthLogoutInterceptor;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

@Configuration
//@EnableAspectJAutoProxy
//@EnableWebMvc
//@ComponentScan({ "com.cafe24.jblog.controller" })
//@Import({MVCConfig.class,SecurityConfig.class, MessageConfig.class, FileUpload.class})
public class WebConfig implements WebMvcConfigurer{

	//
	// Message Converter
	//
	@Bean
	public MappingJackson2HttpMessageConverter mappingConverter() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder().indentOutput(true)
				.dateFormat(new SimpleDateFormat("yyyy-MM-dd")).modulesToInstall(new ParameterNamesModule());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(builder.build());
		converter.setSupportedMediaTypes(Arrays.asList(new MediaType("application", "json", Charset.forName("UTF-8"))));

		return converter;
	}

	
	public StringHttpMessageConverter stringConverter() {
		StringHttpMessageConverter converter = new StringHttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList(new MediaType("text", "html", Charset.forName("UTF-8"))));
		return converter;
	}
	
	
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(mappingConverter());
		converters.add(stringConverter());
	}
	
	//
	// Interceptor
	//

	@Bean
	public AuthLoginInterceptor authLoginInterceptor() {
		return new AuthLoginInterceptor();
	}

	@Bean
	public AuthLogoutInterceptor authLogoutInterceptor() {
		return new AuthLogoutInterceptor();
	}

	@Bean
	public AuthAccessInterceptor authAccessInterceptor() {
		return new AuthAccessInterceptor();
	}
	
	
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(authLoginInterceptor()).addPathPatterns("/user/auth");

		registry.addInterceptor(authLogoutInterceptor()).addPathPatterns("/user/logout");

		registry.addInterceptor(authAccessInterceptor()).addPathPatterns("/**").excludePathPatterns("/user/login")
				.excludePathPatterns("/user/logout").excludePathPatterns("/user/join").excludePathPatterns("/user/api/**").excludePathPatterns("/sh");

	}
	
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/logos/**").addResourceLocations("file:/jblog-uploads/");
	}
	
	
	
	
	
}
