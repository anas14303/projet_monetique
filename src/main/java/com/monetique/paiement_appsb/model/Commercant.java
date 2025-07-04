package com.monetique.paiement_appsb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "commercant")
public class Commercant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du commerçant est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100, message = "La ville ne doit pas dépasser 100 caractères")
    @Column(nullable = false)
    private String ville;

    @NotBlank(message = "Le secteur est obligatoire")
    @Size(max = 100, message = "Le secteur ne doit pas dépasser 100 caractères")
    @Column(nullable = false)
    private String secteur;

    // Relation OneToMany vers Terminal
    @OneToMany(mappedBy = "commercant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Terminal> terminaux;
}
