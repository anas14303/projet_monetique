package com.monetique.paiement_appsb.controller.admin;

import com.monetique.paiement_appsb.dto.PaiementDTO;
import com.monetique.paiement_appsb.model.*;
import com.monetique.paiement_appsb.service.*;

import org.springframework.beans.BeanUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/admin/transactions")
public class TransactionController {

    private final PaiementService paiementService;
    private final CarteBancaireService carteBancaireService;

    public TransactionController(PaiementService paiementService, CarteBancaireService carteBancaireService) {
        this.paiementService = paiementService;
        this.carteBancaireService = carteBancaireService;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                              @RequestParam String newStatus,
                              @RequestParam String comment,
                              RedirectAttributes redirectAttributes) {
        try {
            paiementService.updateStatus(id, newStatus, comment);
            redirectAttributes.addFlashAttribute("successMessage", "Statut mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour du statut");
        }
        return "redirect:/admin/transactions";
    }

    @GetMapping
    public String listTransactions(
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) Long montantMin,
            @RequestParam(required = false) Long montantMax,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String nomCommercant,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        try {
            // Récupérer les paiements avec pagination
            Page<Paiement> paiements = paiementService.searchPaiements(
                dateDebut, dateFin, montantMin, montantMax, statut, nomCommercant,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"))
            );
            
            model.addAttribute("paiements", paiements.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", paiements.getTotalPages());
            model.addAttribute("totalItems", paiements.getTotalElements());
            model.addAttribute("cartesBancaires", carteBancaireService.findAll());
            model.addAttribute("statuts", StatutPaiement.values());
            
            // Ajouter les paramètres de recherche actuels au modèle
            model.addAttribute("dateDebut", dateDebut);
            model.addAttribute("dateFin", dateFin);
            model.addAttribute("montantMin", montantMin);
            model.addAttribute("montantMax", montantMax);
            model.addAttribute("statut", statut);
            model.addAttribute("nomCommercant", nomCommercant);
            
            return "admin/transactions/list";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la recherche des transactions: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("paiementDTO", new PaiementDTO());
        model.addAttribute("action", "create");
        return "admin/transactions/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Paiement paiement = paiementService.getPaiement(id);
        PaiementDTO dto = new PaiementDTO();
        BeanUtils.copyProperties(paiement, dto);
        model.addAttribute("paiementDTO", dto);
        model.addAttribute("action", "edit");
        return "admin/transactions/form";
    }

    @GetMapping("/{id}")
    public String viewTransaction(@PathVariable Long id, Model model) {
        Paiement paiement = paiementService.getPaiement(id);
        model.addAttribute("paiement", paiement);
        model.addAttribute("historique", paiement.getHistoriqueStatuts());
        return "admin/transactions/view";
    }

    @PostMapping
    public String createPaiement(@Valid @ModelAttribute PaiementDTO dto, 
                                BindingResult result, 
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/transactions/form";
        }
        try {
            paiementService.saveFromDTO(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Paiement créé avec succès");
            return "redirect:/admin/transactions";
        } catch (Exception e) {
            result.reject("error.paiement.creation", e.getMessage());
            return "admin/transactions/form";
        }
    }

    @PostMapping("/{id}")
    public String updatePaiement(@PathVariable Long id,
                                @Valid @ModelAttribute PaiementDTO dto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/transactions/form";
        }
        try {
            paiementService.updateFromDTO(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Paiement mis à jour avec succès");
            return "redirect:/admin/transactions";
        } catch (Exception e) {
            result.reject("error.paiement.update", e.getMessage());
            return "admin/transactions/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deletePaiement(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            paiementService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Paiement supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression du paiement");
        }
        return "redirect:/admin/transactions";
    }
}
