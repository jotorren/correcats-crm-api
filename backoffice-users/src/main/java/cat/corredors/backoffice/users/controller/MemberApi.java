package cat.corredors.backoffice.users.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cat.corredors.backoffice.users.crosscutting.BOUserNotFoundException;
import cat.corredors.backoffice.users.crosscutting.BOUsersConstants;
import cat.corredors.backoffice.users.crosscutting.MemberAlreadyRegisteredException;
import cat.corredors.backoffice.users.crosscutting.MemberEmailAlreadyExistsException;
import cat.corredors.backoffice.users.crosscutting.MemberNickAlreadyExistsException;
import cat.corredors.backoffice.users.crosscutting.MemberNotRegisteredException;
import cat.corredors.backoffice.users.crosscutting.MemberStillRegisteredException;
import cat.corredors.backoffice.users.domain.Associada;
import cat.corredors.backoffice.users.domain.AssociadaForm;
import cat.corredors.backoffice.users.domain.AssociadaListItem;
import io.swagger.annotations.ApiParam;

@Validated
@RequestMapping(BOUsersConstants.REST.Endpoints.API_BASE)
public interface MemberApi {

	@GetMapping(
			value = "/", 
			params = BOUsersConstants.REST.Endpoints.API_VERSION, 
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
			params = BOUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<Associada>> getMember(
    		@NotNull String memberId) 
    		throws BOUserNotFoundException;

	@GetMapping(
			value = "/nick", 
			params = BOUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<Boolean>> isNickOk(
    		@NotNull String nick)
    		throws BOUserNotFoundException, MemberNickAlreadyExistsException;

	@GetMapping(
			value = "/email", 
			params = BOUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<Boolean>> isEmailOk(
    		@NotNull String email) throws MemberEmailAlreadyExistsException;
	
	@GetMapping(
			value = "/consistency", 
			params = BOUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<List<String>>> checkConsistency(
    		@NotNull String nick,
    		@NotNull String email) throws BOUserNotFoundException;

	@GetMapping(
			value = "/consistency/nicks", 
			params = BOUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<Map<String, Pair<String, String>>>> listInconsistentNicks();

	@GetMapping(
			value = "/consistency/emails", 
			params = BOUsersConstants.REST.Endpoints.API_VERSION, 
			produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseData<Map<String, Pair<String, String>>>> listInconsistentEmails();
	
	@PutMapping(
			value = "/{memberId}", 
			params = BOUsersConstants.REST.Endpoints.API_VERSION,
			consumes = { MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE }
	)
	ResponseEntity<ResponseData<Associada>> updateMember(
			@NotNull String memberId, @NotNull AssociadaForm data) 
			throws BOUserNotFoundException, MemberNickAlreadyExistsException, MemberEmailAlreadyExistsException;
	
	@PutMapping(
			value = "/register/{memberId}", 
			params = BOUsersConstants.REST.Endpoints.API_VERSION, 
			produces = { MediaType.APPLICATION_JSON_VALUE }
	)
	ResponseEntity<ResponseData<Associada>> registerMember(
			@NotNull String memberId) 
			throws BOUserNotFoundException, MemberAlreadyRegisteredException;

	@PutMapping(
			value = "/unregister/{memberId}", 
			params = BOUsersConstants.REST.Endpoints.API_VERSION, 
			produces = { MediaType.APPLICATION_JSON_VALUE }
	)
	ResponseEntity<ResponseData<Associada>> unregisterMember(
			@NotNull String memberId) 
			throws BOUserNotFoundException, MemberNotRegisteredException;
	
	@DeleteMapping(
			value = "/{memberId}", 
			params = BOUsersConstants.REST.Endpoints.API_VERSION, 
			produces = { MediaType.APPLICATION_JSON_VALUE }
	)
	ResponseEntity<ResponseData<Void>> deleteMember(
			@NotNull String memberId) 
			throws BOUserNotFoundException, MemberStillRegisteredException;
	
	@PostMapping(
			value = "/", 
			params = BOUsersConstants.REST.Endpoints.API_VERSION,
			consumes = { MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE }
	)
	ResponseEntity<ResponseData<String>> registerMember(
			@NotNull AssociadaForm data) 
			throws BOUserNotFoundException, MemberNickAlreadyExistsException, MemberEmailAlreadyExistsException;
}
