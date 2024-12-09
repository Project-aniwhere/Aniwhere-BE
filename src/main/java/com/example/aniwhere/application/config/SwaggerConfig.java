package com.example.aniwhere.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("AniWhere API Documentation")
						.version("1.0.0")
						.description("AniWhere API 명세서"))
				.servers(List.of(
						new Server().url("/")
				));
	}
}