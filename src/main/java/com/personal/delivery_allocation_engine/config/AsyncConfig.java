package com.personal.delivery_allocation_engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig {

  @Bean(name = "partnerAllocationExecutor")
  public TaskExecutor partnerAllocationExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(25);
    executor.setThreadNamePrefix("allocation-");
    executor.initialize();
    return executor;
  }
}
