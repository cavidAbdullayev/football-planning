package org.example.footballplanning.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Football Planning API")
                        .description("API for planning football matches")
                        .version("v1.0")
                        .termsOfService("Term of service: (url)")
                        .contact(new Contact()
                                .name("Javid Abdullayev")
                                .email("cavidabdullayevv20@gmail.com")
                                .url("https://github.com/cavidAbdullayev")));
    }
}
