package com.monetique.paiement_appsb.mapper;

import com.monetique.paiement_appsb.dto.CarteBancaireDTO;
import com.monetique.paiement_appsb.model.CarteBancaire;
import com.monetique.paiement_appsb.model.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CarteBancaireMapper {
    CarteBancaireMapper INSTANCE = Mappers.getMapper(CarteBancaireMapper.class);

    @Mapping(target = "utilisateurId", source = "utilisateur.id")
    @Mapping(target = "utilisateurNomComplet", source = "utilisateur", qualifiedByName = "mapUtilisateurNomComplet")
    @Mapping(target = "statut", source = "statut")
    @Mapping(target = "dateCreation", source = "dateCreation")
    CarteBancaireDTO toDto(CarteBancaire carteBancaire);

    @Mapping(target = "utilisateur", source = "utilisateurId", qualifiedByName = "mapUtilisateur")
    @Mapping(target = "statut", source = "statut", defaultValue = "ACTIVE")
    @Mapping(target = "dateCreation", ignore = true) // La date de création est gérée automatiquement
    CarteBancaire toEntity(CarteBancaireDTO dto);

    @Named("mapUtilisateur")
    default Utilisateur mapUtilisateur(Long utilisateurId) {
        if (utilisateurId == null) {
            return null;
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(utilisateurId);
        return utilisateur;
    }

    @Named("mapUtilisateurNomComplet")
    default String mapUtilisateurNomComplet(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }
        return utilisateur.getNom();
    }
}
