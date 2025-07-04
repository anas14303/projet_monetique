package com.monetique.paiement_appsb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monetique.paiement_appsb.validation.CreateValidationGroup;
import com.monetique.paiement_appsb.validation.UpdateValidationGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "utilisateur",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "username"),
           @UniqueConstraint(columnNames = "email")
       })
@Data
@NoArgsConstructor
public class Utilisateur {

    //séquence
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(max = 20, message = "Le nom d'utilisateur ne doit pas dépasser 20 caractères")
    @Column(nullable = false, length = 20, unique = true)
    private String username;

    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(max = 100, message = "Le nom complet ne doit pas dépasser 100 caractères")
    @Column(name = "full_name", nullable = false, length = 100)
    private String nom;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Veuillez fournir un email valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    @Column(nullable = false, length = 100, unique = true)
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire", groups = {CreateValidationGroup.class})
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères", 
          groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    @Column(nullable = false, length = 120)
    @JsonIgnore
    private String password;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    
    @Column(nullable = false)
    private boolean active = true;
    
    @JsonIgnore
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarteBancaire> cartesBancaires;
    
    @JsonIgnore
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paiement> paiements;
}