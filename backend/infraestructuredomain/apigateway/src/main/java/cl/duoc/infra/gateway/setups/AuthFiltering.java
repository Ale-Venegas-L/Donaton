package cl.duoc.infra.gateway.setups;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthFiltering implements GlobalFilter, Ordered {

    private final WebClient.Builder webclientBuilder;

    public AuthFiltering(WebClient.Builder _webClientBuilder) {
        this.webclientBuilder = _webClientBuilder;
    }

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/auth/") || path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            log.info("Error de Header");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header");
        }

        String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        String[] parts = authHeader.split(" ");
        if (parts.length != 2 || !"Bearer".equals(parts[0])) {
            log.info("Error de Bearer");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad Authorization structure");
        }

        return webclientBuilder.build()
                .get()
                .uri("lb://keycloak/auth/roles")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + parts[1])
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    if (response == null || response.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Roles missing");
                    }
                    return exchange;
                })
                .onErrorMap(error -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Communication Error", error.getCause()))
                .flatMap(chain::filter);
    }
}
