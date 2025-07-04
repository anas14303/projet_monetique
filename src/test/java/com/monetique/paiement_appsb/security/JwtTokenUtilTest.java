package com.monetique.paiement_appsb.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class JwtTokenUtilTest {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private UserDetails userDetails;

    @BeforeEach
    public void setup() {
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
        // Création d'un token avec une expiration très courte (1ms)
        JwtTokenUtil utilWithShortExpiration = new JwtTokenUtil();
        utilWithShortExpiration.setSecret("testSecret");
        utilWithShortExpiration.setExpiration(1); // 1ms d'expiration

        String token = utilWithShortExpiration.generateToken(userDetails);
        
        // Attendre que le token expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Le token devrait être expiré
        assertFalse(utilWithShortExpiration.validateToken(token, userDetails));
    }

    @Test
    public void testInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertThrows(Exception.class, () -> {
            jwtTokenUtil.validateToken(invalidToken, userDetails);
        });
    }
}
