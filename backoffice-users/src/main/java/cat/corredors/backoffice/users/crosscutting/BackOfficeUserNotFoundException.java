package cat.corredors.backoffice.users.crosscutting;

public class BackOfficeUserNotFoundException extends BackOfficeUsersException {
	private static final long serialVersionUID = 3596609353597786528L;

	public BackOfficeUserNotFoundException(String uid) {
		super(BackOfficeUsersConstants.REST.ErrorCodes.ERR_011, "Unknown user", uid);
	}
}
