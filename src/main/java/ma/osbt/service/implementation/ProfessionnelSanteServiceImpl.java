package ma.osbt.service.implementation;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ma.osbt.entitie.ProfessionnelSanteMentale;
import ma.osbt.entitie.StatutValidation;
import ma.osbt.repository.ProfessionnelSanteMentaleRepository;
import ma.osbt.service.ProfessionnelSanteService;

 
@Service
public class ProfessionnelSanteServiceImpl implements ProfessionnelSanteService {

	@Autowired
    private ProfessionnelSanteMentaleRepository professionnelRepo;

    // Sauvegarder un professionnel avec document justificatif
    public ProfessionnelSanteMentale saveProfessionnel(ProfessionnelSanteMentale professionnel) {
        professionnel.setStatutValidation(StatutValidation.EN_ATTENTE);
        return professionnelRepo.save(professionnel);
    }

    // Lister les professionnels en attente de validation
    public List<ProfessionnelSanteMentale> getProfessionnelsEnAttente() {
        return professionnelRepo.findByStatutValidation(StatutValidation.EN_ATTENTE);
    }

    // Mettre à jour le statut validation
    public ProfessionnelSanteMentale updateStatutValidation(Long id, StatutValidation statut) {
        ProfessionnelSanteMentale p = professionnelRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Professionnel non trouvé"));
        p.setStatutValidation(statut);
        return professionnelRepo.save(p);
    }  
    @Override
    public List<ProfessionnelSanteMentale> getAllProfessionnels() {
        return professionnelRepo.findAll();
    }
    

}
