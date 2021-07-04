package com.hospital.adminapi.configuration.certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
public class CertificateConfiguration {

  @Bean
  public JcaContentSignerBuilder getBuilder() {
    Security.addProvider(new BouncyCastleProvider());
    JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
    builder = builder.setProvider("BC");
    return builder;
  }
}
