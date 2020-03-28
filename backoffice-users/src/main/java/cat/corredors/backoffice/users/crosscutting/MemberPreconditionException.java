package cat.corredors.backoffice.users.crosscutting;

public class MemberPreconditionException extends BOUsersException {
	private static final long serialVersionUID = -5215530417972035898L;

	public MemberPreconditionException(int code, String message, Object... params) {
		super(code, message, params);
	}
}
