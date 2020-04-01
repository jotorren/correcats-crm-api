package cat.corredors.backoffice.users.service;

import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_CHECK_EMAIL;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_CHECK_NICK;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_CREATE_MEMBER;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_DELETE_MEMBER;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_FIND_MEMBER;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_LIST_MEMBERS;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_REGISTER_MEMBER;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_UNREGISTER_MEMBER;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.REST.ErrorCodes.ERR_018;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.REST.ErrorCodes.PREFIX;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import cat.corredors.backoffice.users.crosscutting.BOUserNotFoundException;
import cat.corredors.backoffice.users.crosscutting.BOUsersConstants;
import cat.corredors.backoffice.users.crosscutting.BOUsersSystemFault;
import cat.corredors.backoffice.users.crosscutting.MemberAlreadyRegisteredException;
import cat.corredors.backoffice.users.crosscutting.MemberEmailAlreadyExistsException;
import cat.corredors.backoffice.users.crosscutting.MemberNickAlreadyExistsException;
import cat.corredors.backoffice.users.crosscutting.MemberNotRegisteredException;
import cat.corredors.backoffice.users.crosscutting.MemberStillRegisteredException;
import cat.corredors.backoffice.users.domain.Associada;
import cat.corredors.backoffice.users.domain.AssociadaForm;
import cat.corredors.backoffice.users.domain.AssociadaListItem;
import cat.corredors.backoffice.users.repository.AssociadaRepository;
import cat.corredors.backoffice.users.repository.AssociadaSpecificationsBuilder;
import cat.corredors.backoffice.users.repository.JoomlaRepository;
import cat.corredors.backoffice.users.repository.SearchOperation;
import cat.corredors.backoffice.users.repository.SpecSearchCriteria;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final AssociadaRepository repository;
	private final JoomlaRepository joomlaRepository;
	private final MessageSource messageSource;
	
	public Page<Object> findAll(int offset, int limit, Optional<String> search, Optional<String> sortBy, boolean asc) {
		try {
			int page = offset/limit;
			Pageable pageWithElements = sortBy
					.map(column -> asc?Sort.by(column).ascending():Sort.by(column).descending())
					.map(sort -> PageRequest.of(page, limit, sort))
					.orElse(PageRequest.of(page, limit, Sort.by("cognoms").ascending()));

			return search
					.map(value -> {
						AssociadaSpecificationsBuilder builder = new AssociadaSpecificationsBuilder();
						return repository.findAll(builder
							.with(new SpecSearchCriteria("cognoms", SearchOperation.CONTAINS, value))
							.with(new SpecSearchCriteria("'","nom", SearchOperation.CONTAINS, value))
							.with(new SpecSearchCriteria("'","nick", SearchOperation.CONTAINS, value))
							.with(new SpecSearchCriteria("activat", SearchOperation.EQUALITY, true))
							.build(), pageWithElements);
					})
					.orElse(repository.findByActivatTrue(pageWithElements))
					.map(all -> {
						AssociadaListItem item = new AssociadaListItem();
						BeanUtils.copyProperties(all, item);
						return item;						
					});
		} catch (DataAccessException e) {
			throw new BOUsersSystemFault(BOUsersConstants.REST.ErrorCodes.ERR_000, "System error querying members list",
					e, ERR_LIST_MEMBERS, e.getMessage());
		}
	}

	public Associada findOne(String memberId) throws BOUserNotFoundException {
		try {
			return repository.findById(memberId).orElseThrow(() -> new BOUserNotFoundException(memberId));
		} catch (DataAccessException e) {
			throw new BOUsersSystemFault(BOUsersConstants.REST.ErrorCodes.ERR_000,
					String.format("System error looking for member %s", memberId), e, ERR_FIND_MEMBER, e.getMessage());
		}
	}

	public boolean isNickAvailable(String nick) throws MemberNickAlreadyExistsException, BOUserNotFoundException {
		try {

			if (!this.repository.findByNickIgnoreCase(nick).isEmpty()) {
				throw new MemberNickAlreadyExistsException(nick);
			}

			if (null == joomlaRepository.getJoomlaUserId(nick)) {
				throw new BOUserNotFoundException(nick);
			}
			
			return true;
		} catch (DataAccessException e) {
			throw new BOUsersSystemFault(BOUsersConstants.REST.ErrorCodes.ERR_000,
					String.format("System error looking for nick %s", nick), e, ERR_CHECK_NICK, e.getMessage());
		}
	}
	
	public boolean isEmailAvailable(String email) throws MemberEmailAlreadyExistsException {
		try {

			if (!this.repository.findByEmailIgnoreCase(email).isEmpty()) {
				throw new MemberEmailAlreadyExistsException(email);
			}
			
			return true;
		} catch (DataAccessException e) {
			throw new BOUsersSystemFault(BOUsersConstants.REST.ErrorCodes.ERR_000,
					String.format("System error looking for email %s", email), e, ERR_CHECK_EMAIL, e.getMessage());
		}
	}

	public List<String> checkConsistency(String nick, String email) throws BOUserNotFoundException {
		
		List<String> failed = new ArrayList<String>();
				
		joomlaRepository.getJoomlaEmail(nick).map(joomlaEmail -> {
			if (!email.equalsIgnoreCase(joomlaEmail)) {
				failed.add(messageSource.getMessage(PREFIX + ERR_018, new Object[] { nick, joomlaEmail }, Locale.getDefault()));
			}
			
			return joomlaEmail;
		})
		.orElseThrow(() -> new BOUserNotFoundException(nick));
		
		return failed;
	}
	
	@Transactional
	public Associada register(String memberId) throws BOUserNotFoundException, MemberAlreadyRegisteredException {
		try {
			Associada member = repository.findById(memberId).orElseThrow(() -> new BOUserNotFoundException(memberId));
			
			if (member.getActivat()) {
				throw new MemberAlreadyRegisteredException(member.getNick());
			}
			
			return joomlaRepository.registerJoomlaUser(member.getNick())
					.map(joomlaUserId -> {
						member.setActivat(Boolean.TRUE);
						member.setDataBaixa(null);
						return this.repository.save(member);
					})
					.orElseThrow(() -> new BOUserNotFoundException(member.getNick()));
			
		} catch (DataAccessException e) {
			throw new BOUsersSystemFault(BOUsersConstants.REST.ErrorCodes.ERR_000,
					String.format("System error registering existent %s", memberId), e, ERR_REGISTER_MEMBER,
					e.getMessage());
		}
	}

	public Associada update(String memberId, AssociadaForm data) 
			throws BOUserNotFoundException, MemberNickAlreadyExistsException, MemberEmailAlreadyExistsException {
		try {
			Associada member = repository.findById(memberId).orElseThrow(() -> new BOUserNotFoundException(memberId));

			List<Associada> list = this.repository.findByNickIgnoreCase(data.getNick());
			if (!list.isEmpty() && list.stream().anyMatch(item -> !item.getId().equalsIgnoreCase(memberId))) {
				throw new MemberNickAlreadyExistsException(data.getNick());
			}

			list = this.repository.findByEmailIgnoreCase(data.getEmail());
			if (!list.isEmpty() && list.stream().anyMatch(item -> !item.getId().equalsIgnoreCase(memberId))) {
				throw new MemberEmailAlreadyExistsException(data.getEmail());
			}
			
			BeanUtils.copyProperties(data, member);
			return this.repository.save(member);
			
		} catch (DataAccessException e) {
			throw new BOUsersSystemFault(BOUsersConstants.REST.ErrorCodes.ERR_000,
					String.format("System error registering existent %s", memberId), e, ERR_REGISTER_MEMBER,
					e.getMessage());
		}
	}
	
	@Transactional
	public Associada unregister(String memberId) throws BOUserNotFoundException, MemberNotRegisteredException {
		try {
			Associada member = repository.findById(memberId).orElseThrow(() -> new BOUserNotFoundException(memberId));
			
			if (!member.getActivat()) {
				throw new MemberNotRegisteredException(member.getNick());
			}
			
			return joomlaRepository.unregisterJoomlaUser(member.getNick())
					.map(joomlaUserId -> {
						member.setActivat(Boolean.FALSE);
						member.setDataBaixa(new Date());
						return this.repository.save(member);
					})
					.orElseThrow(() -> new BOUserNotFoundException(member.getNick()));

		} catch (DataAccessException e) {
			throw new BOUsersSystemFault(BOUsersConstants.REST.ErrorCodes.ERR_000,
					String.format("System error unregistering %s", memberId), e, ERR_UNREGISTER_MEMBER,
					e.getMessage());
		}
	}

	public void delete(String memberId) throws BOUserNotFoundException, MemberStillRegisteredException {
		try {
			Associada member = repository.findById(memberId).orElseThrow(() -> new BOUserNotFoundException(memberId));
			
			if (member.getActivat()) {
				throw new MemberStillRegisteredException(member.getNick());
			}
			
			this.repository.delete(member);

		} catch (DataAccessException e) {
			throw new BOUsersSystemFault(BOUsersConstants.REST.ErrorCodes.ERR_000,
					String.format("System error deleting %s", memberId), e, ERR_DELETE_MEMBER,
					e.getMessage());
		}
	}
	
	@Transactional
	public Associada create(AssociadaForm data) throws BOUserNotFoundException, MemberNickAlreadyExistsException, MemberEmailAlreadyExistsException {
		try {
			if (!this.repository.findByNickIgnoreCase(data.getNick()).isEmpty()) {
				throw new MemberNickAlreadyExistsException(data.getNick());
			}

			if (!this.repository.findByEmailIgnoreCase(data.getEmail()).isEmpty()) {
				throw new MemberEmailAlreadyExistsException(data.getEmail());
			}

			return joomlaRepository.registerJoomlaUser(data.getNick())
					.map(joomlaUserId -> {
						Associada member = new Associada();
						BeanUtils.copyProperties(data, member);
						member.setActivat(Boolean.TRUE);
						member.setDataBaixa(null);
						return this.repository.save(member);
					})
					.orElseThrow(() -> new BOUserNotFoundException(data.getNick()));
			
		} catch (DataAccessException e) {
			throw new BOUsersSystemFault(BOUsersConstants.REST.ErrorCodes.ERR_000,
					String.format("System error registering new member %s", data.getNick()), e, ERR_CREATE_MEMBER,
					e.getMessage());
		}
	}
	
	public Map<String, Pair<String, String>> findInconsistentEmails() {
		Map<String, Pair<String, String>> list = new HashMap<String, Pair<String, String>>();
		
		repository.findAll(Sort.by("nick").ascending()).forEach(associada -> {
			joomlaRepository.getJoomlaEmail(associada.getNick())
				.map(joomlaEmail -> {
					if (!associada.getEmail().equalsIgnoreCase(joomlaEmail)) {
						list.put(associada.getNick(), Pair.of(associada.getEmail(), joomlaEmail));
					}					
					return joomlaEmail;
				})
				.orElseGet(() -> {
					list.put(associada.getNick(), Pair.of(associada.getEmail(), "notfound"));
					return null;
				});
		});
		
		return list;
	}	

	public Map<String, Pair<String, String>> findInconsistentNicks() {
		Map<String, Pair<String, String>> list = new HashMap<String, Pair<String, String>>();
		
		repository.findAll(Sort.by("nick").ascending()).forEach(associada -> {
			joomlaRepository.getJoomlaName(associada.getEmail())
				.ifPresent(joomlaName -> {
					if (!associada.getNick().equalsIgnoreCase(joomlaName)) {
						list.put(associada.getEmail(), Pair.of(associada.getNick(), joomlaName));
					}
				});
		});
		
		return list;
	}
}
