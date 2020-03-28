package cat.corredors.backoffice.users.crosscutting;

public class MemberNotRegisteredException extends MemberPreconditionException {
	private static final long serialVersionUID = 576110614433012172L;

	public MemberNotRegisteredException(String uid) {
		super(BOUsersConstants.REST.ErrorCodes.ERR_014, "Member not registered", uid);
	}
}
