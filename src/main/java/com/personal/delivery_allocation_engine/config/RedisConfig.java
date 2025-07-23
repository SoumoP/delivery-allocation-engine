package com.personal.delivery_allocation_engine.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Soumyajit Podder created on 23/07/25
 */
@Configuration
public class RedisConfig {

  @Value("${tile38.host:localhost}")
  private String tile38Host;

  @Value("${tile38.port:9851}")
  private int tile38Port;

  @Bean
  public RedisClient tile38Client() {
    return RedisClient.create(String.format("redis://%s:%d", tile38Host, tile38Port));
  }

  @Bean
  public StatefulRedisConnection<String, String> tile38Connection(RedisClient redisClient) {
    return redisClient.connect();
  }

  @Bean
  public RedisCommands<String, String> tile38Commands(StatefulRedisConnection<String, String> connection) {
    return connection.sync();
  }
}
