package cat.corredors.backoffice.users.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants;
import cat.corredors.backoffice.users.domain.CodiPostal;
import cat.corredors.backoffice.users.domain.Municipi;
import cat.corredors.backoffice.users.service.CatalogService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CatalogRestController implements CatalogApi {

	private final CatalogService service;

	@Override
	public ResponseEntity<ResponseData<PageBean<Municipi>>> listMunicipis(
			@RequestParam int offset,
			@RequestParam int limit, 
			@RequestParam Optional<String> search) {

		Page<Municipi> page = service.findAllMunicipis(offset, limit, search);

		PageBean<Municipi> pageBean = new PageBean<Municipi>();
		pageBean.setOffset(offset);
		pageBean.setLimit(limit);
		pageBean.setTotal(page.getTotalElements());
		pageBean.setNumberOfElements(page.getNumberOfElements());
		pageBean.setIncluded(page.getContent().toArray(new Municipi[page.getNumberOfElements()]));

		return ResponseEntity.ok(new ResponseData<PageBean<Municipi>>(BackOfficeUsersConstants.REST.InfoCodes.INF_001, pageBean));
	}

	@Override
	public ResponseEntity<ResponseData<List<Municipi>>> searchMunicipis(@RequestParam String search) {
		return ResponseEntity.ok(new ResponseData<List<Municipi>>(BackOfficeUsersConstants.REST.InfoCodes.INF_001,
				service.searchMunicipis(search)));
	}
	
	@Override
	public ResponseEntity<ResponseData<List<Municipi>>> getMunicipis(
			@RequestParam String codiPostal) {

		return ResponseEntity.ok(new ResponseData<List<Municipi>>(BackOfficeUsersConstants.REST.InfoCodes.INF_001, 
				service.findMunicipisForPostalCode(codiPostal)));
	}

	@Override
	public ResponseEntity<ResponseData<List<CodiPostal>>> getCodisPostals(
			@RequestParam String municipi) {

		return ResponseEntity.ok(new ResponseData<List<CodiPostal>>(BackOfficeUsersConstants.REST.InfoCodes.INF_001, 
				service.findCodiPostals(municipi)));
	}

}
