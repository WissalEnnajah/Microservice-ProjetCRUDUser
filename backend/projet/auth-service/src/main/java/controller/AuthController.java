package controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import dto.LoginRequest;
import dto.RegisterRequest;
import dto.TokenRefreshRequest;
import dto.LogoutRequest;
import service.UserService;
import service.RefreshTokenService;
import repository.RefreshTokenRepository;
import security.JwtService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private RefreshTokenService refreshTokenService;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private JwtService jwtService;

    // Enregistrement utilisateur
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok(Map.of("message", "Utilisateur enregistré avec succès"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", "Inscription échouée", "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("error", "Erreur serveur", "message", e.getMessage())
            );
        }
    }

    // Authentification utilisateur
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(userService.authenticate(request));
        } catch (AuthenticationServiceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", "Authentification échouée", "message", e.getMessage())
            );
        }
    }

    // Rafraîchissement du token JWT
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
            .map(refreshToken -> {
                if (refreshTokenService.isTokenExpired(refreshToken)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Refresh token expiré"));
                }

                String newAccessToken = jwtService.generateToken(refreshToken.getUser());

                // ✅ Envoi d'un log via WebClient
                userService.logRefresh(refreshToken.getUser().getEmail());

                return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Refresh token introuvable")));
    }

    // Déconnexion utilisateur (suppression du refresh token)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        return refreshTokenRepository.findByToken(request.getRefreshToken())
            .map(token -> {
                String userEmail = token.getUser().getEmail();
                refreshTokenRepository.delete(token);

                // ✅ Envoi d'un log via WebClient
                userService.logLogout(userEmail);

                return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Token invalide ou déjà supprimé")));
    }
}
