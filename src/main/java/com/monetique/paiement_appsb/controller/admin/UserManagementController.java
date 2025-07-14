package com.monetique.paiement_appsb.controller.admin;

import com.monetique.paiement_appsb.model.Utilisateur;
import com.monetique.paiement_appsb.repository.RoleRepository;
import com.monetique.paiement_appsb.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    private final UserService userService;
    private final RoleRepository roleRepository;

    public UserManagementController(UserService userService, 
                                  RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }


    @GetMapping
    public String listUsers(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search) {
        
        try {
            // Valider les paramètres de pagination
            if (page < 0) page = 0;
            if (size < 1 || size > 100) size = 10;
            
            // Créer l'objet de tri
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // Récupérer les utilisateurs avec pagination
            Page<Utilisateur> usersPage;
            if (search != null && !search.trim().isEmpty()) {
                usersPage = userService.searchUsers(search, pageable);
            } else {
                usersPage = userService.findAll(pageable);
            }
            
            // Ajouter les attributs au modèle
            model.addAttribute("users", usersPage.getContent());
            model.addAttribute("allRoles", roleRepository.findAll());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usersPage.getTotalPages());
            model.addAttribute("totalItems", usersPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("direction", direction);
            model.addAttribute("search", search);
            
            // Ajouter les paramètres pour la pagination
            model.addAttribute("hasPrevious", page > 0);
            model.addAttribute("hasNext", page < usersPage.getTotalPages() - 1);
            
            return "admin/users/list";
            
        } catch (Exception e) {
            // En cas d'erreur, rediriger vers la première page avec un message d'erreur
            return "redirect:/admin/users?error=" + e.getMessage();
        }
    }
    
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("user", new Utilisateur());
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("isEdit", false);
        return "admin/users/form";
    }
    
    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        Utilisateur user = userService.findById(id);
        model.addAttribute("user", user);
        return "admin/users/view";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Utilisateur user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("isEdit", true);
        return "admin/users/form";
    }
    
    @PostMapping("/save")
    public String saveUser(
            @ModelAttribute("user") Utilisateur user,
            @RequestParam(value = "roleIds", required = false) List<Integer> roleIds,
            @RequestParam(defaultValue = "false") boolean resetPassword,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Utiliser le service pour sauvegarder l'utilisateur
            userService.saveUser(user, roleIds, resetPassword);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                user.getId() == null ? "Utilisateur créé avec succès!" : "Utilisateur mis à jour avec succès!");
            return "redirect:/admin/users";
            
        } catch (IllegalArgumentException e) {
            logger.error("Erreur de validation lors de l'enregistrement de l'utilisateur: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return user.getId() == null ? "redirect:/admin/users/add" : "redirect:/admin/users/edit/" + user.getId();
        } catch (Exception e) {
            logger.error("Erreur lors de l'enregistrement de l'utilisateur", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Une erreur est survenue lors de l'enregistrement: " + e.getMessage());
            return "redirect:/admin/users";
        }
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id, 
                                                       Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Utiliser le service pour supprimer l'utilisateur
            userService.deleteUser(id, authentication.getName());
            
            response.put("success", true);
            response.put("message", "Utilisateur supprimé avec succès");
            return ResponseEntity.ok(response);
            
        } catch (IllegalStateException e) {
            logger.warn("Tentative de suppression non autorisée: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'utilisateur", e);
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/toggle-active/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleActive(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Utiliser le service pour basculer le statut actif
            Utilisateur user = userService.toggleActive(id);
            
            response.put("success", true);
            response.put("active", user.isActive());
            response.put("message", "Statut de l'utilisateur mis à jour avec succès");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Erreur lors du changement de statut de l'utilisateur", e);
            response.put("success", false);
            response.put("message", "Erreur lors de la mise à jour du statut: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/{id}/reset-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.resetPassword(id);
            
            response.put("success", true);
            response.put("message", "Mot de passe réinitialisé avec succès");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la réinitialisation du mot de passe", e);
            response.put("success", false);
            response.put("message", "Erreur lors de la réinitialisation du mot de passe: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
