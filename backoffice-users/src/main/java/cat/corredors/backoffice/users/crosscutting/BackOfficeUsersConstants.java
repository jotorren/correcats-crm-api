package cat.corredors.backoffice.users.crosscutting;

/**
 * This class is engaged to cast application scoped constants.
 */
public interface BackOfficeUsersConstants {

//	String DATE_FORMAT = "dd/MM/yyyy";
	String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	interface REST {

		String DOWNLOAD_FILE_NAME = "associats.csv";
		String DOWNLOAD_CONTENT_TYPE = "text/csv";
		
		interface Endpoints {
			String API_BASE = "/api";
			String API_VERSION = "v=1";
		}

		interface InfoCodes {
			String PREFIX = "info.";

			int INF_001 = 2001;
		}

		interface ErrorCodes {
			String PREFIX = "error.";

			int ERR_LST = 5999;
			
			int ERR_000 = 5000;

			int ERR_011 = 5011;
			int ERR_012 = 5012;
			int ERR_013 = 5013;
			int ERR_014 = 5014;
			int ERR_015 = 5015;
			int ERR_016 = 5016;
			int ERR_017 = 5017;
			int ERR_018 = 5018;
			int ERR_019 = 5019;
			
			int ERR_501 = 5501;
			int ERR_502 = 5502;
			int ERR_503 = 5503;
		}
	}

	interface Security {
		
		interface Roles {
			String ADMIN = "BO_ADMINISTRADORA";
			String SECRETARIA = "BO_SECRETARIA";
			String JUNTA = "BO_JUNTA";
			String ORGANITZADORA = "BO_ORGANITZADORA";
			String ASSOCIADA = "BO_ASSOCIADA";
			String PUBLICA = "BO_PUBLICA";
		}
		
		interface OIDC {
			String SUBJECT_CLAIM = "sub";
			String USER_CLAIM = "preferred_username";
			
			String AUTHORITIES_CLAIM = "resource_access";
			String CLIENT_FIELD = "users-management-console";
			String ROLES_FIELD = "roles";
			String ROLE_PREFIX = "BO_";			
		}
		
		interface ErrorCodes {
			String PREFIX = "error.";
			
			int ACCESS_DENIED = 4036;
		}
	}
	
	interface Domain {
		
		interface ErrorCodes {
			String ERR_UNEXPECTED = "ERR-MEMBER-000";
			
			String ERR_LIST_MEMBERS = "ERR-MEMBER-001";
			String ERR_FIND_MEMBER = "ERR-MEMBER-002";
			String ERR_REGISTER_MEMBER = "ERR-MEMBER-003";
			String ERR_UNREGISTER_MEMBER = "ERR-MEMBER-004";
			String ERR_DELETE_MEMBER = "ERR-MEMBER-005";
			String ERR_CREATE_MEMBER = "ERR-MEMBER-006";
			String ERR_CHECK_NICK = "ERR-MEMBER-007";
			String ERR_CHECK_EMAIL = "ERR-MEMBER-008";
			String ERR_EMAIL_INCONSISTENCIES = "ERR-MEMBER-009";
			String ERR_NICK_INCONSISTENCIES = "ERR-MEMBER-010";
			String ERR_NICK_EMAIL_VERIFICATION = "ERR-MEMBER-011";
			String ERR_MEMBER_NOT_FORUM_USER = "ERR-MEMBER-012";
			String ERR_FORUM_GROUP_NOT_MEMBER = "ERR-MEMBER-013";
			
			String ERR_LIST_CITIES = "ERR-CATALOG-001";
			String ERR_SEARCH_CITIES = "ERR-CATALOG-002";
			String ERR_CITIES_FOR_POSTAL_CODE = "ERR-CATALOG-003";
			String ERR_POSTAL_CODES_FOR_CITY = "ERR-CATALOG-004";
		}
	}
}
