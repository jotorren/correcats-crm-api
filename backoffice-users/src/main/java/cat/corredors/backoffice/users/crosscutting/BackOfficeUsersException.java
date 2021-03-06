package cat.corredors.backoffice.users.crosscutting;

public class BackOfficeUsersException extends Exception {
	private static final long serialVersionUID = -3043893514096899580L;

	private Object[] params;
	private int code;
	
	/**
	 * 
	 * Default constructor
	 * 
	 * @param code
	 *            error code
	 * @param message
	 *            default message
	 */
	public BackOfficeUsersException(int code, String message) {
		super(message);
		this.code = code;
	}

	/**
	 * 
	 * Constructor from a captured exception
	 * 
	 * @param code
	 *            error code
	 * @param message
	 *            default message
	 * @param cause
	 *            captured exception, error origin
	 */
	public BackOfficeUsersException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	/**
	 * 
	 * Constructor from a code and a set of parameters to use when building the
	 * corresponding message
	 * 
	 * @param code
	 *            error code
	 * @param message
	 *            default message
	 * @param params
	 *            the array of parameters to build the corresponding message
	 */
	public BackOfficeUsersException(int code, String message, Object... params) {
		super(message);
		this.code = code;
		this.params = params;
	}

	/**
	 * 
	 * Constructor from a captured exception and a set of parameters to use when
	 * building the corresponding message
	 * 
	 * @param code
	 *            error code
	 * @param message
	 *            default message
	 * @param cause
	 *            captured exception, error origin
	 * @param params
	 *            the array of parameters to build the corresponding message
	 */
	public BackOfficeUsersException(int code, String message, Throwable cause, Object... params) {
		super(message, cause);
		this.code = code;
		this.params = params;
	}

	/**
	 * 
	 * Returns the array of parameters to build the corresponding message
	 * 
	 * @return the array of parameters set during creation
	 */
	public Object[] getParameters() {
		return this.params;
	}

	public int getCode() {
		return code;
	}
}
