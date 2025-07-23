package com.personal.delivery_allocation_engine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Soumyajit Podder created on 23/07/25
 */
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI().info(new Info().title("Delivery Partner Suggestion System API").version("1.0")
        .description("API for managing food delivery orders and partner matching")
        .contact(new Contact().name("Delivery System Team").email("team@delivery.com")));
  }
}