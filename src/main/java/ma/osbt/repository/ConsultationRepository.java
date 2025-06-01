package ma.osbt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.osbt.entitie.Consultation;
@Repository

public interface ConsultationRepository  extends JpaRepository<Consultation, Long>{

}
