package ma.osbt.entitie;

import java.util.Date;
 
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor

public class Reservation  {
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private int prix;
	private String statut;
	private Date dateReservation;
	@ManyToOne
	@JoinColumn(name = "utilisateur_id", referencedColumnName = "id")
    private Utilisateur utilisateur;
	@ManyToOne
	@JoinColumn(name = "professionnel_id", referencedColumnName = "id")
	private ProfessionnelSanteMentale professionnel;
	@OneToOne(mappedBy = "reservation" )
	private Consultation consultation;
	

}
