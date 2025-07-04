package com.monetique.paiement_appsb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "terminal")
public class Terminal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation ManyToOne vers Commercant au lieu d'un simple long
    @NotNull(message = "Le commerçant est obligatoire")
    @ManyToOne(optional = false)
    @JoinColumn(name = "commercant_id", nullable = false)
    private Commercant commercant;

    @NotBlank(message = "Le modèle est obligatoire")
    @Size(max = 100, message = "Le modèle ne doit pas dépasser 100 caractères")
    @Column(nullable = false)
    private String modele;

    @NotBlank(message = "Le statut est obligatoire")
    @Size(max = 50, message = "Le statut ne doit pas dépasser 50 caractères")
    @Column(nullable = false)
    private String statut;
}
