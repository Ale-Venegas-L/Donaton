package cl.duoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication/*(exclude = {SecurityAutoConfiguration.class})*/
public class DonationApp {
    public static void main(String[] args) {
        SpringApplication.run(DonationApp.class, args);
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}
