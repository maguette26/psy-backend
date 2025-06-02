package ma.osbt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.osbt.entitie.Personne;
import ma.osbt.entitie.Role;

@Repository
public interface PersonneRepository extends JpaRepository<Personne,Long> {

	List<Personne> findByRoleIn(List<Role> roles);
	 


}