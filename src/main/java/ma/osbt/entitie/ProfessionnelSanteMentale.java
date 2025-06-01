package ma.osbt.entitie;

import java.util.List;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data 
@NoArgsConstructor @AllArgsConstructor
@DiscriminatorValue(value="PROFESSIONNEL")
public class ProfessionnelSanteMentale extends Personne {
	private String specialite;
		
	@OneToMany(mappedBy = "professionnel")
	private List<Consultation> consultations;
	
	@OneToMany(mappedBy = "professionnel")
    private List<Reservation> reservations;
	
	@OneToMany(mappedBy = "expediteur")
    private List<Message> messagesEnvoyes;
	
    @OneToMany(mappedBy = "destinataire")
    private List<Message> messagesRecus;
    
    @Enumerated(EnumType.STRING)
    private StatutValidation statutValidation = StatutValidation.EN_ATTENTE;
    
    @OneToMany(mappedBy = "professionnel")
    private List<DocumentJustificatif> document;
    
    @OneToMany(mappedBy = "professionnel")
    private List<Disponibilite> disponibilites;
	 
}
