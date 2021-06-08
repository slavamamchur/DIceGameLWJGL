package com.cubegames.engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan("com.cubegames.engine.rest")
public class WebAppConfig {

  @Bean
  public UrlBasedViewResolver setupViewResolver() {
    UrlBasedViewResolver resolver = new UrlBasedViewResolver();
    resolver.setPrefix("/pages/");  // указываем где будут лежать наши веб-страницы
    resolver.setSuffix(".jsp");  // формат View который мы будем использовать
    resolver.setViewClass(JstlView.class);
    return resolver;
  }

  @Bean(name = "multipartResolver")
  public CommonsMultipartResolver commonsMultipartResolver() {
    CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
    commonsMultipartResolver.setDefaultEncoding("utf-8");
    commonsMultipartResolver.setMaxUploadSize(50000000);
    return commonsMultipartResolver;
  }

}