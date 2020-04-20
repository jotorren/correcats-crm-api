package cat.corredors.backoffice.users.controller;

import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.REST.DOWNLOAD_CONTENT_TYPE;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.REST.DOWNLOAD_FILE_NAME;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.REST.InfoCodes.INF_001;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.Security.Roles.ADMIN;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.Security.Roles.JUNTA;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.Security.Roles.ORGANITZADORA;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.Security.Roles.SECRETARIA;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cat.corredors.backoffice.users.configuration.BackOfficeUsersConfigurationProperties;
import cat.corredors.backoffice.users.crosscutting.BackOfficeUserNotFoundException;
import cat.corredors.backoffice.users.crosscutting.MemberAlreadyRegisteredException;
import cat.corredors.backoffice.users.crosscutting.MemberEmailAlreadyExistsException;
import cat.corredors.backoffice.users.crosscutting.MemberNickAlreadyExistsException;
import cat.corredors.backoffice.users.crosscutting.MemberNotRegisteredException;
import cat.corredors.backoffice.users.crosscutting.MemberStillRegisteredException;
import cat.corredors.backoffice.users.domain.Associada;
import cat.corredors.backoffice.users.domain.AssociadaForm;
import cat.corredors.backoffice.users.domain.AssociadaInfantil;
import cat.corredors.backoffice.users.domain.AssociadaInfantilForm;
import cat.corredors.backoffice.users.domain.AssociadaListItem;
import cat.corredors.backoffice.users.domain.SearchCriteria;
import cat.corredors.backoffice.users.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberRestController implements MemberApi {

	private final MemberService service;
	private final BeanValidator beanValidator;
	private final Function<String, URI> internalIdToURI;
	private final BackOfficeUsersConfigurationProperties configuration;
	private final Supplier<List<String>> allAssociadaProperties;

	/**********************************************************************************************************
	 * Associada infantil                                                                                     *
	 **********************************************************************************************************/
	
	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "','" + ORGANITZADORA + "') ")
	public ResponseEntity<ResponseData<PageBean<AssociadaListItem>>> listMembers(@RequestParam int offset,
			@RequestParam int limit, @RequestParam Optional<String> search, @RequestParam Optional<String> sortBy,
			@RequestParam Optional<Boolean> asc) {
		Page<AssociadaListItem> page = service.findAll(offset, limit, search, sortBy, asc.orElse(true));

		PageBean<AssociadaListItem> pageBean = new PageBean<AssociadaListItem>();
		pageBean.setOffset(offset);
		pageBean.setLimit(limit);
		pageBean.setTotal(page.getTotalElements());
		pageBean.setNumberOfElements(page.getNumberOfElements());
		pageBean.setIncluded(page.getContent().toArray(new AssociadaListItem[page.getNumberOfElements()]));

		return ResponseEntity.ok(new ResponseData<PageBean<AssociadaListItem>>(INF_001, pageBean));
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "','" + ORGANITZADORA + "') ")
	public ResponseEntity<ResponseData<AssociadaInfantil>> getChildMember(@PathVariable String memberId)
			throws BackOfficeUserNotFoundException {
		return ResponseEntity.ok(new ResponseData<AssociadaInfantil>(INF_001, service.findOneChild(memberId)));
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "','" + ORGANITZADORA + "') ")
	public ResponseEntity<ResponseData<Boolean>> isNickChildOk(@RequestParam String nick)
			throws MemberNickAlreadyExistsException {
		return ResponseEntity.ok(new ResponseData<Boolean>(INF_001, service.isNickChildAvailable(nick)));
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "','" + ORGANITZADORA + "') ")
	public ResponseEntity<ResponseData<Boolean>> isResponsableChildOk(@RequestParam String nick)
			throws BackOfficeUserNotFoundException {
		return ResponseEntity.ok(new ResponseData<Boolean>(INF_001, service.isResponsableChildOk(nick)));
	}
	
	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "') ")
	public ResponseEntity<ResponseData<AssociadaInfantil>> updateChildMember(@PathVariable String memberId,
			@RequestBody AssociadaInfantilForm data)
			throws BackOfficeUserNotFoundException, MemberNickAlreadyExistsException {
		AssociadaInfantil entity = service.updateChild(memberId, data);
		return ResponseEntity.ok(new ResponseData<AssociadaInfantil>(INF_001, entity));
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "') ")
	public ResponseEntity<ResponseData<String>> registerChildMember(@RequestBody AssociadaInfantilForm data)
			throws MemberNickAlreadyExistsException {
		beanValidator.validate(data);
		AssociadaInfantil entity = service.createChild(data);
		return ResponseEntity.created(internalIdToURI.apply(entity.getId()))
				.body(new ResponseData<String>(INF_001, entity.getId()));
	}
	
	/**********************************************************************************************************
	 * Associada                                                                                              *
	 **********************************************************************************************************/
	
	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "','" + ORGANITZADORA + "') ")
	public ResponseEntity<ResponseData<Associada>> getMember(@PathVariable String memberId)
			throws BackOfficeUserNotFoundException {
		return ResponseEntity.ok(new ResponseData<Associada>(INF_001, service.findOne(memberId)));
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "','" + ORGANITZADORA + "') ")
	public ResponseEntity<ResponseData<Boolean>> isNickOk(@RequestParam String nick)
			throws BackOfficeUserNotFoundException, MemberNickAlreadyExistsException {
		return ResponseEntity.ok(new ResponseData<Boolean>(INF_001, service.isNickAvailable(nick)));
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "','" + ORGANITZADORA + "') ")
	public ResponseEntity<ResponseData<Boolean>> isEmailOk(@RequestParam String email)
			throws MemberEmailAlreadyExistsException {
		return ResponseEntity.ok(new ResponseData<Boolean>(INF_001, service.isEmailAvailable(email)));
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "') ")
	public ResponseEntity<ResponseData<List<String>>> checkConsistency(@RequestParam String nick,
			@RequestParam String email) throws BackOfficeUserNotFoundException {
		return ResponseEntity.ok(new ResponseData<List<String>>(INF_001, service.checkConsistency(nick, email)));
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "') ")
	public ResponseEntity<ResponseData<Map<String, Pair<String, String>>>> listInconsistentNicks() {
		return ResponseEntity
				.ok(new ResponseData<Map<String, Pair<String, String>>>(INF_001, service.findInconsistentNicks()));
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "') ")
	public ResponseEntity<ResponseData<Map<String, Pair<String, String>>>> listInconsistentEmails() {
		return ResponseEntity
				.ok(new ResponseData<Map<String, Pair<String, String>>>(INF_001, service.findInconsistentEmails()));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "') ")
	public ResponseEntity<ResponseData<PageBean<Map<String, Object>>>> search(
			@RequestParam List<String> fields,
			@RequestBody List<SearchCriteria> search,
			@RequestParam Optional<String> logicalOperator,
			@RequestParam int offset, 
			@RequestParam int limit, 
			@RequestParam Optional<String> sortBy,
			@RequestParam Optional<Boolean> asc) {
		
		for (SearchCriteria sc: search) {
			beanValidator.validate(sc);
		}
		
		Page<Map<String, Object>> page = service.findAll(fields, search, logicalOperator.orElse(null), offset, limit, sortBy, asc.orElse(true));
		PageBean<Map<String, Object>> pageBean = new PageBean<Map<String, Object>>();
		pageBean.setOffset(offset);
		pageBean.setLimit(limit);
		pageBean.setTotal(page.getTotalElements());
		pageBean.setNumberOfElements(page.getNumberOfElements());
		pageBean.setIncluded(page.getContent().toArray((Map<String, Object>[]) new Map[page.getNumberOfElements()]));

		return ResponseEntity.ok(new ResponseData<PageBean<Map<String, Object>>>(INF_001, pageBean));
	}
	
	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "') ")
	public ResponseEntity<ResponseData<String>> export(
			@RequestParam int queryType,
			@RequestParam Optional<List<String>> fields,
			@RequestBody Optional<List<SearchCriteria>> search,
			@RequestParam Optional<String> logicalOperator,
			@RequestParam Optional<String> sortBy, @RequestParam Optional<Boolean> asc)
			throws IOException, MissingServletRequestParameterException {
		String uuid = UUID.randomUUID().toString();
		String fileName = DOWNLOAD_FILE_NAME + "-" +uuid.substring(uuid.length()-5);
				
		switch(queryType) {
			case 0:
				service.export(fields.orElseGet(allAssociadaProperties), Collections.emptyList(), null, sortBy, asc.orElse(true), fileName);
				break;
			case 1:
				List<SearchCriteria> criteria = search.orElse(Collections.emptyList());
				criteria.forEach(sc -> { beanValidator.validate(sc); }); // Validate criteria				
				service.export(fields.orElseGet(allAssociadaProperties), criteria, logicalOperator.orElse(null), sortBy, asc.orElse(true), fileName);
				break;
			case 2:
				service.exportInconsistentEmails(fileName);
				break;
			case 3:
				service.exportInconsistentNicks(fileName);
				break;
			case 4:
				service.exportNotForumGroup(fields.orElseGet(allAssociadaProperties), fileName);
				break;
			case 5:
				service.exportForumGroupButNotMembers(fileName);
				break;
		
		}
		
		return ResponseEntity.ok(new ResponseData<String>(INF_001, fileName));
	}

	@Override
	public Flux<String> liveUpdates() {		
		return service.liveUpdates();
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "') ")
	public ResponseEntity<ResponseData<Boolean>> isReady(String file) {
		File f = new File(configuration.getExportDirectory(), file);
		return ResponseEntity.ok(new ResponseData<Boolean>(INF_001, f.exists() && f.canRead()));
	}
	
	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "','" + JUNTA + "') ")
	public void download(HttpServletResponse response, String file) throws IOException {
		File f = new File(configuration.getExportDirectory(), file);
		if (f.exists() && f.canRead()) {
			response.setContentType(DOWNLOAD_CONTENT_TYPE);
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + DOWNLOAD_FILE_NAME + "\"");
			response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");
			
			FileInputStream fr = null;
			try {
				fr = new FileInputStream(f);
				byte[] buff = new byte[1024];
				int read = 0;
				while ((read = fr.read(buff))>0) {
					response.getOutputStream().write(buff, 0, read);
				}
			} finally {
				if (null != fr) {
					try {
						fr.close();
					} catch (IOException e) {
						log.warn(e.getMessage());
					}
				}
			}
		}
	}
	
	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "') ")
	public ResponseEntity<ResponseData<Associada>> updateMember(@PathVariable String memberId,
			@RequestBody AssociadaForm data)
			throws BackOfficeUserNotFoundException, MemberNickAlreadyExistsException, MemberEmailAlreadyExistsException {
		Associada entity = service.update(memberId, data);
		return ResponseEntity.ok(new ResponseData<Associada>(INF_001, entity));
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "') ")
	public ResponseEntity<ResponseData<Associada>> registerMember(@PathVariable String memberId)
			throws BackOfficeUserNotFoundException, MemberAlreadyRegisteredException {
		Associada entity = service.register(memberId);
		return ResponseEntity.ok(new ResponseData<Associada>(INF_001, entity));
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "') ")
	public ResponseEntity<ResponseData<Associada>> unregisterMember(@PathVariable String memberId)
			throws BackOfficeUserNotFoundException, MemberNotRegisteredException {
		Associada entity = service.unregister(memberId);
		return ResponseEntity.ok(new ResponseData<Associada>(INF_001, entity));
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "','" + SECRETARIA + "') ")
	public ResponseEntity<ResponseData<String>> registerMember(@RequestBody AssociadaForm data)
			throws BackOfficeUserNotFoundException, MemberNickAlreadyExistsException, MemberEmailAlreadyExistsException {
		beanValidator.validate(data);
		Associada entity = service.create(data);
		return ResponseEntity.created(internalIdToURI.apply(entity.getId()))
				.body(new ResponseData<String>(INF_001, entity.getId()));
	}

	@Override
	@PreAuthorize("hasAnyAuthority('" + ADMIN + "') ")
	public ResponseEntity<ResponseData<Void>> deleteMember(@PathVariable String memberId)
			throws BackOfficeUserNotFoundException, MemberStillRegisteredException {
		service.delete(memberId);
		return ResponseEntity.ok(new ResponseData<Void>(INF_001, null));
	}
}
