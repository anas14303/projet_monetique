package com.monetique.paiement_appsb.controller;

import com.monetique.paiement_appsb.model.ERole;
import com.monetique.paiement_appsb.model.Role;
import com.monetique.paiement_appsb.model.Utilisateur;
import com.monetique.paiement_appsb.repository.RoleRepository;
import com.monetique.paiement_appsb.service.UtilisateurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/utilisateurs")
public class UtilisateurController {
    
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurController.class);
    

    private static final int DEFAULT_PAGE_SIZE = 10;
    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService, PasswordEncoder passwordEncoder) {
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listUtilisateurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "nom") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String keyword,
            Model model) {
        
        try {
            // Validation des paramètres
            if (page < 0) page = 0;
            if (!List.of("asc", "desc").contains(sortDir.toLowerCase())) {
                sortDir = "asc";
            }
            
            // Création du tri
            Sort sort = Sort.by(sortField);
            sort = sortDir.equalsIgnoreCase("asc") ? sort.ascending() : sort.descending();
            
            // Création de la pagination avec tri
            Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, sort);
            
            // Récupération des utilisateurs avec recherche et pagination
            Page<Utilisateur> utilisateursPage;
            if (keyword != null && !keyword.trim().isEmpty()) {
                utilisateursPage = utilisateurService.search(keyword, pageable);
            } else {
                utilisateursPage = utilisateurService.findAll(pageable);
            }
            
            // Ajout des attributs au modèle
            model.addAttribute("utilisateurs", utilisateursPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", utilisateursPage.getTotalPages());
            model.addAttribute("totalItems", utilisateursPage.getTotalElements());
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("keyword", keyword);
            
            // Gestion des tris inversés pour les liens
            String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
            model.addAttribute("reverseSortDir", reverseSortDir);
            
            // Messages flash
            if (model.containsAttribute("success")) {
                model.addAttribute("successMessage", model.getAttribute("success"));
            }
            if (model.containsAttribute("error")) {
                model.addAttribute("errorMessage", model.getAttribute("error"));
            }
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Une erreur est survenue lors du chargement des utilisateurs : " + e.getMessage());
        }
        
        return "utilisateurs/list";
    }

    @Autowired
    private RoleRepository roleRepository;
    
    @GetMapping("/new")
    public String showNewForm(Model model) {
        if (!model.containsAttribute("utilisateur")) {
            model.addAttribute("utilisateur", new Utilisateur());
        }
        model.addAttribute("allRoles", roleRepository.findAll());
        return "utilisateurs/form";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Utilisateur utilisateur = utilisateurService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + id));
                
            if (!model.containsAttribute("utilisateur")) {
                model.addAttribute("utilisateur", utilisateur);
            }
            
            model.addAttribute("allRoles", roleRepository.findAll());
            return "utilisateurs/form";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/utilisateurs";
        }
    }

    @PostMapping("/save")
    public String saveUtilisateur(
            @ModelAttribute("utilisateur") Utilisateur utilisateur,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Vérification des erreurs de validation
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.utilisateur", bindingResult);
                redirectAttributes.addFlashAttribute("utilisateur", utilisateur);
                return "redirect:/utilisateurs" + (utilisateur.getId() != null ? "/edit/" + utilisateur.getId() : "/new");
            }
            
            // Vérifier si l'email existe déjà
            if (utilisateur.getId() == null) {
                // Nouvel utilisateur - vérifier si l'email existe
                if (utilisateurService.existsByEmail(utilisateur.getEmail())) {
                    bindingResult.rejectValue("email", "email.exists", "Un utilisateur avec cet email existe déjà");
                    redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.utilisateur", bindingResult);
                    redirectAttributes.addFlashAttribute("utilisateur", utilisateur);
                    return "redirect:/utilisateurs/new";
                }
                
                // Vérification que le mot de passe n'est pas vide pour une création
                if (utilisateur.getPassword() == null || utilisateur.getPassword().trim().isEmpty()) {
                    bindingResult.rejectValue("password", "password.required", "Le mot de passe est obligatoire");
                    redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.utilisateur", bindingResult);
                    redirectAttributes.addFlashAttribute("utilisateur", utilisateur);
                    return "redirect:/utilisateurs/new";
                }
            } else {
                // Mise à jour d'un utilisateur existant
                if (utilisateurService.existsByEmailAndIdNot(utilisateur.getEmail(), utilisateur.getId())) {
                    bindingResult.rejectValue("email", "email.exists", "Un utilisateur avec cet email existe déjà");
                    redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.utilisateur", bindingResult);
                    redirectAttributes.addFlashAttribute("utilisateur", utilisateur);
                    return "redirect:/utilisateurs/edit/" + utilisateur.getId();
                }
                
                // Si le mot de passe est vide lors d'une mise à jour, on conserve l'ancien
                if (utilisateur.getPassword() == null || utilisateur.getPassword().trim().isEmpty()) {
                    Utilisateur existingUser = utilisateurService.findById(utilisateur.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + utilisateur.getId()));
                    utilisateur.setPassword(existingUser.getPassword());
                }
            }
            
            // Définir le rôle par défaut si non défini
            if (utilisateur.getRoles() == null || utilisateur.getRoles().isEmpty()) {
                Role defaultRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new IllegalStateException("Rôle utilisateur non trouvé"));
                Set<Role> roles = new HashSet<>();
                roles.add(defaultRole);
                utilisateur.setRoles(roles);
            }
            
            // Hachage du mot de passe s'il est fourni
            if (utilisateur.getPassword() != null && !utilisateur.getPassword().trim().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(utilisateur.getPassword().trim());
                utilisateur.setPassword(encodedPassword);
            } else if (utilisateur.getId() != null) {
                // Si c'est une mise à jour et que le mot de passe n'est pas fourni, on conserve l'ancien
                Utilisateur existingUser = utilisateurService.findById(utilisateur.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + utilisateur.getId()));
                utilisateur.setPassword(existingUser.getPassword());
            }
            
            // Sauvegarde de l'utilisateur
            utilisateurService.save(utilisateur);
            redirectAttributes.addFlashAttribute("success", 
                utilisateur.getId() == null ? 
                "L'utilisateur a été créé avec succès." : 
                "Les modifications ont été enregistrées avec succès.");
                
            return "redirect:/utilisateurs";
            
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde de l'utilisateur", e);
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la sauvegarde de l'utilisateur");
            return "redirect:/utilisateurs" + (utilisateur != null && utilisateur.getId() != null ? "/edit/" + utilisateur.getId() : "/new");
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteUtilisateur(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "nom") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String keyword,
            RedirectAttributes redirectAttributes) {
        
        try {
            Utilisateur utilisateur = utilisateurService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + id));
                
            // Vérifier si l'utilisateur peut être supprimé (vérifier s'il a le rôle ADMIN)
            boolean isAdmin = utilisateur.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
                
            if (isAdmin) {
                redirectAttributes.addFlashAttribute("error", "Impossible de supprimer un administrateur.");
                return "redirect:/utilisateurs?page=" + page + "&sortField=" + sortField + "&sortDir=" + sortDir + "&keyword=" + keyword;
            }
            
            utilisateurService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "L'utilisateur a été supprimé avec succès.");
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la suppression de l'utilisateur : " + e.getMessage());
        }
        
        return "redirect:/utilisateurs?page=" + page + "&sortField=" + sortField + "&sortDir=" + sortDir + "&keyword=" + keyword;
    }
}
