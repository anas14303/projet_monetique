package com.monetique.paiement_appsb.service.impl;

import com.monetique.paiement_appsb.exception.ResourceNotFoundException;
import com.monetique.paiement_appsb.model.ERole;
import com.monetique.paiement_appsb.model.Role;
import com.monetique.paiement_appsb.model.Utilisateur;
import com.monetique.paiement_appsb.repository.RoleRepository;
import com.monetique.paiement_appsb.repository.UtilisateurRepository;
import com.monetique.paiement_appsb.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UtilisateurRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UtilisateurRepository userRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countByActiveTrue();
    }
    
    @Override
    @Transactional
    public Utilisateur saveUser(Utilisateur user, List<Integer> roleIds, boolean resetPassword) {
        logger.info("Sauvegarde de l'utilisateur: {}", user.getEmail());
        
        if (user.getId() == null) {
            // Nouvel utilisateur
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("Cet email est déjà utilisé par un autre utilisateur");
            }
            user.setPassword(passwordEncoder.encode("password123"));
            
            // Initialiser les collections si elles sont nulles
            if (user.getCartesBancaires() == null) {
                user.setCartesBancaires(new ArrayList<>());
            }
            if (user.getPaiements() == null) {
                user.setPaiements(new ArrayList<>());
            }
        } else {
            // Mise à jour d'un utilisateur existant
            Utilisateur existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + user.getId()));
            
            // Vérifier si l'email a été modifié et s'il est déjà utilisé
            if (!existingUser.getEmail().equals(user.getEmail()) && 
                userRepository.existsByEmailAndIdNot(user.getEmail(), user.getId())) {
                throw new IllegalArgumentException("Cet email est déjà utilisé par un autre utilisateur");
            }
            
            // Copier les champs qui ne doivent pas être modifiés
            user.setDateCreation(existingUser.getDateCreation());
            user.setLastLogin(existingUser.getLastLogin());
            
            // Gestion du mot de passe
            if (resetPassword) {
                user.setPassword(passwordEncoder.encode("password123"));
            } else {
                user.setPassword(existingUser.getPassword());
            }
            
            // Préserver les collections existantes
            user.setCartesBancaires(existingUser.getCartesBancaires());
            user.setPaiements(existingUser.getPaiements());
        }
        
        // Gestion des rôles
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec l'ID: " + roleId)))
                .collect(Collectors.toSet());
            user.setRoles(roles);
        } else {
            user.setRoles(Collections.emptySet());
        }
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long userId, String currentUsername) {
        logger.info("Suppression de l'utilisateur ID: {}", userId);
        
        Utilisateur user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
        
        // Empêcher l'auto-suppression
        if (user.getEmail().equals(currentUsername)) {
            throw new IllegalStateException("Vous ne pouvez pas supprimer votre propre compte");
        }
        
        // Vérifier si c'est le dernier administrateur
        if (user.getRoles().stream().anyMatch(role -> role.getName() == ERole.ROLE_ADMIN) &&
            userRepository.countByRoles_Name(ERole.ROLE_ADMIN) <= 1) {
            throw new IllegalStateException("Impossible de supprimer le dernier administrateur");
        }
        
        // Supprimer les références aux rôles
        user.getRoles().clear();
        userRepository.save(user);
        
        // Supprimer l'utilisateur
        userRepository.delete(user);
    }
    
    @Override
    @Transactional
    public Utilisateur toggleActive(Long userId) {
        logger.info("Changement du statut actif pour l'utilisateur ID: {}", userId);
        
        Utilisateur user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
        
        // Inverser le statut actif
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public Utilisateur resetPassword(Long userId) {
        logger.info("Réinitialisation du mot de passe pour l'utilisateur ID: {}", userId);
        
        Utilisateur user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
        
        // Générer un nouveau mot de passe aléatoire
        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        
        // Note: Dans une application réelle, vous voudriez envoyer le nouveau mot de passe par email
        logger.info("Nouveau mot de passe pour {}: {}", user.getEmail(), newPassword);
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Utilisateur findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Utilisateur> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Utilisateur> searchUsers(String keyword, Pageable pageable) {
        logger.debug("Recherche d'utilisateurs avec le mot-clé: {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.search(keyword, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Utilisateur> findAll(Pageable pageable) {
        logger.debug("Récupération de tous les utilisateurs avec pagination");
        return userRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return userRepository.existsByEmailAndIdNot(email, id);
    }
    
    private String generateRandomPassword() {
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*_=+-/";
        
        String allChars = upperCaseLetters + lowerCaseLetters + numbers + specialChars;
        Random random = new Random();
        
        // Créer un mot de passe de 12 caractères
        StringBuilder password = new StringBuilder(12);
        
        // Ajouter au moins un caractère de chaque type
        password.append(upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length())));
        password.append(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // Remplir le reste avec des caractères aléatoires
        for (int i = 0; i < 8; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Mélanger les caractères pour plus de sécurité
        char[] passwordArray = password.toString().toCharArray();
        for (int i = 0; i < passwordArray.length; i++) {
            int randomIndex = random.nextInt(passwordArray.length);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[randomIndex];
            passwordArray[randomIndex] = temp;
        }
        
        return new String(passwordArray);
    }
}
