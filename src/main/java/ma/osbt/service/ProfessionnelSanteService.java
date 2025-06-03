package ma.osbt.service;

import java.util.List;

import ma.osbt.entitie.ProfessionnelSanteMentale;
import ma.osbt.entitie.StatutValidation;

public interface ProfessionnelSanteService {
	
	public ProfessionnelSanteMentale saveProfessionnel(ProfessionnelSanteMentale professionnel);
	public    List<ProfessionnelSanteMentale> getProfessionnelsEnAttente();
	public   ProfessionnelSanteMentale updateStatutValidation(Long id, StatutValidation statut);
	//crud professionnel
	/*public List<Personne> listProfessionnelSantes();
     
    public Optional<ProfessionnelSanteMentale> professionnelById(Long id);
    public void supprimerProfessionnel(Long id);
    public void ajouterProfessionnel(ProfessionnelSanteMentale p);
    public void modifierProfessionnel(ProfessionnelSanteMentale p);
    public List<ProfessionnelSanteMentale> professionnelsEnAttente();
    public void validerProfessionnel(Long id);
    public void refuserProfessionnel(Long id);
  
    //crud disponibilite
    public Disponibilite ajouterDisponibilite(Long professionnelId,Disponibilite disponibilite);
    public void supprimerDisponibilite(Long id);
    public void modifierDisponibilite(Long id,Disponibilite disponibilite);
    public List<Disponibilite> listeDisponibilitesParPro(Long professionnelId);*/
}

