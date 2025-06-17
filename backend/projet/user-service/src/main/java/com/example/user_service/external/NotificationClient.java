package com.example.user_service.external;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

@Component
public class NotificationClient {

    private final WebClient logClient;
    private final WebClient notificationClient;

    public NotificationClient(WebClient.Builder webClientBuilder) {
        this.logClient = webClientBuilder.baseUrl("http://localhost:8083").build();
        this.notificationClient = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    public void sendLog(String message) {
        logClient.post()
                .uri("/logs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"message\":\"" + message + "\"}")
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

    public void sendNotification(String message) {
        notificationClient.post()
                .uri("/api/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"message\":\"" + message + "\"}")
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }
}
