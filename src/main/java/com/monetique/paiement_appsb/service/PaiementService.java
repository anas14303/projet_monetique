package com.monetique.paiement_appsb.service;

import lombok.extern.slf4j.Slf4j;
import com.monetique.paiement_appsb.dto.CarteBancaireDTO;
import com.monetique.paiement_appsb.dto.PaiementDTO;
import com.monetique.paiement_appsb.model.*;
import com.monetique.paiement_appsb.repository.PaiementRepository;
import com.monetique.paiement_appsb.repository.HistoriqueStatutPaiementRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.PageImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Slf4j
@Service
@Transactional(readOnly = true)
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;
    
    @Autowired
    private HistoriqueStatutPaiementRepository historiqueStatutPaiementRepository;
    
    @Autowired
    private CarteBancaireService carteBancaireService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private CommercantService commercantService;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.security.user.name:system}")
    private String systemUsername;

    // CRUD operations
    @Transactional
    public Paiement save(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    @Transactional
    public Paiement createTestPaiement() {
        try {
            // Créer un utilisateur de test si nécessaire
            Utilisateur utilisateur = utilisateurService.findByEmail("test@example.com")
                    .orElseGet(() -> {
                        Utilisateur u = new Utilisateur();
                        u.setEmail("test@example.com");
                        u.setNom("Test");
                        u.setPassword("$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi"); // password: password
                        return utilisateurService.save(u);
                    });

            // Créer une carte bancaire de test si nécessaire
            CarteBancaireDTO carteDTO;
            Optional<CarteBancaireDTO> carteOpt = carteBancaireService.findByNumeroMasked("4111111111111111");
            if (carteOpt.isPresent()) {
                carteDTO = carteOpt.get();
            } else {
                CarteBancaireDTO dto = new CarteBancaireDTO();
                dto.setNumeroMasked("4111111111111111");
                dto.setType("VISA");
                dto.setDateExp(new Date());
                dto.setUtilisateurId(utilisateur.getId());
                dto.setUtilisateurNomComplet(utilisateur.getNom());
                carteDTO = carteBancaireService.save(dto);
            }

            // Créer un commerçant de test si nécessaire
            Long commercantId = 1L; // ID du commerçant à utiliser
            Optional<Commercant> commercantOptional = commercantService.findById(commercantId);
            if (commercantOptional.isEmpty()) {
                throw new RuntimeException("Commercant not found with ID: " + commercantId);
            }
            Commercant commercant = commercantOptional.get();

            // Créer le paiement
            Paiement paiement = new Paiement();
            paiement.setMontant(new BigDecimal("100.00"));
            paiement.setDate(new Date());
            paiement.setStatut(StatutPaiement.PENDING);
            paiement.setCommercant(commercant);
            paiement.setUtilisateur(utilisateur);
            
            // Récupérer l'entité CarteBancaire à partir du DTO
            CarteBancaire carte = new CarteBancaire();
            carte.setId(carteDTO.getId());
            carte.setNumeroMasked(carteDTO.getNumeroMasked());
            carte.setType(carteDTO.getType());
            carte.setDateExp(carteDTO.getDateExp());
            carte.setUtilisateur(utilisateur);
            
            paiement.setCarteBancaire(carte);
            paiement.setTypeCarte("VISA");
            paiement.setCommentaire("Paiement de test");

            return save(paiement);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du paiement de test: " + e.getMessage(), e);
        }
    }
    
    public Page<Paiement> findAll(Pageable pageable) {
        return paiementRepository.findAll(pageable);
    }

    public Optional<Paiement> findById(Long id) {
        return paiementRepository.findById(id);
    }

    public Paiement saveFromDTO(PaiementDTO dto) {
        Paiement paiement = new Paiement();
        mapDTOToPaiement(dto, paiement);
        return save(paiement);
    }

    public Paiement updateFromDTO(Long id, PaiementDTO dto) {
        Optional<Paiement> existingPaiement = findById(id);
        if (existingPaiement.isEmpty()) {
            throw new RuntimeException("Paiement not found with ID: " + id);
        }
        Paiement paiement = existingPaiement.get();
        mapDTOToPaiement(dto, paiement);
        return save(paiement);
    }

    public void deleteById(Long id) {
        paiementRepository.deleteById(id);
    }

    private void mapDTOToPaiement(PaiementDTO dto, Paiement paiement) {
        paiement.setMontant(dto.getMontant());
        paiement.setDate(dto.getDate());
        paiement.setStatut(StatutPaiement.valueOf(dto.getStatut()));
        paiement.setTypeCarte(dto.getTypeCarte());
        paiement.setCommentaire(dto.getCommentaire());
        
        if (dto.getCommercantId() != null) {
            Optional<Commercant> commercant = commercantService.findById(dto.getCommercantId());
            if (commercant.isPresent()) {
                paiement.setCommercant(commercant.get());
            }
        }
        
        if (dto.getCarteBancaireId() != null) {
            Optional<CarteBancaireDTO> carteDtoOpt = carteBancaireService.findById(dto.getCarteBancaireId());
            if (carteDtoOpt.isPresent()) {
                CarteBancaireDTO carteDto = carteDtoOpt.get();
                CarteBancaire carte = new CarteBancaire();
                carte.setId(carteDto.getId());
                carte.setNumeroMasked(carteDto.getNumeroMasked());
                carte.setType(carteDto.getType());
                carte.setDateExp(carteDto.getDateExp());
                // Si vous avez besoin de plus de propriétés, ajoutez-les ici
                paiement.setCarteBancaire(carte);
            }
        }
    }

    public Paiement getPaiement(Long id) {
        return paiementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction non trouvée avec l'ID: " + id));
    }



    // Filtering methods
    public Page<Paiement> searchPaiements(String dateDebut, String dateFin, Long montantMin, Long montantMax, String statut, String nomCommercant, Pageable pageable) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Paiement> query = cb.createQuery(Paiement.class);
            Root<Paiement> root = query.from(Paiement.class);
            List<Predicate> predicates = new ArrayList<>();

            // Filtrer par date
            if (dateDebut != null && !dateDebut.trim().isEmpty()) {
                Date dateDebutObj = new SimpleDateFormat("yyyy-MM-dd").parse(dateDebut);
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), dateDebutObj));
            }
            if (dateFin != null && !dateFin.trim().isEmpty()) {
                Date dateFinObj = new SimpleDateFormat("yyyy-MM-dd").parse(dateFin);
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), dateFinObj));
            }

            // Filtrer par montant
            if (montantMin != null) {
                BigDecimal montantMinBD = new BigDecimal(montantMin);
                predicates.add(cb.greaterThanOrEqualTo(root.get("montant"), montantMinBD));
            }
            if (montantMax != null) {
                BigDecimal montantMaxBD = new BigDecimal(montantMax);
                predicates.add(cb.lessThanOrEqualTo(root.get("montant"), montantMaxBD));
            }

            // Filtrer par statut
            if (statut != null && !statut.trim().isEmpty() && !statut.equals(StatutPaiement.ALL.name())) {
                predicates.add(cb.equal(root.get("statut"), StatutPaiement.valueOf(statut)));
            }

            // Filtrer par nom du commerçant
            if (nomCommercant != null && !nomCommercant.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("commercant").get("nom"), nomCommercant));
            }

            query.where(predicates.toArray(new Predicate[0]));
            query.orderBy(cb.desc(root.get("date")));

            // Créer la requête paginée
            TypedQuery<Paiement> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());

            // Récupérer le nombre total d'éléments
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Paiement> countRoot = countQuery.from(Paiement.class);
            countQuery.select(cb.count(countRoot));
            countQuery.where(predicates.toArray(new Predicate[0]));
            long total = entityManager.createQuery(countQuery).getSingleResult();

            // Retourner la page avec les résultats
            return new PageImpl<>(typedQuery.getResultList(), pageable, total);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche des paiements: " + e.getMessage(), e);
        }
    }

    public Page<Paiement> filterPaiements(StatutPaiement statut, Commercant commercant, String typeCarte,
                                         Date startDate, Date endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return paiementRepository.filterPaiements(statut, commercant, typeCarte, startDate, endDate, pageable);
    }

    public Page<Paiement> findByDateRange(Date startDate, Date endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return paiementRepository.findByDateRange(startDate, endDate, pageable);
    }

    public Page<Paiement> findByCommercant(Commercant commercant, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return paiementRepository.findByCommercant(commercant, pageable);
    }

    public Page<Paiement> findByTypeCarte(String typeCarte, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return paiementRepository.findByTypeCarte(typeCarte, pageable);
    }

    public Page<Paiement> findByStatut(StatutPaiement statut, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return paiementRepository.findByStatut(statut, pageable);
    }
    
    /**
     * Met à jour le statut d'une transaction
     * @param id ID de la transaction à mettre à jour
     * @param newStatus Nouveau statut
     * @param comment Commentaire optionnel
     * @return La transaction mise à jour
     * @throws RuntimeException si la transaction n'est pas trouvée
     */
    @Transactional
    public Paiement updateStatus(Long id, String newStatus, String comment) {
        Paiement paiement = paiementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction non trouvée avec l'ID: " + id));
            
        // Convertir le nouveau statut en enum
        StatutPaiement newStatusEnum = StatutPaiement.valueOf(newStatus);
        
        // Sauvegarder l'ancien statut pour le journal
        StatutPaiement oldStatus = paiement.getStatut();
        
        // Mettre à jour le statut
        paiement.setStatut(newStatusEnum);
        
        // Journaliser le changement de statut
        log.info("Changement de statut pour le paiement {} : {} -> {}", 
                id, oldStatus, newStatusEnum);
        
        // Ajouter un commentaire si fourni
        if (comment != null && !comment.trim().isEmpty()) {
            String currentComment = paiement.getCommentaire() != null ? paiement.getCommentaire() + "\n" : "";
            paiement.setCommentaire(currentComment + "[" + newStatusEnum.name() + "] " + comment);
        }
        
        // Journaliser le changement de statut
        HistoriqueStatutPaiement historique = new HistoriqueStatutPaiement(
            paiement,
            oldStatus,
            newStatusEnum,
            comment,
            SecurityContextHolder.getContext().getAuthentication() != null ? 
                SecurityContextHolder.getContext().getAuthentication().getName() : 
                systemUsername
        );
        historiqueStatutPaiementRepository.save(historique);
        
        return paiementRepository.save(paiement);
    }
}
