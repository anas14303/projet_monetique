package com.monetique.paiement_appsb.model;

import jakarta.persistence.*;
import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "carte_bancaire")
public class CarteBancaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_masked", nullable = false)
    private String numeroMasked;

    @Column(nullable = false)
    private String type;
    
    @Column(name = "date_exp", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateExp;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
    
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    private String statut = "ACTIVE";
    
    @Column(name = "date_creation", nullable = false, updatable = false)
    @CreationTimestamp
    private Date dateCreation;

    public CarteBancaire() {}

    public CarteBancaire(String numeroMasked, String type, Date dateExp) {
        this.numeroMasked = numeroMasked;
        this.type = type;
        this.dateExp = dateExp;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroMasked() {
        return numeroMasked;
    }

    public void setNumeroMasked(String numeroMasked) {
        this.numeroMasked = numeroMasked;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDateExp() {
        return dateExp;
    }

    public void setDateExp(Date dateExp) {
        this.dateExp = dateExp;
    }
    
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public Date getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CarteBancaire that = (CarteBancaire) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}