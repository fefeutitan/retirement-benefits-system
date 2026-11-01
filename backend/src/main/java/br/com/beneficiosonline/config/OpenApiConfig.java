package br.com.beneficiosonline.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Benefícios Online API", version = "v1",
        description = "API para benefícios de aposentadoria, documentos e tramitações"))
public class OpenApiConfig {
}
