package ma.osbt.service;

import java.util.List;
import java.util.Optional;

import ma.osbt.entitie.Reservation;

public interface ReservationService {
	public List<Reservation> getAllReservations();
	 public Optional<Reservation> getReservationById(Long id);
	 public Reservation createReservation(Reservation reservation); 
	 public Reservation updateReservation(Long id, Reservation updatedReservation);
	  public void deleteReservation(Long id);
 }
