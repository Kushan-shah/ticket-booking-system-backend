package com.booking.system.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Ticket Booking System API")
                        .version("1.0")
                        .description("Production level backend API with Optimistic Locking"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomizer globalResponseCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    // Add standard error responses to every endpoint (description only, no schema)
                    operation.getResponses()
                            .addApiResponse("400", new ApiResponse().description("Bad Request — Validation failed or invalid input"))
                            .addApiResponse("401", new ApiResponse().description("Unauthorized — Missing or invalid JWT token"))
                            .addApiResponse("403", new ApiResponse().description("Forbidden — Insufficient permissions for this action"))
                            .addApiResponse("404", new ApiResponse().description("Not Found — Resource does not exist"))
                            .addApiResponse("409", new ApiResponse().description("Conflict — Seat already locked by another user (Optimistic Lock)"))
                            .addApiResponse("500", new ApiResponse().description("Internal Server Error — Unexpected failure"));
                })
        );
    }
}
