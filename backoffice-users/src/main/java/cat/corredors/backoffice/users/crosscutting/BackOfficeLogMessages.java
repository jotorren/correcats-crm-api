package cat.corredors.backoffice.users.crosscutting;

public interface BackOfficeLogMessages {
	
	String ANY_BEAN_VALIDATOR_IGNORE_FIELD = "log.any.bean.validator.ignore.field";
	
	String ANY_SECURITY_CLAIM_FIELD_NOT_FOUND = "log.any.security.claim.field.notfound";
	String ANY_SECURITY_CONTEXT_USER = "log.any.security.context.user";
	
	String MEMBER_FIELDS_SELECTION_ERROR = "log.member.fields.selection.error";
	String MEMBER_EXPORT_FILE_ERROR = "log.member.export.file.error";
}
