package com.monetique.paiement_appsb.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "historique_statut_paiement")
public class HistoriqueStatutPaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paiement_id", nullable = false)
    private Paiement paiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement nouveauStatut;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(nullable = false)
    private String modifiePar;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateModification;

    public HistoriqueStatutPaiement() {}

    public HistoriqueStatutPaiement(Paiement paiement, StatutPaiement ancienStatut, 
                                   StatutPaiement nouveauStatut, String commentaire, String modifiePar) {
        this.paiement = paiement;
        this.ancienStatut = ancienStatut;
        this.nouveauStatut = nouveauStatut;
        this.commentaire = commentaire;
        this.modifiePar = modifiePar;
    }
}
