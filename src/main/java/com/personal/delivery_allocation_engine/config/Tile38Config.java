package com.personal.delivery_allocation_engine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@Configuration
@ConfigurationProperties(prefix = "tile38")
@Data
public class Tile38Config {
  private String host = "localhost";
  private int port = 9851;
  private int timeout = 2000;
  private Pool pool = new Pool();
  private Collections collections = new Collections();

  @Data
  public static class Pool {
    private int maxActive = 20;
    private int maxIdle = 10;
    private int minIdle = 5;
    private int maxWait = 2000;
  }

  @Data
  public static class Collections {
    private String partners = "delivery_partners";
    private String restaurants = "restaurants";
    private String orders = "active_orders";
  }
}