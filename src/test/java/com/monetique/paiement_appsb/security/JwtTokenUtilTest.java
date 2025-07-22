package com.monetique.paiement_appsb.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
public class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;
    private UserDetails userDetails;
    
    // Clé secrète de test (au moins 64 caractères pour HS512)
    private static final String TEST_SECRET = "my-super-secure-secret-key-of-64-chars-or-more-1234567890abcdefghijklmnopqrstuvwxyz";
    
    // Durée d'expiration de 24h en millisecondes
    private static final long EXPIRATION = 24 * 60 * 60 * 1000;

    @BeforeEach
    public void setup() {
        // Utiliser le constructeur qui prend la clé et l'expiration en paramètres
        jwtTokenUtil = new JwtTokenUtil(TEST_SECRET, EXPIRATION);
        
        userDetails = new User(
            "testuser",
            "password",
            Collections.emptyList()
        );
    }

    @Test
    public void testGenerateAndValidateToken() {
        // 1. Génération du token
        String token = jwtTokenUtil.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // 2. Validation du token
        boolean isValid = jwtTokenUtil.validateToken(token, userDetails);
        assertTrue(isValid);

        // 3. Extraction du nom d'utilisateur
        String username = jwtTokenUtil.getUsernameFromToken(token);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    public void testTokenExpiration() {
        // Créer un token avec une date d'expiration passée
        long expiredExpiration = -1000; // Expiration dans le passé
        JwtTokenUtil expiredTokenUtil = new JwtTokenUtil(TEST_SECRET, expiredExpiration);
        
        // Générer le token (sera déjà expiré)
        String token = expiredTokenUtil.generateToken(userDetails);
        
        // Vérifier que le token est considéré comme expiré
        assertFalse(expiredTokenUtil.validateToken(token, userDetails), 
            "Le token devrait être considéré comme expiré");
    }

    @Test
    public void testInvalidToken() {
        // Test avec un token mal formé
        String invalidToken = "invalid.token.here";
        assertThrows(Exception.class, 
            () -> jwtTokenUtil.validateToken(invalidToken, userDetails),
            "Un token invalide devrait lever une exception"
        );
        
        // Test avec un token signé avec une autre clé
        JwtTokenUtil otherKeyTokenUtil = new JwtTokenUtil(
            "a-different-secret-key-1234567890-abcdefghijklmnopqrstuvwxyz-ABCDEFGHIJKLMNOPQRSTUVWXYZ", 
            86400000
        );
        String tokenWithOtherKey = otherKeyTokenUtil.generateToken(userDetails);
        
        assertThrows(Exception.class, 
            () -> jwtTokenUtil.validateToken(tokenWithOtherKey, userDetails),
            "Un token signé avec une clé différente devrait être rejeté"
        );
    }
}
