package com.monetique.paiement_appsb.repository;

import com.monetique.paiement_appsb.model.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Find a user by email
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<Utilisateur> findByEmail(String email);
    
    /**
     * Check if a user exists with the given email
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if a user exists with the given email excluding a specific user ID
     * @param email the email to check
     * @param id the user ID to exclude
     * @return true if a user with the email exists (excluding the specified ID), false otherwise
     */
    boolean existsByEmailAndIdNot(String email, Long id);
    
    /**
     * Search for users by name or email containing the given keyword
     * @param keyword the search term
     * @param pageable pagination information
     * @return page of users matching the search criteria
     */
    @Query("SELECT u FROM Utilisateur u WHERE " +
           "LOWER(u.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Utilisateur> search(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Find a user by username
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<Utilisateur> findByUsername(String username);
    
    /**
     * Check if a user exists with the given username
     * @param username the username to check
     * @return true if a user with the username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if a user exists with the given username excluding a specific user ID
     * @param username the username to check
     * @param id the user ID to exclude
     * @return true if a user with the username exists (excluding the specified ID), false otherwise
     */
    boolean existsByUsernameAndIdNot(String username, Long id);

    /**
     * Find all users with pagination
     * @param pageable pagination information
     * @return page of users
     */
    @Override
    @NonNull 
    Page<Utilisateur> findAll(@NonNull Pageable pageable);

    /**
     * Find users by name (case-insensitive partial match)
     * @param nom the name to search for
     * @param pageable pagination information
     * @return page of matching users
     */
    @Query("SELECT u FROM Utilisateur u WHERE LOWER(u.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    Page<Utilisateur> findByNom(
            @Param("nom") String nom,
            Pageable pageable
    );
    
    /**
     * Delete a user by email
     * @param email the email of the user to delete
     * @return the number of users deleted
     */
    @Modifying
    @Query("DELETE FROM Utilisateur u WHERE u.email = :email")
    int deleteByEmail(@Param("email") String email);
    
    /**
     * Find users by email (case-insensitive partial match)
     * @param email the email to search for
     * @param pageable pagination information
     * @return page of matching users
     */
    @Query("SELECT u FROM Utilisateur u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    Page<Utilisateur> searchByEmail(
            @Param("email") String email,
            Pageable pageable
    );
}
