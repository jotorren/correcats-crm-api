package cat.corredors.backoffice.users.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JoomlaRepository {

	private final @Qualifier("joomlaJdbcTemplate") JdbcTemplate jdbcTemplate;
	
	private String selectString(String sql, String value) {
		return jdbcTemplate.query(sql,
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, value);
				}
			}, new ResultSetExtractor<String>() {
				public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getString(1);
					}
					return null;
				}
			});
	}
	
	private Integer selectInteger(String sql, String value) {
		return jdbcTemplate.query(sql,
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, value);
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

	private Integer selectInteger(String sql, Integer value) {
		return jdbcTemplate.query(sql,
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setInt(1, value);
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
		jdbcTemplate.execute(query,
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
	
	public Integer getJoomlaUserId(String nick) {
		return selectInteger("SELECT id FROM e8yu3_users where name=?", nick);
	}

	public Optional<String> getJoomlaEmail(String nick) {
		return 	Optional.of(true).map(empty -> {
			return selectString("SELECT email FROM e8yu3_users WHERE name = ?", nick);
		});
	}

	public Optional<String> getJoomlaName(String email) {
		return 	Optional.of(true).map(empty -> {
			return selectString("SELECT name FROM e8yu3_users WHERE email = ?", email);
		});
	}
	
	public Boolean isAssociat(Integer uid) {
		return 1 == selectInteger("SELECT COUNT(*) FROM e8yu3_user_usergroup_map WHERE user_id = ? AND group_id = 10", uid);
	}

	public Boolean inDistributionList(Integer uid) {
		return 1 == selectInteger("SELECT COUNT(*) FROM e8yu3_acymailing_listsub WHERE listid = 2 AND subid IN " +
									"(SELECT subid FROM e8yu3_acymailing_subscriber WHERE userid = ?)", uid);
	}
	
	public Optional<Integer> registerJoomlaUser(String nick) {
		return Optional.ofNullable(getJoomlaUserId(nick))
				.map(joomlaUserId -> {
						if (!isAssociat(joomlaUserId)) {
							return updateJoomlaUser(
								"INSERT INTO e8yu3_user_usergroup_map(user_id, group_id) VALUES (?, 10)", joomlaUserId);
						}
						return joomlaUserId;
					})
				.map(joomlaUserId -> updateJoomlaUser(
						"UPDATE e8yu3_kunena_users SET group_id = 10 WHERE userid = ?", joomlaUserId))
				.map(joomlaUserId -> {
						if (!inDistributionList(joomlaUserId)) {
							return updateJoomlaUser(
								"INSERT INTO e8yu3_acymailing_listsub(listid,subid,subdate,unsubdate,status) " + 
										"SELECT 2,subid,UNIX_TIMESTAMP(),null,1 FROM e8yu3_acymailing_subscriber WHERE userid = ?", joomlaUserId);
						}
						return joomlaUserId;
					})
				;
	}

	public Optional<Integer> unregisterJoomlaUser(String nick) {
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
}
