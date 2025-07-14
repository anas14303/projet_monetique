package com.monetique.paiement_appsb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String nom;
    private String email;
    private List<String> roles;

    public LoginResponse(String accessToken, Long id, String nom, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.roles = roles;
    }
}
