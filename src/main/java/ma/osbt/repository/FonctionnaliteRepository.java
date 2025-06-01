package ma.osbt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.osbt.entitie.Fonctionnalite;
@Repository

public interface FonctionnaliteRepository extends JpaRepository<Fonctionnalite,Long> {

}
