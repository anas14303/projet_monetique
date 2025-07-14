package com.monetique.paiement_appsb.model;

import jakarta.persistence.*;
import java.util.Date;

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