package ma.osbt.service.implementation;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ma.osbt.entitie.Disponibilite;
import ma.osbt.entitie.DocumentJustificatif;
import ma.osbt.entitie.Personne;
import ma.osbt.entitie.ProfessionnelSanteMentale;
import ma.osbt.entitie.Role;
import ma.osbt.entitie.StatutValidation;
import ma.osbt.entitie.TypeDocumentJustificatif;
import ma.osbt.repository.DisponibiliteRepository;
import ma.osbt.repository.DocumentJustificatifRepository;
import ma.osbt.repository.PersonneRepository;
import ma.osbt.repository.ProfessionnelSanteMentaleRepository;
 import ma.osbt.service.ProfessionnelSanteService;

@Service
public class ProfessionnelSanteServiceImpl implements ProfessionnelSanteService {

    @Autowired
    private ProfessionnelSanteMentaleRepository professionnelRepository;
    @Autowired
    private DocumentJustificatifRepository documentRepository;
    private final String dossierUpload = "uploads/";
    
    @Autowired
    private DisponibiliteRepository disponibiliteRepository;
    
    @Autowired
    private PersonneRepository personneRepository;
    public List<Personne> listProfessionnelSantes() {
        List<Role> rolesProfessionnels = List.of(Role.PSYCHATRE, Role.PSYCHOLOGUE);
        return personneRepository.findByRoleIn(rolesProfessionnels);
    }

   // @Override
   // public List<ProfessionnelSanteMentale> listProfessionnelSante() {
    //    return professionnelRepository.findAll();
    //}

    @Override
    public Optional<ProfessionnelSanteMentale> professionnelById(Long id) {
        return professionnelRepository.findById(id);
    }

    @Override
    public void supprimerProfessionnel(Long id) {
        professionnelRepository.deleteById(id);
    }

    @Override
    public void ajouterProfessionnel(ProfessionnelSanteMentale p) {
        professionnelRepository.save(p);
    }

    @Override
    public void modifierProfessionnel(ProfessionnelSanteMentale p) {
        Optional<ProfessionnelSanteMentale> existant = professionnelRepository.findById(p.getId());
        if (existant.isPresent()) {
            ProfessionnelSanteMentale professionnel = existant.get();
            professionnel.setNom(p.getNom());
            professionnel.setPrenom(p.getPrenom());
            professionnel.setEmail(p.getEmail());
            professionnel.setRole(p.getRole());
            professionnel.setSpecialite(p.getSpecialite());
            professionnelRepository.save(professionnel);
        } else {
            throw new RuntimeException("Professionnel non trouvé");
        }
    }

    @Override
    public List<ProfessionnelSanteMentale> professionnelsEnAttente() {
        return professionnelRepository.findByStatutValidation(StatutValidation.EN_ATTENTE);
    }

    @Override
    public void validerProfessionnel(Long id) {
        professionnelRepository.findById(id).ifPresent(pro -> {
            pro.setStatutValidation(StatutValidation.VALIDE);
            professionnelRepository.save(pro);
        });
    }

    @Override
    public void refuserProfessionnel(Long id) {
        professionnelRepository.findById(id).ifPresent(pro -> {
            pro.setStatutValidation(StatutValidation.REFUSE);
            professionnelRepository.save(pro);
        });
    }
 
    @Override
    public void ajouterDocument(Long professionnelId, MultipartFile fichier) {
        try {
            ProfessionnelSanteMentale professionnel = professionnelRepository.findById(professionnelId)
                .orElseThrow(() -> new RuntimeException("Professionnel non trouvé"));

            File dossier = new File(dossierUpload);
            if (!dossier.exists()) {
                dossier.mkdirs();
            }

            String nomFichier = fichier.getOriginalFilename();
            Path chemin = Paths.get(dossierUpload + nomFichier);
            Files.write(chemin, fichier.getBytes());

            DocumentJustificatif document = new DocumentJustificatif();
            document.setNomFichier(nomFichier);
            document.setUrlStockage(chemin.toString());
            document.setValide(false);
            document.setType(TypeDocumentJustificatif.DIPLOME);  
            document.setProfessionnel(professionnel);

            documentRepository.save(document);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du stockage du fichier", e);
        }
    }

    @Override
    public List<DocumentJustificatif> getDocumentsParProfessionnel(Long professionnelId) {
        return documentRepository.findByProfessionnelId(professionnelId);
    }

	@Override
	public Disponibilite ajouterDisponibilite(Long professionnelId, Disponibilite disponibilite) {
		 ProfessionnelSanteMentale professionnel = professionnelRepository.findById(professionnelId)
	                .orElseThrow(() -> new RuntimeException("Professionnel non trouvé"));
	        disponibilite.setProfessionnel(professionnel);
	        return disponibiliteRepository.save(disponibilite);
	}

	@Override
	public void supprimerDisponibilite(Long id) {
		disponibiliteRepository.deleteById(id);
		
	}

	@Override
	public void modifierDisponibilite(Long id, Disponibilite disponibilite) {
		 Disponibilite disponibiliteModifier=disponibiliteRepository.findById(id).orElseThrow(() -> new RuntimeException("Disponibilité non trouvée"));
		 disponibiliteModifier.setDate(disponibilite.getDate());
		 disponibiliteModifier.setHeureDebut(disponibilite.getHeureDebut());
		 disponibiliteModifier.setHeureFin(disponibilite.getHeureFin());
		  disponibiliteRepository.save(disponibiliteModifier);
	}

	@Override
	public List<Disponibilite> listeDisponibilitesParPro(Long professionnelId) {
		return disponibiliteRepository.findByProfessionnelId(professionnelId);
	}
	
	
	 
	

}
