package cat.corredors.backoffice.users.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import cat.corredors.backoffice.users.domain.Associada;

public interface AssociadaRepository extends PagingAndSortingRepository<Associada, String>, JpaSpecificationExecutor<Associada>{
	
	List<Associada> findByNickIgnoreCase(String nick);
	List<Associada> findByNickIgnoreCaseAndActivatTrue(String nick);
	List<Associada> findByEmailIgnoreCase(String email);
	Page<Associada> findByActivatTrue(Pageable pageable);
	List<Associada> findByActivatTrue(Sort sort);
}
