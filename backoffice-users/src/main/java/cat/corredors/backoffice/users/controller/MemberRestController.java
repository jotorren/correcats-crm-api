package cat.corredors.backoffice.users.controller;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import cat.corredors.backoffice.users.service.MemberService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberRestController implements MemberApi {
	
	private final MemberService service;
	private final BeanValidator beanValidator;
	private final Function<String, URI> internalIdToURI;
	
	@Override
	public ResponseEntity<ResponseData<PageBean<AssociadaListItem>>> listMembers(
			@RequestParam int offset, @RequestParam int limit,
			@RequestParam Optional<String> search, @RequestParam Optional<String> sortBy, @RequestParam Optional<Boolean> asc) {
		Page<Object> page = service.findAll(offset, limit, search, sortBy, asc.orElse(true));

		PageBean<AssociadaListItem> pageBean = new PageBean<AssociadaListItem>();
		pageBean.setOffset(offset);
		pageBean.setLimit(limit);
		pageBean.setTotal(page.getTotalElements());
		pageBean.setNumberOfElements(page.getNumberOfElements());
		pageBean.setIncluded(page.getContent().toArray(new AssociadaListItem[page.getNumberOfElements()]));

		return ResponseEntity.ok(new ResponseData<PageBean<AssociadaListItem>>(BOUsersConstants.REST.InfoCodes.INF_001, pageBean));
	}

	@Override
	public ResponseEntity<ResponseData<Associada>> getMember(@PathVariable String memberId)
			throws BOUserNotFoundException {
		return ResponseEntity.ok(new ResponseData<Associada>(BOUsersConstants.REST.InfoCodes.INF_001,
				service.findOne(memberId)));
	}

	@Override
	public ResponseEntity<ResponseData<Boolean>> isNickOk(@RequestParam String nick) throws BOUserNotFoundException, MemberNickAlreadyExistsException {
		return ResponseEntity.ok(new ResponseData<Boolean>(BOUsersConstants.REST.InfoCodes.INF_001,
				service.isNickAvailable(nick)));
	}

	@Override
	public ResponseEntity<ResponseData<Boolean>> isEmailOk(@RequestParam String email) throws MemberEmailAlreadyExistsException {
		return ResponseEntity.ok(new ResponseData<Boolean>(BOUsersConstants.REST.InfoCodes.INF_001,
				service.isEmailAvailable(email)));
	}
	
	@Override
	public ResponseEntity<ResponseData<Associada>> updateMember(
			@PathVariable String memberId, @RequestBody AssociadaForm data) 
					throws BOUserNotFoundException, MemberNickAlreadyExistsException, MemberEmailAlreadyExistsException {
		Associada entity = service.update(memberId, data);
		return ResponseEntity.ok(new ResponseData<Associada>(BOUsersConstants.REST.InfoCodes.INF_001, entity));
	}
	
	@Override
	public ResponseEntity<ResponseData<Associada>> registerMember(
			@PathVariable String memberId) throws BOUserNotFoundException, MemberAlreadyRegisteredException {
		Associada entity = service.register(memberId);
		return ResponseEntity.ok(new ResponseData<Associada>(BOUsersConstants.REST.InfoCodes.INF_001, entity));
	}
	
	@Override
	public ResponseEntity<ResponseData<Associada>> unregisterMember(@PathVariable String memberId) throws BOUserNotFoundException, MemberNotRegisteredException {
		Associada entity = service.unregister(memberId);
		return ResponseEntity.ok(new ResponseData<Associada>(BOUsersConstants.REST.InfoCodes.INF_001, entity));
	}

	@Override
	public ResponseEntity<ResponseData<Void>> deleteMember(@PathVariable String memberId) throws BOUserNotFoundException, MemberStillRegisteredException {
		service.delete(memberId);
		return ResponseEntity.ok(new ResponseData<Void>(BOUsersConstants.REST.InfoCodes.INF_001, null));
	}
	
	@Override
	public ResponseEntity<ResponseData<String>> registerMember(@RequestBody AssociadaForm data)
			throws BOUserNotFoundException, MemberNickAlreadyExistsException, MemberEmailAlreadyExistsException {
		beanValidator.validate(data);
		Associada entity = service.create(data);
		return ResponseEntity.created(internalIdToURI.apply(entity.getId()))
				.body(new ResponseData<String>(BOUsersConstants.REST.InfoCodes.INF_001, entity.getId()));
	}
}
