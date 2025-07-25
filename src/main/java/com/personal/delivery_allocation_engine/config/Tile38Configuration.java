package com.personal.delivery_allocation_engine.config;

import com.personal.delivery_allocation_engine.health.Tile38HealthIndicator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@Configuration
@EnableConfigurationProperties(Tile38Config.class)
@Slf4j
public class Tile38Configuration {

  private final Tile38Config tile38Config;

  public Tile38Configuration(Tile38Config tile38Config) {
    this.tile38Config = tile38Config;
  }

  @Bean(name = "tile38RedisConnectionFactory")
  public LettuceConnectionFactory tile38ConnectionFactory() {
    log.info("Configuring Tile38 connection to {}:{}", tile38Config.getHost(), tile38Config.getPort());

    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    config.setHostName(tile38Config.getHost());
    config.setPort(tile38Config.getPort());

    LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
        .commandTimeout(Duration.ofMillis(tile38Config.getTimeout())).build();

    LettuceConnectionFactory factory = new LettuceConnectionFactory(config, clientConfig);
    factory.setValidateConnection(true);

    return factory;
  }

  @Bean(name = "tile38RedisTemplate")
  public RedisTemplate<String, Object> tile38RedisTemplate(
      @Qualifier("tile38RedisConnectionFactory") LettuceConnectionFactory connectionFactory) {

    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Use String serializers for Tile38 commands
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new StringRedisSerializer());

    template.setDefaultSerializer(new StringRedisSerializer());
    template.afterPropertiesSet();

    return template;
  }

  @Bean
  public GenericObjectPoolConfig<?> tile38PoolConfig() {
    GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
    poolConfig.setMaxTotal(tile38Config.getPool().getMaxActive());
    poolConfig.setMaxIdle(tile38Config.getPool().getMaxIdle());
    poolConfig.setMinIdle(tile38Config.getPool().getMinIdle());
    poolConfig.setMaxWaitMillis(tile38Config.getPool().getMaxWait());
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    return poolConfig;
  }

  @Bean
  public Tile38HealthIndicator tile38HealthIndicator(
      @Qualifier("tile38RedisTemplate") RedisTemplate<String, Object> tile38Template) {
    return new Tile38HealthIndicator(tile38Template);
  }
}

