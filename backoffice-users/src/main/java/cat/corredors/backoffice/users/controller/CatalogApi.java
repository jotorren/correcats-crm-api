package cat.corredors.backoffice.users.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants;
import cat.corredors.backoffice.users.domain.CodiPostal;
import cat.corredors.backoffice.users.domain.Municipi;
import io.swagger.annotations.ApiParam;

@Validated
@RequestMapping("/cataleg")
public interface CatalogApi {

	@GetMapping(
			value = "/municipi", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<PageBean<Municipi>>> listMunicipis(
    		@ApiParam(value = "List offset", required = true) @NotNull int offset, 
    		@ApiParam(value = "Page max number of elements", required = true) @NotNull int limit,
    		@ApiParam(value = "Value to search in name", required = false) Optional<String> search
    );

	@GetMapping(
			value = "/municipi/search", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<List<Municipi>>> searchMunicipis(
    		@ApiParam(value = "Value to search in name", required = true) @NotNull String search
    );
	
	@GetMapping(
			value = "/municipi/codipostal", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<List<Municipi>>> getMunicipis(
    		@ApiParam(value = "Postal code", required = true) @NotNull String codiPostal
    );
	
	@GetMapping(
			value = "/codipostal", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<List<CodiPostal>>> getCodisPostals(
    		@ApiParam(value = "Municipi code", required = true) @NotNull String municipi
    );
}
