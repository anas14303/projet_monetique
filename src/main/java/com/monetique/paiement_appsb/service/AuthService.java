package com.monetique.paiement_appsb.service;

import com.monetique.paiement_appsb.dto.RegisterRequest;
import com.monetique.paiement_appsb.model.ERole;
import com.monetique.paiement_appsb.model.Role;
import com.monetique.paiement_appsb.model.Utilisateur;
import com.monetique.paiement_appsb.repository.RoleRepository;
import com.monetique.paiement_appsb.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void registerNewUser(RegisterRequest registerRequest) {
        try {
            System.out.println("\n=== [AUTH SERVICE] Début de l'enregistrement d'un nouvel utilisateur ===");
            System.out.println("Vérification de l'existence de l'utilisateur...");
            
            // Vérifier si l'email existe déjà
            boolean emailExists = utilisateurRepository.existsByEmail(registerRequest.getEmail());
            System.out.println("Vérification de l'email: " + 
                             (emailExists ? "EXISTE DÉJÀ" : "NON TROUVÉ"));
            
            if (emailExists) {
                String errorMsg = "Cette adresse email est déjà utilisée";
                System.out.println("Erreur : " + errorMsg);
                throw new RuntimeException(errorMsg);
            }

            // Vérifier si le rôle USER existe
            System.out.println("\nRecherche du rôle USER...");
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseGet(() -> {
                        // Si le rôle n'existe pas, le créer
                        System.out.println("Rôle USER non trouvé, création en cours...");
                        Role newRole = new Role(ERole.ROLE_USER);
                        System.out.println("Nouveau rôle à créer: " + newRole);
                        
                        try {
                            Role savedRole = roleRepository.save(newRole);
                            System.out.println("Rôle USER créé avec succès. ID: " + savedRole.getId());
                            return savedRole;
                        } catch (Exception e) {
                            System.err.println("Erreur lors de la création du rôle: " + e.getMessage());
                            e.printStackTrace();
                            throw new RuntimeException("Erreur lors de la création du rôle utilisateur", e);
                        }
                    });
                    
            System.out.println("Rôle à attribuer: " + userRole.getName() + " (ID: " + userRole.getId() + ")");

            // Créer un nouvel utilisateur
            System.out.println("Création du nouvel utilisateur...");
            Utilisateur user = new Utilisateur();
            user.setNom(registerRequest.getFullName().trim());
            user.setEmail(registerRequest.getEmail().trim());
            
            System.out.println("Détails de l'utilisateur à enregistrer :");
            System.out.println("- Nom complet: " + user.getNom());
            System.out.println("- Email: " + user.getEmail());
            
            // Attribuer le rôle USER
            System.out.println("Attribution du rôle à l'utilisateur...");
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);

            // Enregistrer l'utilisateur
            System.out.println("Sauvegarde de l'utilisateur dans la base de données...");
            Utilisateur savedUser = utilisateurRepository.save(user);
            
            System.out.println("Utilisateur enregistré avec succès. ID: " + savedUser.getId() + 
                             ", Email: " + savedUser.getEmail());
            System.out.println("Rôles de l'utilisateur: " + savedUser.getRoles());
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'enregistrement de l'utilisateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Authentifie un utilisateur avec son email
     * @param email L'email de l'utilisateur
     * @return L'objet Authentication si l'authentification réussit
     */
    public Authentication authenticate(String email) {
        // Dans une application réelle, vous devriez vérifier le mot de passe ici
        // Pour cette version simplifiée, nous supposons que l'utilisateur est authentifié
        // s'il existe dans la base de données
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, "")
        );
    }
}
