package cat.corredors.backoffice.users.crosscutting;

public class MemberEmailAlreadyExistsException extends MemberPreconditionException {
	private static final long serialVersionUID = -2325616568438304898L;

	public MemberEmailAlreadyExistsException(String email) {
		super(BOUsersConstants.REST.ErrorCodes.ERR_017, "eMail adress already in use", email);
	}
}
