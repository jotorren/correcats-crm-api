package cat.corredors.backoffice.users.crosscutting;

public class MemberNickAlreadyExistsException extends MemberPreconditionException {
	private static final long serialVersionUID = 6724302991163247757L;

	public MemberNickAlreadyExistsException(String nick) {
		super(BackOfficeUsersConstants.REST.ErrorCodes.ERR_016, "Nick already exists", nick);
	}
}
