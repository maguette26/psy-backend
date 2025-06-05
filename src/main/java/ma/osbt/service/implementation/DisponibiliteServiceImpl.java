package ma.osbt.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ma.osbt.entitie.Disponibilite;
import ma.osbt.entitie.ProfessionnelSanteMentale;
import ma.osbt.repository.DisponibiliteRepository;
import ma.osbt.repository.ProfessionnelSanteMentaleRepository;
import ma.osbt.service.DisponibiliteService;

@Service
public class DisponibiliteServiceImpl implements DisponibiliteService {

    @Autowired
    private DisponibiliteRepository disponibiliteRepository;

    @Autowired
    private ProfessionnelSanteMentaleRepository professionnelRepository;

    @Autowired
    private UtilisateurConnecteService utilisateurConnecteService;

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_" + role));
    }

    @Override
    public Disponibilite ajouterDisponibilite(Disponibilite disponibilite) {
        if (!(hasRole("PSYCHOLOGUE") || hasRole("PSYCHIATRE"))) {
            throw new RuntimeException("Accès refusé : rôle insuffisant");
        }

        String email = utilisateurConnecteService.getEmailConnecte();
        ProfessionnelSanteMentale professionnel = professionnelRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professionnel non trouvé"));

        disponibilite.setProfessionnel(professionnel);
        return disponibiliteRepository.save(disponibilite);
    }

    @Override
    public List<Disponibilite> getDisponibilitesProfessionnelConnecte() {
        if (!(hasRole("PSYCHOLOGUE") || hasRole("PSYCHIATRE"))) {
            throw new RuntimeException("Accès refusé : rôle insuffisant");
        }

        String email = utilisateurConnecteService.getEmailConnecte();
        ProfessionnelSanteMentale professionnel = professionnelRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professionnel non trouvé"));

        return disponibiliteRepository.findByProfessionnelId(professionnel.getId());
    }

    @Override
    public Disponibilite modifierDisponibilite(Long id, Disponibilite updated) {
        if (!(hasRole("PSYCHOLOGUE") || hasRole("PSYCHIATRE"))) {
            throw new RuntimeException("Accès refusé : rôle insuffisant");
        }

        String email = utilisateurConnecteService.getEmailConnecte();
        Disponibilite dispo = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilité non trouvée"));

        if (!dispo.getProfessionnel().getEmail().equals(email)) {
            throw new RuntimeException("Accès interdit");
        }

        dispo.setDate(updated.getDate());
        dispo.setHeureDebut(updated.getHeureDebut());
        dispo.setHeureFin(updated.getHeureFin());
        return disponibiliteRepository.save(dispo);
    }

    @Override
    public void supprimerDisponibilite(Long id) {
        if (!(hasRole("PSYCHOLOGUE") || hasRole("PSYCHIATRE"))) {
            throw new RuntimeException("Accès refusé : rôle insuffisant");
        }

        String email = utilisateurConnecteService.getEmailConnecte();
        Disponibilite dispo = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilité non trouvée"));

        if (!dispo.getProfessionnel().getEmail().equals(email)) {
            throw new RuntimeException("Accès interdit");
        }

        disponibiliteRepository.deleteById(id);
    }
}
