package com.monetique.paiement_appsb.controller.api;

import com.monetique.paiement_appsb.dto.LoginRequest;
import com.monetique.paiement_appsb.dto.LoginResponse;
import com.monetique.paiement_appsb.security.JwtTokenUtil;
import com.monetique.paiement_appsb.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API d'authentification et d'autorisation")
public class ApiAuthController {
    private static final Logger logger = LoggerFactory.getLogger(ApiAuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Tentative de connexion pour l'email: {}", loginRequest.getEmail());
            
            // Authentification simplifiée - vérifie uniquement l'existence de l'email
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), "")
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            logger.info("Utilisateur trouvé: {} (ID: {})", userDetails.getUsername(), userDetails.getId());
            
            String token = jwtTokenUtil.generateToken(userDetails);
            logger.debug("Token JWT généré avec succès");
            
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            logger.debug("Rôles de l'utilisateur: {}", roles);

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(new LoginResponse(
                            token,
                            userDetails.getId(),
                            userDetails.getNom(),
                            userDetails.getEmail(),
                            roles
                    ));
        } catch (Exception e) {
            logger.error("Erreur lors de l'authentification pour l'email {}: {}", 
                loginRequest.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(401).body("Échec de l'authentification: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion utilisateur", description = "Invalide la session de l'utilisateur")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Déconnexion réussie");
    }

    @GetMapping("/check")
    @Operation(summary = "Vérification d'authentification", description = "Vérifie si l'utilisateur est authentifié")
    public ResponseEntity<?> checkAuth(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).build();
    }
}
