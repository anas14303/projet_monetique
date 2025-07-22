package com.monetique.paiement_appsb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TerminalDTO {
    private Long id;
    
    @NotBlank(message = "Le modèle est obligatoire")
    @Size(max = 100, message = "Le modèle ne doit pas dépasser 100 caractères")
    private String modele;
    
    @NotBlank(message = "Le statut est obligatoire")
    @Size(max = 50, message = "Le statut ne doit pas dépasser 50 caractères")
    private String statut;
    
    @NotNull(message = "L'ID du commerçant est obligatoire")
    private Long commercantId;
    
    private String commercantNom;
}
