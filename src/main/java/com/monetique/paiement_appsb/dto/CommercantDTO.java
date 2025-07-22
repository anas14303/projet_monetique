package com.monetique.paiement_appsb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CommercantDTO {
    private Long id;

    @NotBlank(message = "Le nom du commerçant est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100, message = "La ville ne doit pas dépasser 100 caractères")
    private String ville;

    @NotBlank(message = "Le secteur est obligatoire")
    @Size(max = 100, message = "Le secteur ne doit pas dépasser 100 caractères")
    private String secteur;
    
    private List<TerminalDTO> terminaux;
}
