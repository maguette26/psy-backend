package ma.osbt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import ma.osbt.entitie.Disponibilite;
import ma.osbt.entitie.DocumentJustificatif;
import ma.osbt.entitie.Personne;
import ma.osbt.entitie.ProfessionnelSanteMentale;
 

public interface ProfessionnelSanteService {
	
	 
	//crud professionnel
	public List<Personne> listProfessionnelSantes();
    //public List<ProfessionnelSanteMentale> listProfessionnelSante();
    public Optional<ProfessionnelSanteMentale> professionnelById(Long id);
    public void supprimerProfessionnel(Long id);
    public void ajouterProfessionnel(ProfessionnelSanteMentale p);
    public void modifierProfessionnel(ProfessionnelSanteMentale p);
    public List<ProfessionnelSanteMentale> professionnelsEnAttente();
    public void validerProfessionnel(Long id);
    public void refuserProfessionnel(Long id);
    //document 
    public void ajouterDocument(Long professionnelId, MultipartFile fichier);
    public List<DocumentJustificatif> getDocumentsParProfessionnel(Long professionnelId);
    //crud disponibilite
    public Disponibilite ajouterDisponibilite(Long professionnelId,Disponibilite disponibilite);
    public void supprimerDisponibilite(Long id);
    public void modifierDisponibilite(Long id,Disponibilite disponibilite);
    public List<Disponibilite> listeDisponibilitesParPro(Long professionnelId);
}

