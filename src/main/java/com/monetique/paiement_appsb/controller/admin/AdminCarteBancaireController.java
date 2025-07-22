package com.monetique.paiement_appsb.controller.admin;

import com.monetique.paiement_appsb.dto.CarteBancaireDTO;
import com.monetique.paiement_appsb.service.CarteBancaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/cartes")
public class AdminCarteBancaireController {

    private static final List<String> SORT_FIELDS = Arrays.asList(
            "id", "numeroMasked", "type", "dateExpiration", "dateCreation", "statut"
    );
    private static final String DEFAULT_SORT_FIELD = "dateCreation";
    private static final String DEFAULT_SORT_DIR = "desc";
    
    @Autowired
    private CarteBancaireService carteBancaireService;

    @GetMapping
    public String listCartesBancaires(
            @RequestParam(required = false) String numeroMasked,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = DEFAULT_SORT_FIELD) String sortBy,
            @RequestParam(defaultValue = DEFAULT_SORT_DIR) String sortDir,
            Model model) {
        
        // Validation des paramètres de tri
        if (!SORT_FIELDS.contains(sortBy)) {
            sortBy = DEFAULT_SORT_FIELD;
        }
        
        if (!Arrays.asList("asc", "desc").contains(sortDir.toLowerCase())) {
            sortDir = DEFAULT_SORT_DIR;
        }
        
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Appel du service de recherche
        Page<CarteBancaireDTO> cartes = carteBancaireService.search(
            numeroMasked, 
            type, 
            statut,
            pageable
        );

        model.addAttribute("cartes", cartes.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cartes.getTotalPages());
        model.addAttribute("totalItems", cartes.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        // Paramètres de recherche
        model.addAttribute("numeroMasked", numeroMasked);
        model.addAttribute("type", type);
        model.addAttribute("statut", statut);
        
        // Liste des statuts pour le filtre
        model.addAttribute("statuts", Arrays.asList("ACTIVE", "BLOQUEE", "EXPIREE", "VOLUME_DEPASSE"));
        
        return "admin/cartes/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("carteDTO", new CarteBancaireDTO());
        model.addAttribute("action", "create");
        return "admin/cartes/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        return carteBancaireService.findById(id)
                .map(carteDTO -> {
                    model.addAttribute("carteDTO", carteDTO);
                    model.addAttribute("action", "edit");
                    return "admin/cartes/form";
                })
                .orElse("redirect:/admin/cartes");
    }

    @PostMapping
    public String saveCarteBancaire(
            @Valid @ModelAttribute("carteDTO") CarteBancaireDTO carteDTO,
            BindingResult result,
            @RequestParam String action,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("action", action);
            return "admin/cartes/form";
        }
        
        try {
            if ("edit".equals(action)) {
                carteBancaireService.update(carteDTO.getId(), carteDTO);
                redirectAttributes.addFlashAttribute("successMessage", "Carte bancaire mise à jour avec succès");
            } else {
                carteBancaireService.save(carteDTO);
                redirectAttributes.addFlashAttribute("successMessage", "Carte bancaire créée avec succès");
            }
            return "redirect:/admin/cartes";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de l'enregistrement: " + e.getMessage());
            model.addAttribute("action", action);
            return "admin/cartes/form";
        }
    }

    @GetMapping("/{id}")
    public String showCarteBancaire(@PathVariable Long id, Model model) {
        return carteBancaireService.findById(id)
                .map(carteDTO -> {
                    model.addAttribute("carte", carteDTO);
                    return "admin/cartes/view";
                })
                .orElse("redirect:/admin/cartes");
    }

    @PostMapping("/{id}/delete")
    public String deleteCarteBancaire(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            carteBancaireService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Carte bancaire supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/admin/cartes";
    }
}
