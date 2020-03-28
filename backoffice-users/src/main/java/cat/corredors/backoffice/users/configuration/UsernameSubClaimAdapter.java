package cat.corredors.backoffice.users.configuration;

import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Security.OIDC.SUBJECT_CLAIM;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Security.OIDC.USER_CLAIM;
import static cat.corredors.backoffice.users.crosscutting.LogMessages.ANY_SECURITY_CLAIM_FIELD_NOT_FOUND;
import static cat.corredors.backoffice.users.crosscutting.LogMessages.ANY_SECURITY_CONTEXT_USER;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UsernameSubClaimAdapter implements Converter<Map<String, Object>, Map<String, Object>> {

	private final MappedJwtClaimSetConverter delegate = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
	private final MessageSource messageSource;

	@Override
	public Map<String, Object> convert(Map<String, Object> claims) {
		Map<String, Object> convertedClaims = this.delegate.convert(claims);
		
		String username = (String)convertedClaims.get(USER_CLAIM);
		if (null == username) {
			log.warn(messageSource.getMessage(ANY_SECURITY_CLAIM_FIELD_NOT_FOUND, new Object[] { USER_CLAIM }, Locale.getDefault()));
			return convertedClaims;
		}
		
		log.info(messageSource.getMessage(ANY_SECURITY_CONTEXT_USER, new Object[] { username }, Locale.getDefault()));
		convertedClaims.put(SUBJECT_CLAIM, username);
		
		return convertedClaims;
	}

}
