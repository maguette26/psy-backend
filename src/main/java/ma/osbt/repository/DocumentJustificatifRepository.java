package ma.osbt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.osbt.entitie.DocumentJustificatif;
@Repository

public interface DocumentJustificatifRepository extends JpaRepository<DocumentJustificatif,Long > {
	List<DocumentJustificatif> findByProfessionnelId(Long id);
}
