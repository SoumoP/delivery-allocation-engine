package com.personal.delivery_allocation_engine.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@Slf4j
public class Tile38Configuration {
  @Value("${tile38.host}")
  private String host;
  @Value("${tile38.port}")
  private int port;
  @Value("${tile38.timeout}")
  private int timeout;
  @Value("${tile38.pool.max-active}")
  private int maxActive;
  @Value("${tile38.pool.max-idle}")
  private int maxIdle;
  @Value("${tile38.pool.min-idle}")
  private int minIdle;
  @Value("${tile38.pool.max-wait}")
  private int maxWait;
  @Value("${tile38.collections.partners}")
  private String partnersCollection;
  @Value("${tile38.collections.restaurants}")
  private String restaurantsCollection;
  @Value("${tile38.collections.orders}")
  private String ordersCollection;

  @Bean(name = "tile38RedisConnectionFactory")
  public LettuceConnectionFactory tile38ConnectionFactory() {
    log.info("Configuring Tile38 connection to {}:{}", host, port);

    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    config.setHostName(host);
    config.setPort(port);

    LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
        .commandTimeout(Duration.ofMillis(timeout)).build();

    LettuceConnectionFactory factory = new LettuceConnectionFactory(config, clientConfig);
    factory.setValidateConnection(true);

    return factory;
  }

  @Bean(name = "tile38RedisTemplate")
  public RedisTemplate<String, Object> tile38RedisTemplate(
      @Qualifier("tile38RedisConnectionFactory") LettuceConnectionFactory connectionFactory) {

    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Use String serializers for all keys and values, since Tile38 commands are string-based
    StringRedisSerializer stringSerializer = new StringRedisSerializer();
    template.setKeySerializer(stringSerializer);
    template.setValueSerializer(stringSerializer);
    template.setHashKeySerializer(stringSerializer);
    template.setHashValueSerializer(stringSerializer);
    template.setDefaultSerializer(stringSerializer);

    template.afterPropertiesSet();
    return template;
  }

  @Bean
  public GenericObjectPoolConfig<?> tile38PoolConfig() {
    GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
    poolConfig.setMaxTotal(maxActive);
    poolConfig.setMaxIdle(maxIdle);
    poolConfig.setMinIdle(minIdle);
    poolConfig.setMaxWaitMillis(maxWait);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    return poolConfig;
  }
}
