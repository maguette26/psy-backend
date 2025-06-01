package ma.osbt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.osbt.entitie.Message;
import ma.osbt.entitie.Personne;
@Repository

public interface MessageRepository extends JpaRepository<Message,Long>{
	List<Message> findByExpediteurId(Long id);
    List<Message> findByDestinataireId(Long id);
    List<Message> findByExpediteurOrDestinataireOrderByDateDesc(Personne expediteur, Personne destinataire);

}
