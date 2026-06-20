package cl.duoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class KeycloakApp {
    public static void main(String[] args) {
        SpringApplication.run(KeycloakApp.class, args);
    }
}