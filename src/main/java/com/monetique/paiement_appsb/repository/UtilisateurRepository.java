package com.monetique.paiement_appsb.repository;

import com.monetique.paiement_appsb.model.ERole;
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
     * @return true if a user with the email exists and has a different ID, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Utilisateur u WHERE u.email = :email AND u.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
    
    /**
     * Search for users by name or email containing the given keyword
     * @param keyword the search term
     * @param pageable pagination information
     * @return page of users matching the search criteria
     */
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.active = true")
    long countByActiveTrue();
    
    @Query("SELECT u FROM Utilisateur u WHERE " +
           "LOWER(u.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Utilisateur> search(@Param("keyword") String keyword, Pageable pageable);
    

    /**
     * Search for users by name or email containing the given keywords
     * @param nom the name to search for
     * @param email the email to search for
     * @param pageable pagination information
     * @return page of users matching the search criteria
     */
    @Query("SELECT u FROM Utilisateur u WHERE " +
           "LOWER(u.nom) LIKE LOWER(CONCAT('%', :nom, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    Page<Utilisateur> findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(
        @Param("nom") String nom, 
        @Param("email") String email, 
        Pageable pageable
    );
    

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
     * Find users by role name
     * @param roleName the role name to search for
     * @param pageable pagination information
     * @return page of matching users
     */
    @Query("SELECT u FROM Utilisateur u JOIN u.roles r WHERE r.name = :roleName")
    Page<Utilisateur> findByRoleName(@Param("roleName") ERole roleName, Pageable pageable);
    
    /**
     * Count users by role name
     * @param roleName the role name to count users for
     * @return the number of users with the specified role
     */
    @Query("SELECT COUNT(u) FROM Utilisateur u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoles_Name(@Param("roleName") ERole roleName);
    
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
