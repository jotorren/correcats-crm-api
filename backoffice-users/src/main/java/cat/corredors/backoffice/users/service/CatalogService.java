package cat.corredors.backoffice.users.service;

import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_CITIES_FOR_POSTAL_CODE;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_LIST_CITIES;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_POSTAL_CODES_FOR_CITY;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_SEARCH_CITIES;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import cat.corredors.backoffice.users.crosscutting.BOUsersConstants;
import cat.corredors.backoffice.users.crosscutting.BOUsersSystemFault;
import cat.corredors.backoffice.users.domain.CodiPostal;
import cat.corredors.backoffice.users.domain.Municipi;
import cat.corredors.backoffice.users.repository.CodiPostalRepository;
import cat.corredors.backoffice.users.repository.MunicipiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogService {

	private final MunicipiRepository municipisRepo;
	private final CodiPostalRepository codiPostalRepo;
	
	public Page<Municipi> findAllMunicipis(int offset, int limit, Optional<String> search) {
		try {
			int page = offset/limit;
			Pageable pageWithElements = PageRequest.of(page, limit, Sort.by("nom").ascending());

			return search
					.map(filter -> {
						return municipisRepo.findByNomContaining(filter, pageWithElements);
					})
					.orElse(municipisRepo.findAll(pageWithElements));
		} catch (DataAccessException e) {
			throw new BOUsersSystemFault(BOUsersConstants.REST.ErrorCodes.ERR_000, "System error querying municipis list",
					e, ERR_LIST_CITIES, e.getMessage());
		}
	}

	public List<Municipi> searchMunicipis(String search) {
		try {
			return municipisRepo.findByNomContaining(search);
		} catch (DataAccessException e) {
			throw new BOUsersSystemFault(BOUsersConstants.REST.ErrorCodes.ERR_000, "System error querying municipis list",
					e, ERR_SEARCH_CITIES, e.getMessage());
		}
	}
	
	public List<Municipi> findMunicipisForPostalCode(String codiPostal) {
		try {

			return codiPostalRepo.findByValorIgnoreCase(codiPostal)
					.stream()
					.map(cp -> municipisRepo.findById(cp.getMunicipi()).get())
					.collect(Collectors.toList());

		} catch (DataAccessException e) {
			throw new BOUsersSystemFault(BOUsersConstants.REST.ErrorCodes.ERR_000, "System error querying municipis list",
					e, ERR_CITIES_FOR_POSTAL_CODE, e.getMessage());
		}
	}
	
	public List<CodiPostal> findCodiPostals(String municipi) {
		try {
			return codiPostalRepo.findByMunicipiIgnoreCase(municipi);
		} catch (DataAccessException e) {
			throw new BOUsersSystemFault(BOUsersConstants.REST.ErrorCodes.ERR_000, "System error querying postal codes",
					e, ERR_POSTAL_CODES_FOR_CITY, e.getMessage());
		}
	}	
}
