package com.personal.delivery_allocation_engine.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@Component
@Slf4j
public class Tile38HealthIndicator implements HealthIndicator {

  public static final String STATUS = "status";
  private final RedisTemplate<String, Object> tile38Template;

  public Tile38HealthIndicator(RedisTemplate<String, Object> tile38Template) {
    this.tile38Template = tile38Template;
  }

  @Override
  public Health health() {
    try {
      // Test Tile38 connectivity with a simple PING command
      String result = tile38Template.execute(
          (RedisCallback<String>) connection -> Objects.requireNonNull(connection.ping()));

      if ("PONG".equals(result)) {
        return Health.up().withDetail(STATUS, "Connected").withDetail("response", result).build();
      } else {
        return Health.down().withDetail(STATUS, "Unexpected response").withDetail("response", result).build();
      }
    } catch (Exception e) {
      log.error("Tile38 health check failed: {}", e.getMessage());
      return Health.down().withDetail(STATUS, "Connection failed").withDetail("error", e.getMessage()).build();
    }
  }
}
