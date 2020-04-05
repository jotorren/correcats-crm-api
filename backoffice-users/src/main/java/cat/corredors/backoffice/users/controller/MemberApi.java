package cat.corredors.backoffice.users.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cat.corredors.backoffice.users.crosscutting.BackOfficeUserNotFoundException;
import cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants;
import cat.corredors.backoffice.users.crosscutting.MemberAlreadyRegisteredException;
import cat.corredors.backoffice.users.crosscutting.MemberEmailAlreadyExistsException;
import cat.corredors.backoffice.users.crosscutting.MemberNickAlreadyExistsException;
import cat.corredors.backoffice.users.crosscutting.MemberNotRegisteredException;
import cat.corredors.backoffice.users.crosscutting.MemberStillRegisteredException;
import cat.corredors.backoffice.users.domain.Associada;
import cat.corredors.backoffice.users.domain.AssociadaForm;
import cat.corredors.backoffice.users.domain.AssociadaListItem;
import cat.corredors.backoffice.users.domain.SearchCriteria;
import io.swagger.annotations.ApiParam;
import reactor.core.publisher.Flux;

@Validated
@RequestMapping(BackOfficeUsersConstants.REST.Endpoints.API_BASE)
public interface MemberApi {

	@GetMapping(
			value = "/", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<PageBean<AssociadaListItem>>> listMembers(
    		@ApiParam(value = "List offset", required = true) @NotNull int offset, 
    		@ApiParam(value = "Page max number of elements", required = true) @NotNull int limit,
    		@ApiParam(value = "Value to search in all fields", required = false) Optional<String> search,
    		@ApiParam(value = "Field to sort results", required = false) Optional<String> sortBy,
    		@ApiParam(value = "Sort direction", required = false) Optional<Boolean> asc
    		);

	@GetMapping(
			value = "/{memberId}", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<Associada>> getMember(
    		@NotNull String memberId) 
    		throws BackOfficeUserNotFoundException;

	@GetMapping(
			value = "/nick", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<Boolean>> isNickOk(
    		@NotNull String nick)
    		throws BackOfficeUserNotFoundException, MemberNickAlreadyExistsException;

	@GetMapping(
			value = "/email", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<Boolean>> isEmailOk(
    		@NotNull String email) throws MemberEmailAlreadyExistsException;
	
	@GetMapping(
			value = "/consistency", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<List<String>>> checkConsistency(
    		@NotNull String nick,
    		@NotNull String email) throws BackOfficeUserNotFoundException;

	@GetMapping(
			value = "/consistency/nicks", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<Map<String, Pair<String, String>>>> listInconsistentNicks();

	@GetMapping(
			value = "/consistency/emails", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<Map<String, Pair<String, String>>>> listInconsistentEmails();

	@PostMapping(
			value = "/search", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ResponseData<PageBean<Map<String, Object>>>> search(
			@NotNull List<String> fields,
			@NotNull List<SearchCriteria> search,
			Optional<String> logicalOperator,
    		@NotNull int offset, 
    		@NotNull int limit,
    		Optional<String> sortBy,
    		Optional<Boolean> asc   		
    		);
	
	@PostMapping(
			value = "/export", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ResponseData<String>> export(
			@NotNull int queryType,
			Optional<List<String>> fields,
    		Optional<List<SearchCriteria>> search,
    		Optional<String> logicalOperator,
    		Optional<String> sortBy,
    		Optional<Boolean> asc    		
    		) throws IOException, MissingServletRequestParameterException;
	
	@GetMapping(
			value = "/export/live", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	Flux<String> liveUpdates();

	@GetMapping(
			value = "/export/ready", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ResponseData<Boolean>> isReady(@NotNull String file);
	
	@GetMapping(
			value = "/download", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    void download(
    		HttpServletResponse response,
    		@NotNull String file
    ) throws IOException;
	
	@PutMapping(
			value = "/{memberId}", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION,
			consumes = { MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE }
	)
	ResponseEntity<ResponseData<Associada>> updateMember(
			@NotNull String memberId, @NotNull AssociadaForm data) 
			throws BackOfficeUserNotFoundException, MemberNickAlreadyExistsException, MemberEmailAlreadyExistsException;
	
	@PutMapping(
			value = "/register/{memberId}", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = { MediaType.APPLICATION_JSON_VALUE }
	)
	ResponseEntity<ResponseData<Associada>> registerMember(
			@NotNull String memberId) 
			throws BackOfficeUserNotFoundException, MemberAlreadyRegisteredException;

	@PutMapping(
			value = "/unregister/{memberId}", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = { MediaType.APPLICATION_JSON_VALUE }
	)
	ResponseEntity<ResponseData<Associada>> unregisterMember(
			@NotNull String memberId) 
			throws BackOfficeUserNotFoundException, MemberNotRegisteredException;
	
	@DeleteMapping(
			value = "/{memberId}", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION, 
			produces = { MediaType.APPLICATION_JSON_VALUE }
	)
	ResponseEntity<ResponseData<Void>> deleteMember(
			@NotNull String memberId) 
			throws BackOfficeUserNotFoundException, MemberStillRegisteredException;
	
	@PostMapping(
			value = "/", 
			params = BackOfficeUsersConstants.REST.Endpoints.API_VERSION,
			consumes = { MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE }
	)
	ResponseEntity<ResponseData<String>> registerMember(
			@NotNull AssociadaForm data) 
			throws BackOfficeUserNotFoundException, MemberNickAlreadyExistsException, MemberEmailAlreadyExistsException;
}
