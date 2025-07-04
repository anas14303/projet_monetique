package com.monetique.paiement_appsb.service;

import com.monetique.paiement_appsb.model.Terminal;
import com.monetique.paiement_appsb.repository.TerminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TerminalService {

    @Autowired
    private TerminalRepository terminalRepository;

    public Terminal create(Terminal terminal) {
        return terminalRepository.save(terminal);
    }

    public Optional<Terminal> read(Long id) {
        return terminalRepository.findById(id);
    }

    public Terminal update(Terminal terminal) {
        return terminalRepository.save(terminal);
    }

    public void delete(Long id) {
        terminalRepository.deleteById(id);
    }

    public Page<Terminal> findAll(int page, int size) {
        return terminalRepository.findAll(PageRequest.of(page, size));
    }

    public Page<Terminal> findByModele(String modele, int page, int size) {
        return terminalRepository.findByModele(modele, PageRequest.of(page, size));
    }

    public Page<Terminal> findByCommercant(Long commercantId, int page, int size) {
        return terminalRepository.findByCommercantId(commercantId, PageRequest.of(page, size));
    }

    public Page<Terminal> findByStatut(String statut, int page, int size) {
        return terminalRepository.findByStatut(statut, PageRequest.of(page, size));
    }

    public Page<Terminal> filterTerminals(
            String modele,
            Long commercantId,
            String statut,
            int page,
            int size
    ) {
        return terminalRepository.findByModeleAndCommercantIdAndStatut(
                modele,
                commercantId,
                statut,
                PageRequest.of(page, size)
        );
    }
}
