package com.idle.rushcutter.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "RushCutter API", version = "1.0", description = "Rush Cutter API 문서")
)
public class SwaggerConfig {
}
