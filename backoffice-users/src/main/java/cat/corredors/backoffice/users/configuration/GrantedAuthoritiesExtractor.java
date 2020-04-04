package cat.corredors.backoffice.users.configuration;

import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.Security.OIDC.AUTHORITIES_CLAIM;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.Security.OIDC.CLIENT_FIELD;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.Security.OIDC.ROLES_FIELD;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.Security.OIDC.ROLE_PREFIX;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeLogMessages.ANY_SECURITY_CLAIM_FIELD_NOT_FOUND;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GrantedAuthoritiesExtractor extends JwtAuthenticationConverter {
	
	private final MessageSource messageSource;
	
	@SuppressWarnings("unchecked")
	@Override
	protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
		final Map<String, Object> resourceAccess = (Map<String, Object>) jwt.getClaims().get(AUTHORITIES_CLAIM);
		if (null == resourceAccess) {
			log.warn(messageSource.getMessage(ANY_SECURITY_CLAIM_FIELD_NOT_FOUND, new Object[] { AUTHORITIES_CLAIM }, Locale.getDefault()));
			return Collections.emptyList();
		}
		
		final Map<String, Object> client = (Map<String, Object>) resourceAccess.get(CLIENT_FIELD);
		if (null == client) {
			log.warn(messageSource.getMessage(ANY_SECURITY_CLAIM_FIELD_NOT_FOUND, new Object[] { CLIENT_FIELD }, Locale.getDefault()));
			return Collections.emptyList();
		}
		
		return ((List<String>) client.get(ROLES_FIELD)).stream()
				.map(roleName -> ROLE_PREFIX + roleName.toUpperCase())
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());		
	}
}
