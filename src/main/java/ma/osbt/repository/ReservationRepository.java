package ma.osbt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.osbt.entitie.ProfessionnelSanteMentale;
import ma.osbt.entitie.Reservation;
@Repository

public interface ReservationRepository extends JpaRepository<Reservation,Long>{
	 	   List<Reservation> findByProfessionnelId(Long professionnelId);
	   List<Reservation> findByProfessionnel(ProfessionnelSanteMentale professionnel);
	   List<Reservation> findByUtilisateurId(Long utilisateurId);

}
