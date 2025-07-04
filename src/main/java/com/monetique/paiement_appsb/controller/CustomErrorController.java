package com.monetique.paiement_appsb.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * Contrôleur personnalisé pour la gestion des erreurs de l'application.
 * Ce contrôleur intercepte toutes les erreurs et affiche une page d'erreur appropriée
 * avec des détails utiles pour le débogage en environnement de développement.
 */
@Controller
public class CustomErrorController implements ErrorController {

    /**
     * Gère les erreurs et affiche une page d'erreur personnalisée.
     * 
     * @param request La requête HTTP
     * @param model Le modèle pour transmettre des données à la vue
     * @return Le nom de la vue d'erreur
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Récupérer les informations sur l'erreur
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        // Définir les valeurs par défaut
        int statusCode = 500;
        String errorMessage = "Une erreur inattendue s'est produite";
        String errorPath = path != null ? path.toString() : "Inconnu";
        
        // Déterminer le message d'erreur en fonction du code d'état
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorMessage = "La page demandée est introuvable";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorMessage = "Vous n'êtes pas autorisé à accéder à cette ressource";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorMessage = "Une erreur interne du serveur s'est produite";
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                errorMessage = "Requête incorrecte";
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                errorMessage = "Non autorisé - Authentification requise";
            }
        }
        
        // Ajouter les attributs au modèle
        model.addAttribute("status", statusCode);
        model.addAttribute("error", HttpStatus.valueOf(statusCode).getReasonPhrase());
        model.addAttribute("message", errorMessage);
        model.addAttribute("path", errorPath);
        model.addAttribute("timestamp", new Date());
        
        // Ajouter le message d'erreur au modèle s'il est disponible
        if (message != null && !message.toString().isEmpty()) {
            model.addAttribute("errorMessage", message.toString());
        }
        
        // Pour le débogage en environnement de développement
        if (exception != null) {
            model.addAttribute("exception", exception.toString());
        }
        
        return "error";
    }
    
    /**
     * Gère les erreurs d'accès refusé (403).
     * 
     * @return Le nom de la vue d'erreur 403
     */
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("error", "Accès refusé. Vous n'avez pas les autorisations nécessaires pour accéder à cette ressource.");
        model.addAttribute("status", 403);
        model.addAttribute("timestamp", new Date());
        return "error/403"; // Assurez-vous que ce chemin correspond à votre template Thymeleaf
    }
    
    /**
     * Méthode dépréciée pour la compatibilité avec les anciennes versions de Spring Boot.
     * 
     * @return Le chemin d'erreur
     * @deprecated Cette méthode est conservée pour la compatibilité avec les anciennes versions de Spring Boot
     */
    @Deprecated(since = "3.0.0", forRemoval = true)
    public String getErrorPath() {
        return "/error";
    }
}
