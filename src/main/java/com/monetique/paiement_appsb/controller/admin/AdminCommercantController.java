package com.monetique.paiement_appsb.controller.admin;

import com.monetique.paiement_appsb.model.Commercant;
import com.monetique.paiement_appsb.service.CommercantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/admin/commercants")
public class AdminCommercantController {

    @Autowired
    private CommercantService commercantService;

    @GetMapping
    public String listCommercants(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) String secteur,
            @RequestParam(required = false) String success,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Commercant> commercants;
        
        if (nom != null || ville != null || secteur != null) {
            commercants = commercantService.findByNomContainingIgnoreCaseOrVilleContainingIgnoreCaseOrSecteurContainingIgnoreCase(nom, ville, secteur, pageable);
        } else {
            commercants = commercantService.findAll(pageable);
        }

        model.addAttribute("commercants", commercants.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", commercants.getTotalPages());
        model.addAttribute("nomSearch", nom);
        model.addAttribute("villeSearch", ville);
        model.addAttribute("secteurSearch", secteur);
        
        if (success != null) {
            model.addAttribute("successMessage", success);
        }
        
        return "admin/commercants/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("commercant", new Commercant());
        model.addAttribute("action", "create");
        return "admin/commercants/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Commercant> commercant = commercantService.findById(id);
        if (commercant.isPresent()) {
            model.addAttribute("commercant", commercant.get());
            model.addAttribute("action", "edit");
            return "admin/commercants/form";
        }
        return "redirect:/admin/commercants";
    }

    @PostMapping
    public String save(@Valid Commercant commercant, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", commercant.getId() == null ? "create" : "edit");
            return "admin/commercants/form";
        }
        try {
            commercantService.save(commercant);
            return "redirect:/admin/commercants?success=Le commerçant a été enregistré avec succès";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Une erreur est survenue lors de l'enregistrement: " + e.getMessage());
            model.addAttribute("action", commercant.getId() == null ? "create" : "edit");
            return "admin/commercants/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        commercantService.deleteById(id);
        return "redirect:/admin/commercants";
    }
}
