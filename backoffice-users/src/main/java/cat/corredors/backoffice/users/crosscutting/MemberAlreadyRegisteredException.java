package cat.corredors.backoffice.users.crosscutting;

public class MemberAlreadyRegisteredException extends MemberPreconditionException {
	private static final long serialVersionUID = -7674686314214561518L;

	public MemberAlreadyRegisteredException(String uid) {
		super(BOUsersConstants.REST.ErrorCodes.ERR_013, "Member already registered", uid);
	}
}
