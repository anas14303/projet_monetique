package com.monetique.paiement_appsb.repository;

import com.monetique.paiement_appsb.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Utilisateur, Long> {
    long countByActiveTrue();
}
