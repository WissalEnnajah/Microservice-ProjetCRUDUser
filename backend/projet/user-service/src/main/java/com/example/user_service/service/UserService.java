package com.example.user_service.service;

import com.example.user_service.dto.UserDTO;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.external.NotificationClient;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NotificationClient notificationClient;
    private final WebClient.Builder webClientBuilder; // 👈 Injection WebClient

    public UserDTO createUser(UserDTO dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .role(dto.getRole())
                .active(true)
                .build();
        user = userRepository.save(user);

        String msg = "Création utilisateur : " + user.getUsername();
        notificationClient.sendNotification("Utilisateur créé : " + user.getUsername());
        notificationClient.sendLog(msg);

        webClientBuilder.build()
                .post()
                .uri("http://localhost:8083/api/logs/external")
                .bodyValue(msg)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();

        return toDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UserDTO updateUser(Long id, UserDTO dto) {
        // 🔐 Récupérer l'utilisateur connecté
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        System.out.println("ROLE from DB: " + currentUser.getRole());

        // 🔐 Vérifier autorisation (si c'est un USER, il peut modifier que lui-même)
        if (currentUser.getRole().equals("USER") && !currentUser.getId().equals(id)) {
            throw new RuntimeException("⛔ Tu n'as pas le droit de modifier cet utilisateur !");
        }

        // 🔄 Continuer la mise à jour
        User user = userRepository.findById(id).orElseThrow();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user = userRepository.save(user);

        String msg = "Modification utilisateur : " + user.getUsername();
        notificationClient.sendLog(msg);

        webClientBuilder.build()
                .post()
                .uri("http://localhost:8083/api/logs/external")
                .bodyValue(msg)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();

        return toDTO(user);
    }


    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);

        String msg = "Suppression utilisateur : " + user.getUsername();
        notificationClient.sendLog(msg);

        webClientBuilder.build()
                .post()
                .uri("http://localhost:8083/api/logs/external")
                .bodyValue(msg)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();
    }

    public void toggleActivation(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(!user.isActive());
        userRepository.save(user);
        String state = user.isActive() ? "activé" : "désactivé";

        String msg = "Changement d'état utilisateur : " + user.getUsername() + " → " + state;
        notificationClient.sendNotification("Utilisateur " + state + " : " + user.getUsername());
        notificationClient.sendLog(msg);

        webClientBuilder.build()
                .post()
                .uri("http://localhost:8083/api/logs/external")
                .bodyValue(msg)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }
}
