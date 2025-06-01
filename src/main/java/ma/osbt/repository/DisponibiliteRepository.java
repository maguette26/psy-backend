package ma.osbt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.osbt.entitie.Disponibilite;

public interface DisponibiliteRepository extends JpaRepository<Disponibilite,Long> {
	 List<Disponibilite> findByProfessionnelId(Long professionnelId);
}
