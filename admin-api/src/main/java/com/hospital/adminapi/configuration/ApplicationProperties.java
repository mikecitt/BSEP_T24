package com.hospital.adminapi.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

  @Getter
  public final KeyStore keyStore = new KeyStore();

  @Getter
  public final TrustStore trustStore = new TrustStore();

  @Getter
  public final Certificates certificates = new Certificates();

  public static class KeyStore {

    @Getter
    @Setter
    private String filepath;

    @Getter
    @Setter
    private String password;
  }

  public static class TrustStore {

    @Getter
    @Setter
    private String filepath;

    @Getter
    @Setter
    private String password;
  }

  public static class Certificates {

    @Getter
    @Setter
    private String filepath;
  }
}
