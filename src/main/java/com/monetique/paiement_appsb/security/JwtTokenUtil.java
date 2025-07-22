package com.monetique.paiement_appsb.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    private String secret;
    private long expiration;
    
    // Constructeur par défaut pour la compatibilité avec Spring
    public JwtTokenUtil() {
        // Les valeurs par défaut seront remplacées par @Value si le bean est géré par Spring
        this.secret = "default-super-secure-secret-key-of-64-chars-or-more-1234567890abcdefghijklmnopqrstuvwxyz";
        this.expiration = 86400000; // 24h par défaut
    }
    
    // Constructeur pour les tests
    public JwtTokenUtil(String secret, long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }
    
    // Méthodes setter pour la configuration Spring et les tests
    @Value("${jwt.secret:}")
    public void setSecret(String secret) {
        if (secret != null && !secret.isEmpty()) {
            this.secret = secret;
        }
    }
    
    @Value("${jwt.expiration:86400000}")
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    // Méthode rendue public pour les tests
    public SecretKey getSigningKey() {
        // Vérifier si la clé est assez longue (au moins 64 caractères pour HS512)
        if (secret.getBytes(StandardCharsets.UTF_8).length < 64) {
            // Si la clé est trop courte, on en génère une nouvelle de manière sécurisée
            return Jwts.SIG.HS512.key().build();
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Méthode rendue public pour les tests
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .clockSkewSeconds(2) // Ajout d'une tolérance de 2 secondes
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
