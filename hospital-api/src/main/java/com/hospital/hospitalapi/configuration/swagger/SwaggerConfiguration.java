package com.hospital.hospitalapi.configuration.swagger;

import com.hospital.hospitalapi.configuration.ApplicationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    private final ApplicationProperties applicationProperties;

    public SwaggerConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.hospital.hospitalapi.web.rest"))
                .paths(PathSelectors.regex("/api.*")).build().apiInfo(metaData());
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder().title(applicationProperties.getSwagger().getTitle())
                .description(applicationProperties.getSwagger().getDescription())
                .version(applicationProperties.getSwagger().getVersion())
                .termsOfServiceUrl(applicationProperties.getSwagger().getTermsOfServiceUrl())
                .contact(new Contact(applicationProperties.getSwagger().getContactName(),
                        applicationProperties.getSwagger().getContactUrl(),
                        applicationProperties.getSwagger().getContactEmail()))
                .license(applicationProperties.getSwagger().getLicense())
                .licenseUrl(applicationProperties.getSwagger().getLicenseUrl()).build();
    }
}