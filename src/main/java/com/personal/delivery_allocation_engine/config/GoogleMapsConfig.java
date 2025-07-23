package com.personal.delivery_allocation_engine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Soumyajit Podder created on 23/07/25
 */
@Configuration
public class GoogleMapsConfig {

  @Value("${google.maps.api.key:}")
  private String apiKey;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  public String getApiKey() {
    return apiKey;
  }
}