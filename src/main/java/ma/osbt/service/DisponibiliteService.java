package ma.osbt.service;

import java.util.List;

import ma.osbt.entitie.Disponibilite;

public interface DisponibiliteService {

	 public Disponibilite ajouterDisponibilite(Disponibilite disponibilite);
	 public void supprimerDisponibilite(Long id);
	 public Disponibilite modifierDisponibilite(Long id, Disponibilite updated);
	public List<Disponibilite> getDisponibilitesProfessionnelConnecte();

}
