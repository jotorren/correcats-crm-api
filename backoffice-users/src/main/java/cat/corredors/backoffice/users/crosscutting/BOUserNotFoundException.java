package cat.corredors.backoffice.users.crosscutting;

public class BOUserNotFoundException extends BOUsersException {
	private static final long serialVersionUID = 3596609353597786528L;

	public BOUserNotFoundException(String uid) {
		super(BOUsersConstants.REST.ErrorCodes.ERR_011, "Unknown user", uid);
	}
}
