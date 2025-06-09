package ma.osbt.entitie;

import java.time.LocalTime;
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

public class Consultation {
	@Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)	
private Long idConsultation;
private Date dateConsultation;
private LocalTime heure; 
private Double prix;

@ManyToOne()
private ProfessionnelSanteMentale professionnel;
@OneToOne
@JoinColumn(name = "reservation_id")
private Reservation reservation;
}
