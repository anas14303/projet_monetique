package com.monetique.paiement_appsb.service;

import com.monetique.paiement_appsb.model.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    long countActiveUsers();
    
    Utilisateur saveUser(Utilisateur user, List<Integer> roleIds, boolean resetPassword);
    
    void deleteUser(Long userId, String currentUsername);
    
    Utilisateur toggleActive(Long userId);
    
    Utilisateur resetPassword(Long userId);
    
    Utilisateur findById(Long id);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndIdNot(String email, Long id);
    
    Page<Utilisateur> searchUsers(String keyword, Pageable pageable);
    
    Page<Utilisateur> findAll(Pageable pageable);
    
    Optional<Utilisateur> findByEmail(String email);
}
