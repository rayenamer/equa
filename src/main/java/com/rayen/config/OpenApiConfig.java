package com.rayen.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EQUA - Wallet Management API")
                        .version("1.0")
                        .description("Microservice for Wallet, Token & Asset management with advanced business rules, "
                                + "risk management and AI-based credit scoring.")
                        .contact(new Contact()
                                .name("Rayen")
                                .email("rayen@equa.com")));
    }
}
