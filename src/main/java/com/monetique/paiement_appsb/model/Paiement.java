package com.monetique.paiement_appsb.model;

import jakarta.persistence.*;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "paiement")
@Data
@NoArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'utilisateur est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @NotNull(message = "Le commerçant est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commercant_id", nullable = false)
    private Commercant commercant;

    @NotNull(message = "La carte bancaire est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carte_bancaire_id", nullable = false)
    private CarteBancaire carteBancaire;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @NotNull(message = "La date est obligatoire")
    @Column(nullable = false)
    private Date date;

    @NotNull(message = "Le type de carte est obligatoire")
    @Column(nullable = false)
    private String typeCarte;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statut;

    @Column(length = 500)
    private String commentaire;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "paiement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoriqueStatutPaiement> historiqueStatuts;

    public Paiement(Utilisateur utilisateur, Commercant commercant, CarteBancaire carteBancaire, BigDecimal montant, String typeCarte, StatutPaiement statut) {
        this.utilisateur = utilisateur;
        this.commercant = commercant;
        this.carteBancaire = carteBancaire;
        this.montant = montant;
        this.date = new Date();
        this.typeCarte = typeCarte;
        this.statut = statut;
    }

    @PrePersist
    protected void onCreate() {
        date = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        date = new Date();
    }

    public StatutPaiement getStatut() {
        return statut;
    }

    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }
    
    public void setStatut(String statut) {
        if (statut != null) {
            this.statut = StatutPaiement.valueOf(statut.toUpperCase());
        }
    }

    public Commercant getCommercant() {
        return commercant;
    }

    public void setCommercant(Commercant commercant) {
        this.commercant = commercant;
    }

    public String getTypeCarte() {
        return typeCarte;
    }

    public void setTypeCarte(String typeCarte) {
        this.typeCarte = typeCarte;
    }
    
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    
    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
    
    public CarteBancaire getCarteBancaire() {
        return carteBancaire;
    }

    public void setCarteBancaire(CarteBancaire carteBancaire) {
        this.carteBancaire = carteBancaire;
    }
    
    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public List<HistoriqueStatutPaiement> getHistoriqueStatuts() {
        return historiqueStatuts;
    }

    public void setHistoriqueStatuts(List<HistoriqueStatutPaiement> historiqueStatuts) {
        this.historiqueStatuts = historiqueStatuts;
    }
}
