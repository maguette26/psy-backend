package ma.osbt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.osbt.entitie.Reservation;
@Repository

public interface ReservationRepository extends JpaRepository<Reservation,Long>{

}
