package com.rag.platform;

import com.rag.platform.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class RagPlatformApplication {
  public static void main(String[] args) {
    SpringApplication.run(RagPlatformApplication.class, args);
  }
}

