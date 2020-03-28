package cat.corredors.backoffice.users.crosscutting;

public class MemberStillRegisteredException extends MemberPreconditionException {
	private static final long serialVersionUID = -7674686314214561518L;

	public MemberStillRegisteredException(String uid) {
		super(BOUsersConstants.REST.ErrorCodes.ERR_015, "Member still registered", uid);
	}
}
