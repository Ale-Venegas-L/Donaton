package cl.duoc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI campaignOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Campaign Service API")
                        .description("Documentación para campañas de Donaton")
                        .version("1.0.0")
                        )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Development server"),
                        new Server()
                                .url("https://api.campaign.com")
                                .description("Production server")
                ));
    }
}
