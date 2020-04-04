package cat.corredors.backoffice.users.controller;

import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.REST.DOWNLOAD_CONTENT_TYPE;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.REST.DOWNLOAD_FILE_NAME;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.REST.InfoCodes.INF_001;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
		Page<Object> page = service.findAll(offset, limit, search, sortBy, asc.orElse(true));

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

	@Override
	public void export(HttpServletResponse response, @RequestParam List<String> fields,
			@RequestParam Optional<String> sortBy, @RequestParam Optional<Boolean> asc) throws IOException {

		response.setContentType(DOWNLOAD_CONTENT_TYPE);
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + DOWNLOAD_FILE_NAME + "\"");
		response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");
		
		CSVPrinter printer = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT);
		try {
			printer.printRecord("SEP=,");
			for (Map<String, Object> data:service.findAll(fields, sortBy, asc.orElse(true))){
				printer.printRecord(data.values());
			}
		} finally {
			try {
				printer.close();
			} catch (IOException e) {
				log.warn(e.getMessage());
			}
		}
	}

	@Override
	public ResponseEntity<ResponseData<String>> exportAsync(@RequestParam List<String> fields,
			@RequestParam Optional<String> sortBy, @RequestParam Optional<Boolean> asc)
			throws IOException {
		String fileName = DOWNLOAD_FILE_NAME + "-" + System.currentTimeMillis();
		service.export(fields, sortBy, asc.orElse(true), fileName);
		return ResponseEntity.ok(new ResponseData<String>(INF_001, fileName));
	}

	@Override
	public Flux<String> liveUpdates() {		
		return service.liveUpdates();
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
}
