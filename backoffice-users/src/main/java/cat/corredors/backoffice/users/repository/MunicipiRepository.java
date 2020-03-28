package cat.corredors.backoffice.users.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import cat.corredors.backoffice.users.domain.Municipi;

public interface MunicipiRepository extends PagingAndSortingRepository<Municipi, String> {

	Page<Municipi> findByNomContaining(String text, Pageable page);
	List<Municipi> findByNomContaining(String text);
}
