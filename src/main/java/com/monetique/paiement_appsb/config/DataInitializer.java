package com.monetique.paiement_appsb.config;

import com.monetique.paiement_appsb.model.ERole;
import com.monetique.paiement_appsb.model.Role;
import com.monetique.paiement_appsb.model.Utilisateur;
import com.monetique.paiement_appsb.repository.RoleRepository;
import com.monetique.paiement_appsb.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(UtilisateurRepository utilisateurRepository, 
                                          RoleRepository roleRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            // Création des rôles s'ils n'existent pas
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_ADMIN)));
                
            // Création des autres rôles (peuvent être utilisés plus tard)
            roleRepository.findByName(ERole.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_USER)));
            
            roleRepository.findByName(ERole.ROLE_MODERATOR)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_MODERATOR)));

            // Création d'un utilisateur admin par défaut s'il n'existe pas
            if (utilisateurRepository.count() == 0) {
                Utilisateur admin = new Utilisateur();
                admin.setUsername("admin");
                admin.setNom("Administrateur");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                admin.setRoles(roles);
                
                admin.setActive(true);
                utilisateurRepository.save(admin);
            }
        };
    }
}