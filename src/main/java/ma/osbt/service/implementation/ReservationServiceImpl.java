package ma.osbt.service.implementation;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ma.osbt.entitie.Reservation;
import ma.osbt.repository.ReservationRepository;
import ma.osbt.service.ReservationService;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
	  @Autowired
	    private ReservationRepository reservationRepository;
	  public List<Reservation> getAllReservations() {
	        return reservationRepository.findAll();
	    }

	    public Optional<Reservation> getReservationById(Long id) {
	        return reservationRepository.findById(id);
	    }

	    public Reservation createReservation(Reservation reservation) {
	        reservation.setStatut("en_attente");  
	        return reservationRepository.save(reservation);
	    }

	    public Reservation updateReservation(Long id, Reservation updatedReservation) {
	        return reservationRepository.findById(id).map(res -> {
	            res.setPrix(updatedReservation.getPrix());
	            res.setStatut(updatedReservation.getStatut());
	            res.setDateReservation(updatedReservation.getDateReservation());
	            res.setUtilisateur(updatedReservation.getUtilisateur());
	            res.setProfessionnel(updatedReservation.getProfessionnel());
	            return reservationRepository.save(res);
	        }).orElse(null);
	    }

	    public void deleteReservation(Long id) {
	        reservationRepository.deleteById(id);
	    }
}

