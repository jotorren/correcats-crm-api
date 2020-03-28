package cat.corredors.backoffice.users.service;

import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_CREATE_MEMBER;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_DELETE_MEMBER;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_FIND_MEMBER;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_LIST_MEMBERS;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_REGISTER_MEMBER;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_UNREGISTER_MEMBER;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_CHECK_NICK;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_CHECK_EMAIL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
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
import cat.corredors.backoffice.users.repository.SearchOperation;
import cat.corredors.backoffice.users.repository.SpecSearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

	private final AssociadaRepository repository;
	private final @Qualifier("joomlaJdbcTemplate") JdbcTemplate joomlaRepository;

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

			if (null == getJoomlaUserId(nick)) {
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
	
	private Integer getJoomlaUserId(String nick) {
		return joomlaRepository.query("SELECT id FROM e8yu3_users where name=?",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1, nick);
					}
				}, new ResultSetExtractor<Integer>() {
					public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							return resultSet.getInt(1);
						}
						return null;
					}
				});
	}
	
	private Integer updateJoomlaUser(String query, Integer joomlaUserId) {
		joomlaRepository.execute(query,
				new PreparedStatementCallback<Boolean>() {
					@Override
					public Boolean doInPreparedStatement(PreparedStatement ps)
							throws SQLException, DataAccessException {

						ps.setInt(1, joomlaUserId);
						return ps.execute();

					}
				});
		return joomlaUserId;
	}

	private Optional<Integer> registerJoomlaUser(String nick) throws BOUserNotFoundException {
		return Optional.ofNullable(getJoomlaUserId(nick))
				.map(joomlaUserId -> updateJoomlaUser(
						"INSERT INTO e8yu3_user_usergroup_map(user_id, group_id) VALUES (?, 10)", joomlaUserId))
				.map(joomlaUserId -> updateJoomlaUser(
						"UPDATE e8yu3_kunena_users SET group_id = 10 WHERE userid = ?", joomlaUserId))
				.map(joomlaUserId -> updateJoomlaUser(
						"INSERT INTO e8yu3_acymailing_listsub(listid,subid,subdate,unsubdate,status) " + 
								"SELECT 2,subid,UNIX_TIMESTAMP(),null,1 FROM e8yu3_acymailing_subscriber WHERE userid = ?", joomlaUserId))
				;
	}

	private Optional<Integer> unregisterJoomlaUser(String nick) throws BOUserNotFoundException {
		return Optional.ofNullable(getJoomlaUserId(nick))
				.map(joomlaUserId -> updateJoomlaUser(
						"DELETE FROM e8yu3_user_usergroup_map WHERE group_id = 10 AND user_id = ?", joomlaUserId))
				.map(joomlaUserId -> updateJoomlaUser(
						"UPDATE e8yu3_kunena_users SET group_id = 1 WHERE userid = ?", joomlaUserId))
				.map(joomlaUserId -> updateJoomlaUser(
						"DELETE FROM e8yu3_acymailing_listsub " +
//						"UPDATE e8yu3_acymailing_listsub SET unsubdate = UNIX_TIMESTAMP(), status = -1 " + 
								"WHERE listid = 2 AND subid IN (SELECT subid FROM e8yu3_acymailing_subscriber WHERE userid = ?)", joomlaUserId))
				;		
	}
	
	@Transactional
	public Associada register(String memberId) throws BOUserNotFoundException, MemberAlreadyRegisteredException {
		try {
			Associada member = repository.findById(memberId).orElseThrow(() -> new BOUserNotFoundException(memberId));
			
			if (member.getActivat()) {
				throw new MemberAlreadyRegisteredException(member.getNick());
			}
			
			return registerJoomlaUser(member.getNick())
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
			
			return unregisterJoomlaUser(member.getNick())
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

	@Transactional
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

			return registerJoomlaUser(data.getNick())
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
}
