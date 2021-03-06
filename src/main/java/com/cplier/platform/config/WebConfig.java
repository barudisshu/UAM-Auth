package com.cplier.platform.config;

import com.cplier.platform.Constants;
import com.cplier.platform.servlet.ParameterRequestFileter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.nio.charset.StandardCharsets;

@Configuration
public class WebConfig {

  private final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;

  public WebConfig(Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
    this.jackson2ObjectMapperBuilder = jackson2ObjectMapperBuilder;
  }

  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setDefaultCharset(StandardCharsets.UTF_8);
    converter.setObjectMapper(jackson2ObjectMapperBuilder.build());
    return converter;
  }

  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages/messages");
    messageSource.setDefaultEncoding(Constants.ENCODING);
    return messageSource;
  }

  @Bean
  public FilterRegistrationBean<ParameterRequestFileter> filterRegistrationBean() {
    FilterRegistrationBean<ParameterRequestFileter> registration = new FilterRegistrationBean<>();
    registration.setFilter(new ParameterRequestFileter());
    registration.addUrlPatterns("/*");
    return registration;
  }
}
