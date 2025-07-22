package com.monetique.paiement_appsb.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class CarteBancaireDTO {
    private Long id;
    
    @NotBlank(message = "Le numéro masqué est obligatoire")
    @Size(max = 20, message = "Le numéro masqué ne doit pas dépasser 20 caractères")
    private String numeroMasked;
    
    @NotBlank(message = "Le type de carte est obligatoire")
    private String type;
    
    @NotNull(message = "La date d'expiration est obligatoire")
    @Future(message = "La date d'expiration doit être dans le futur")
    private Date dateExp;
    
    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long utilisateurId;
    
    private String utilisateurNomComplet;
    
    private String statut;
    
    private Date dateCreation;
}
