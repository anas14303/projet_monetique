package com.monetique.paiement_appsb.controller.admin;

import com.monetique.paiement_appsb.model.Utilisateur;
import com.monetique.paiement_appsb.repository.RoleRepository;
import com.monetique.paiement_appsb.repository.UtilisateurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/utilisateurs")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminUtilisateurController {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;

    public AdminUtilisateurController(UtilisateurRepository utilisateurRepository, 
                                   RoleRepository roleRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String listUtilisateurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search,
            Model model) {
        
        // Vérifier si l'utilisateur est admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/access-denied";
        }
        
        Pageable pageable = PageRequest.of(
            page, 
            size, 
            Sort.by(Sort.Direction.fromString(direction), sortBy)
        );

        Page<Utilisateur> pageUtilisateurs;
        if (search != null && !search.isEmpty()) {
            pageUtilisateurs = utilisateurRepository.search(search, pageable);
        } else {
            pageUtilisateurs = utilisateurRepository.findAll(pageable);
        }

        model.addAttribute("utilisateurs", pageUtilisateurs);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageUtilisateurs.getTotalPages());
        model.addAttribute("totalItems", pageUtilisateurs.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("search", search);

        return "admin/utilisateurs/list";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Utilisateur getUtilisateur(@PathVariable Long id) {
        return utilisateurRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @PostMapping
    @ResponseBody
    public Utilisateur createUtilisateur(@RequestBody Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public Utilisateur updateUtilisateur(
            @PathVariable Long id, 
            @RequestBody Utilisateur utilisateurDetails) {
        
        return utilisateurRepository.findById(id).map(utilisateur -> {
            utilisateur.setNom(utilisateurDetails.getNom());
            utilisateur.setEmail(utilisateurDetails.getEmail());
            utilisateur.setActive(utilisateurDetails.isActive());
            utilisateur.setRoles(utilisateurDetails.getRoles());
            return utilisateurRepository.save(utilisateur);
        }).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteUtilisateur(@PathVariable Long id) {
        utilisateurRepository.deleteById(id);
    }

    @PostMapping("/{id}/toggle-active")
    @ResponseBody
    public void toggleActive(@PathVariable Long id) {
        utilisateurRepository.findById(id).ifPresent(utilisateur -> {
            utilisateur.setActive(!utilisateur.isActive());
            utilisateurRepository.save(utilisateur);
        });
    }
}
