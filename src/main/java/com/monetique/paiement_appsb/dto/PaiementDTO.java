package com.monetique.paiement_appsb.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaiementDTO {
    private Long id;
    
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal montant;
    
    @NotNull(message = "La date est obligatoire")
    private Date date;
    
    @NotNull(message = "Le statut est obligatoire")
    private String statut;
    
    @NotNull(message = "Le commerçant est obligatoire")
    private Long commercantId;
    
    @NotNull(message = "L'utilisateur est obligatoire")
    private Long utilisateurId;
    
    @NotNull(message = "La carte bancaire est obligatoire")
    private Long carteBancaireId;
    
    @NotNull(message = "Le type de carte est obligatoire")
    @Size(min = 3, max = 20, message = "Le type de carte doit avoir entre 3 et 20 caractères")
    private String typeCarte;
    
    private String commentaire;
}
