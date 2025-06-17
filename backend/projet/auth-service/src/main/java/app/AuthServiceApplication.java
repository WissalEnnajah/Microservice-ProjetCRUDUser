package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication(scanBasePackages = {"app", "config", "security", "service", "controller"})
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "entity")

public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);

	}
	
	@Bean
	public WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}


}



