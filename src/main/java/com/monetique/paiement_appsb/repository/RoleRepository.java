package com.monetique.paiement_appsb.repository;

import com.monetique.paiement_appsb.model.ERole;
import com.monetique.paiement_appsb.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
