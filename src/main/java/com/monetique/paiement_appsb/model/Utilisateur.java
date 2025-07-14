package com.monetique.paiement_appsb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import com.monetique.paiement_appsb.model.Paiement;
import java.util.Set;

@Entity
@Table(name = "utilisateur",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "email")
       })
@Data
@NoArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String nom;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Veuillez fournir un email valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    @Column(nullable = false, length = 100, unique = true)
    private String email;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarteBancaire> cartesBancaires;
    
    @JsonIgnore
    @OneToMany(mappedBy = "utilisateur", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<Paiement> paiements;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private boolean active = true;
    
    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;
    
    @UpdateTimestamp
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(length = 100)
    private String password;
    
    // Méthodes utilitaires
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    // Méthodes pour gérer la relation bidirectionnelle avec les cartes bancaires
    public void addCarteBancaire(CarteBancaire carte) {
        if (this.cartesBancaires == null) {
            this.cartesBancaires = new java.util.ArrayList<>();
        }
        if (!this.cartesBancaires.contains(carte)) {
            this.cartesBancaires.add(carte);
            carte.setUtilisateur(this);
        }
    }
    
    public void removeCarteBancaire(CarteBancaire carte) {
        if (this.cartesBancaires != null) {
            this.cartesBancaires.remove(carte);
            carte.setUtilisateur(null);
        }
    }
    
    // Méthode pour mettre à jour la collection de cartes bancaires
    public void setCartesBancaires(List<CarteBancaire> nouvellesCartes) {
        if (this.cartesBancaires == null) {
            this.cartesBancaires = nouvellesCartes;
            if (nouvellesCartes != null) {
                nouvellesCartes.forEach(carte -> carte.setUtilisateur(this));
            }
        } else if (nouvellesCartes != null) {
            // Supprimer les cartes qui ne sont plus dans la nouvelle collection
            this.cartesBancaires.removeIf(carte -> !nouvellesCartes.contains(carte));
            
            // Ajouter ou mettre à jour les nouvelles cartes
            for (CarteBancaire nouvelleCarte : nouvellesCartes) {
                if (!this.cartesBancaires.contains(nouvelleCarte)) {
                    this.cartesBancaires.add(nouvelleCarte);
                }
                nouvelleCarte.setUtilisateur(this);
            }
        } else {
            this.cartesBancaires.clear();
        }
    }
    
    // Méthodes pour gérer la relation bidirectionnelle avec les paiements
    public void addPaiement(Paiement paiement) {
        if (this.paiements == null) {
            this.paiements = new java.util.ArrayList<>();
        }
        if (!this.paiements.contains(paiement)) {
            this.paiements.add(paiement);
            paiement.setUtilisateur(this);
        }
    }
    
    public void removePaiement(Paiement paiement) {
        if (this.paiements != null) {
            this.paiements.remove(paiement);
            paiement.setUtilisateur(null);
        }
    }
    
    // Méthode pour mettre à jour la collection de paiements
    @PreUpdate
    @PrePersist
    public void updatePaiements() {
        if (this.paiements != null) {
            // Mettre à jour la référence de l'utilisateur pour chaque paiement
            this.paiements.forEach(paiement -> {
                if (paiement.getUtilisateur() != this) {
                    paiement.setUtilisateur(this);
                }
            });
        }
    }
}