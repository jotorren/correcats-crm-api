package cat.corredors.backoffice.users.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import cat.corredors.backoffice.users.domain.AssociadaInfantil;

public interface AssociadaInfantilRepository extends PagingAndSortingRepository<AssociadaInfantil, String>, JpaSpecificationExecutor<AssociadaInfantil> {
	
	List<AssociadaInfantil> findByNickIgnoreCase(String nick);
	List<AssociadaInfantil> findByNickIgnoreCaseAndActivatTrue(String nick);
	List<AssociadaInfantil> findByEmailIgnoreCase(String email);
	Page<AssociadaInfantil> findByActivatTrue(Pageable pageable);
	List<AssociadaInfantil> findByActivatTrue(Sort sort);
}
