package cat.corredors.backoffice.users.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import cat.corredors.backoffice.users.domain.CodiPostal;

public interface CodiPostalRepository extends PagingAndSortingRepository<CodiPostal, Integer> {

	List<CodiPostal> findByValorIgnoreCase(String municipi);
	List<CodiPostal> findByMunicipiIgnoreCase(String municipi);
}
