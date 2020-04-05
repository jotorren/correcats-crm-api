package cat.corredors.backoffice.users.domain;

public enum SearchOperation {
	EQ, IN, NOT, GT, LT, GTE, LTE, LIKE, STARTS_WITH, ENDS_WITH, CONTAINS;

	public static final String[] SIMPLE_OPERATION_SET = { ":", "!", ">", "<", "#", "@", "~" };

	public static final String OR_PREDICATE_FLAG = "'";

	public static final String ZERO_OR_MORE_REGEX = "*";

	public static final String OR_OPERATOR = "OR";

	public static final String AND_OPERATOR = "AND";

	public static final String LEFT_PARANTHESIS = "(";

	public static final String RIGHT_PARANTHESIS = ")";

	public static SearchOperation getSimpleOperation(final char input) {
		switch (input) {
		case ':':
			return EQ;
		case '!':
			return NOT;
		case '>':
			return GT;
		case '<':
			return LT;
		case '#':
			return GTE;
		case '@':
			return LTE;
		case '~':
			return LIKE;
		default:
			return null;
		}
	}
}
