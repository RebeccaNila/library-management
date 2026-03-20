package com.library.management.common.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI librarySystemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management System API")
                        .description("RESTful API for managing books, borrowers, and borrow records.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("John")
                                .email("john@example.com")));
    }
}
