package com.monetique.paiement_appsb.mapper;

import com.monetique.paiement_appsb.dto.CommercantDTO;
import com.monetique.paiement_appsb.dto.TerminalDTO;
import com.monetique.paiement_appsb.model.Commercant;
import com.monetique.paiement_appsb.model.Terminal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommercantMapper {
    CommercantMapper INSTANCE = Mappers.getMapper(CommercantMapper.class);

    @Mapping(target = "terminaux", source = "terminaux", qualifiedByName = "mapTerminaux")
    CommercantDTO toDto(Commercant commercant);

    @Mapping(target = "terminaux", ignore = true)
    Commercant toEntity(CommercantDTO dto);

    @Named("mapTerminaux")
    default List<TerminalDTO> mapTerminaux(List<Terminal> terminaux) {
        if (terminaux == null) {
            return null;
        }
        return terminaux.stream()
                .map(terminal -> {
                    TerminalDTO dto = new TerminalDTO();
                    dto.setId(terminal.getId());
                    dto.setModele(terminal.getModele());
                    dto.setStatut(terminal.getStatut());
                    if (terminal.getCommercant() != null) {
                        dto.setCommercantId(terminal.getCommercant().getId());
                        dto.setCommercantNom(terminal.getCommercant().getNom());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
