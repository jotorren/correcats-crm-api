package cat.corredors.backoffice.users.controller;

import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.REST.DOWNLOAD_CONTENT_TYPE;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.REST.DOWNLOAD_FILE_NAME;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.REST.InfoCodes.INF_001;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
	
	@Override
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
	public ResponseEntity<ResponseData<Associada>> getMember(@PathVariable String memberId)
			throws BackOfficeUserNotFoundException {
		return ResponseEntity.ok(new ResponseData<Associada>(INF_001, service.findOne(memberId)));
	}

	@Override
	public ResponseEntity<ResponseData<Boolean>> isNickOk(@RequestParam String nick)
			throws BackOfficeUserNotFoundException, MemberNickAlreadyExistsException {
		return ResponseEntity.ok(new ResponseData<Boolean>(INF_001, service.isNickAvailable(nick)));
	}

	@Override
	public ResponseEntity<ResponseData<Boolean>> isEmailOk(@RequestParam String email)
			throws MemberEmailAlreadyExistsException {
		return ResponseEntity.ok(new ResponseData<Boolean>(INF_001, service.isEmailAvailable(email)));
	}

	@Override
	public ResponseEntity<ResponseData<List<String>>> checkConsistency(@RequestParam String nick,
			@RequestParam String email) throws BackOfficeUserNotFoundException {
		return ResponseEntity.ok(new ResponseData<List<String>>(INF_001, service.checkConsistency(nick, email)));
	}

	@Override
	public ResponseEntity<ResponseData<Map<String, Pair<String, String>>>> listInconsistentNicks() {
		return ResponseEntity
				.ok(new ResponseData<Map<String, Pair<String, String>>>(INF_001, service.findInconsistentNicks()));
	}

	@Override
	public ResponseEntity<ResponseData<Map<String, Pair<String, String>>>> listInconsistentEmails() {
		return ResponseEntity
				.ok(new ResponseData<Map<String, Pair<String, String>>>(INF_001, service.findInconsistentEmails()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResponseEntity<ResponseData<PageBean<Map<String, Object>>>> search(
			@RequestParam List<String> fields,
			@RequestBody List<SearchCriteria> search,
			@RequestParam Optional<String> logicalOperator,
			@RequestParam int offset, 
			@RequestParam int limit, 
			@RequestParam Optional<String> sortBy,
			@RequestParam Optional<Boolean> asc) throws IOException, ParseException {
		// TODO Remove ParseException throw
		
		// TODO Move convert to HTTPMessageConverter
		convert(search);
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
	public ResponseEntity<ResponseData<String>> export(
			@RequestParam int queryType,
			@RequestParam Optional<List<String>> fields,
			@RequestBody Optional<List<SearchCriteria>> search,
			@RequestParam Optional<String> logicalOperator,
			@RequestParam Optional<String> sortBy, @RequestParam Optional<Boolean> asc)
			throws IOException, MissingServletRequestParameterException, ParseException {
		// TODO Remove ParseException throw
		
		String fileName = DOWNLOAD_FILE_NAME + "-" + System.currentTimeMillis();
		
		switch(queryType) {
			case 0:
				service.export(fields.get(), Collections.emptyList(), null, sortBy, asc.orElse(true), fileName);
				break;
			case 1:
				if (!fields.isPresent()) {
					throw new MissingServletRequestParameterException("fields", "List<String>");
				}
				if (!search.isPresent()) {
					throw new MissingServletRequestParameterException("search", "List<SearchCriteria>");
				}
				// TODO Move convert to HTTPMessageConverter
				convert(search.get());
				service.export(fields.get(), search.get(), logicalOperator.orElse(null), sortBy, asc.orElse(true), fileName);
				break;
			case 2:
				service.exportInconsistentEmails(fileName);
				break;
			case 3:
				service.exportInconsistentNicks(fileName);
				break;
		}
		
		return ResponseEntity.ok(new ResponseData<String>(INF_001, fileName));
	}

	@Override
	public Flux<String> liveUpdates() {		
		return service.liveUpdates();
	}

	@Override
	public ResponseEntity<ResponseData<Boolean>> isReady(String file) {
		File f = new File(configuration.getExportDirectory(), file);
		return ResponseEntity.ok(new ResponseData<Boolean>(INF_001, f.exists() && f.canRead()));
	}
	
	@Override
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
	public ResponseEntity<ResponseData<Associada>> updateMember(@PathVariable String memberId,
			@RequestBody AssociadaForm data)
			throws BackOfficeUserNotFoundException, MemberNickAlreadyExistsException, MemberEmailAlreadyExistsException {
		Associada entity = service.update(memberId, data);
		return ResponseEntity.ok(new ResponseData<Associada>(INF_001, entity));
	}

	@Override
	public ResponseEntity<ResponseData<Associada>> registerMember(@PathVariable String memberId)
			throws BackOfficeUserNotFoundException, MemberAlreadyRegisteredException {
		Associada entity = service.register(memberId);
		return ResponseEntity.ok(new ResponseData<Associada>(INF_001, entity));
	}

	@Override
	public ResponseEntity<ResponseData<Associada>> unregisterMember(@PathVariable String memberId)
			throws BackOfficeUserNotFoundException, MemberNotRegisteredException {
		Associada entity = service.unregister(memberId);
		return ResponseEntity.ok(new ResponseData<Associada>(INF_001, entity));
	}

	@Override
	public ResponseEntity<ResponseData<Void>> deleteMember(@PathVariable String memberId)
			throws BackOfficeUserNotFoundException, MemberStillRegisteredException {
		service.delete(memberId);
		return ResponseEntity.ok(new ResponseData<Void>(INF_001, null));
	}

	@Override
	public ResponseEntity<ResponseData<String>> registerMember(@RequestBody AssociadaForm data)
			throws BackOfficeUserNotFoundException, MemberNickAlreadyExistsException, MemberEmailAlreadyExistsException {
		beanValidator.validate(data);
		Associada entity = service.create(data);
		return ResponseEntity.created(internalIdToURI.apply(entity.getId()))
				.body(new ResponseData<String>(INF_001, entity.getId()));
	}
	
	private static void convert(List<SearchCriteria> scList) throws ParseException {
    	for (SearchCriteria sc:scList) {
			if (sc.getKey().equals("dataAlta") || sc.getKey().equals("dataBaixa")) {
				// TODO Move date format to a constant or configuration property
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				sc.setValue(formatter.parse((String) sc.getValue()));
			} else if (sc.getKey().equals("activat")) {
				sc.setValue(Boolean.valueOf((String) sc.getValue()));
			} else if (sc.getKey().equals("quotaAlta")) {
				sc.setValue(Float.valueOf((String) sc.getValue()));
			} 
    	}		
	}
}
