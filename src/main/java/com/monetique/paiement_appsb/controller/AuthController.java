package com.monetique.paiement_appsb.controller;

import com.monetique.paiement_appsb.dto.LoginRequest;
import com.monetique.paiement_appsb.dto.RegisterRequest;
import com.monetique.paiement_appsb.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Gère la page de connexion
     * @param error Paramètre d'erreur (optionnel)
     * @param logout Paramètre de déconnexion (optionnel)
     * @param expired Paramètre d'expiration de session (optionnel)
     * @param model Modèle pour transmettre des attributs à la vue
     * @return Le nom de la vue à afficher
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "expired", required = false) String expired,
            @RequestParam(value = "denied", required = false) String denied,
            @RequestParam(value = "redirect", required = false) String redirectUrl,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Identifiants invalides. Veuillez réessayer.");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Déconnexion réussie. À bientôt !");
        }
        
        if (expired != null) {
            model.addAttribute("message", "Votre session a expiré. Veuillez vous reconnecter.");
        }
        
        if (denied != null) {
            model.addAttribute("error", "Accès refusé. Veuillez vous connecter avec un compte autorisé.");
        }
        
        // Conserver l'URL de redirection après la connexion
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            model.addAttribute("redirectUrl", redirectUrl);
        }
        
        return "auth/login";
    }

    /**
     * Affiche le formulaire d'inscription
     * @param model Modèle pour transmettre les données à la vue
     * @return Le nom de la vue d'inscription
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    /**
     * Traite la soumission du formulaire d'inscription
     * @param registerRequest Les données du formulaire d'inscription
     * @param result Résultat de la validation
     * @param redirectAttributes Attributs pour la redirection
     * @return Redirection vers la page de connexion en cas de succès, ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model,
                             HttpServletRequest request) {
        
        System.out.println("\n=== [REGISTER] Début du traitement de la requête ===");
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Méthode: " + request.getMethod());
        System.out.println("Headers: " + Collections.list(request.getHeaderNames())
                .stream()
                .map(h -> h + ": " + request.getHeader(h))
                .collect(Collectors.joining(", ")));
        
        System.out.println("\nDonnées du formulaire reçues :");
        System.out.println("- Username: " + registerRequest.getUsername());
        System.out.println("- FullName: " + registerRequest.getFullName());
        System.out.println("- Email: " + registerRequest.getEmail());
        System.out.println("- Password: [PROTECTED]");
        System.out.println("- ConfirmPassword: [PROTECTED]");
        
        // Vérification des erreurs de validation
        if (result.hasErrors()) {
            System.out.println("\nErreurs de validation détectées :");
            result.getAllErrors().forEach(error -> {
                System.out.println("- " + error.getDefaultMessage());
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    System.out.println("  Champ: " + fieldError.getField());
                    System.out.println("  Valeur rejetée: " + fieldError.getRejectedValue());
                }
            });
            model.addAttribute("error", "Veuillez corriger les erreurs ci-dessous.");
            return "auth/register";
        }

        // Vérification de la correspondance des mots de passe
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            System.out.println("Erreur : Les mots de passe ne correspondent pas");
            result.rejectValue("confirmPassword", "error.confirmPassword", "Les mots de passe ne correspondent pas");
            model.addAttribute("error", "Les mots de passe ne correspondent pas.");
            return "auth/register";
        }

        try {
            // Enregistrement de l'utilisateur
            authService.registerNewUser(registerRequest);
            redirectAttributes.addFlashAttribute("success", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            
            // Messages d'erreur plus conviviaux
            if (errorMessage.contains("déjà utilisé")) {
                if (errorMessage.contains("nom d'utilisateur")) {
                    result.rejectValue("username", "error.username", errorMessage);
                } else if (errorMessage.contains("email")) {
                    result.rejectValue("email", "error.email", errorMessage);
                }
            } else {
                // Pour les autres erreurs, afficher un message générique
                errorMessage = "Une erreur est survenue lors de l'inscription. Veuillez réessayer.";
                System.err.println("Erreur lors de l'inscription: " + e.getMessage());
                e.printStackTrace();
            }
            
            model.addAttribute("error", errorMessage);
            return "auth/register";
        }
    }
    
    /**
     * Gère la soumission du formulaire de connexion
     * @param loginRequest Les informations de connexion
     * @param result Résultat de la validation
     * @param redirectUrl URL de redirection après connexion réussie
     * @param model Modèle pour transmettre des attributs à la vue
     * @return Redirection vers le tableau de bord ou affichage du formulaire avec les erreurs
     */
    @PostMapping("/login")
    public String loginUser(
            @Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
            BindingResult result,
            @RequestParam(value = "redirect", required = false) String redirectUrl,
            HttpServletRequest request,
            Model model) {
        
        // Vérification des erreurs de validation
        if (result.hasErrors()) {
            model.addAttribute("loginRequest", loginRequest);
            return "auth/login";
        }
        
        try {
            // Tenter l'authentification via le service
            Authentication authentication = authService.authenticate(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            );
            
            // Définir l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Créer la session si nécessaire
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            
            // Vérifier si l'utilisateur a le rôle requis
            boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                
            // Rediriger en fonction du rôle et de l'URL de redirection
            if (redirectUrl != null && !redirectUrl.isEmpty() && !redirectUrl.contains("error")) {
                return "redirect:" + redirectUrl;
            } else if (isAdmin) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/user/dashboard";
            }
            
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Identifiants invalides. Veuillez réessayer.");
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("error", "Une erreur est survenue lors de la connexion : " + e.getMessage());
            return "auth/login";
        }
    }
}
