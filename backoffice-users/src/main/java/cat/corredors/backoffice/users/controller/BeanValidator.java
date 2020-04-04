package cat.corredors.backoffice.users.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants;
import cat.corredors.backoffice.users.crosscutting.BackOfficeUsersSystemFault;
import cat.corredors.backoffice.users.crosscutting.ErrorBean;
import cat.corredors.backoffice.users.crosscutting.BackOfficeLogMessages;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BeanValidator {
	
	@Autowired
	private MessageSource messageSource;

	@Autowired
	private Validator jsr380Validator;
	
	private int getErrorCode(ConstraintViolation<?> violation) {
		if (violation.getConstraintDescriptor().getAnnotation().annotationType().equals(NotEmpty.class)
				|| violation.getConstraintDescriptor().getAnnotation().annotationType().equals(NotNull.class)) {
			return BackOfficeUsersConstants.REST.ErrorCodes.ERR_501;
		} else if (violation.getConstraintDescriptor().getAnnotation().annotationType().equals(Pattern.class)) {
			return BackOfficeUsersConstants.REST.ErrorCodes.ERR_502;
		} else if (violation.getConstraintDescriptor().getAnnotation().annotationType().equals(Future.class)) {
			return BackOfficeUsersConstants.REST.ErrorCodes.ERR_503;
		}

		return BackOfficeUsersConstants.REST.ErrorCodes.ERR_501;
	}

	public void validate(Object bean, String... exclude) {

		Set<ConstraintViolation<Object>> constraintViolations = jsr380Validator.validate(bean);
		List<ErrorBean> errors = process(new HashSet<ConstraintViolation<?>>(constraintViolations));
		if (!errors.isEmpty()) {
			throw new BackOfficeUsersSystemFault(BackOfficeUsersConstants.REST.ErrorCodes.ERR_LST, "Validation errors", new Object[] { errors });
		}
	}
	
	public List<ErrorBean> process(Set<ConstraintViolation<?>> violations, String... exclude) {
		List<String> ignored = Arrays.asList(exclude);
		
		List<ErrorBean> errors = new ArrayList<ErrorBean>();
		for (ConstraintViolation<?> violation : violations) {
			if (ignored.contains(violation.getPropertyPath().toString())) {
				log.debug(messageSource.getMessage(BackOfficeLogMessages.ANY_BEAN_VALIDATOR_IGNORE_FIELD,
						new Object[] { violation.getPropertyPath() }, Locale.getDefault()));
			} else {
				String message = " -> " + violation.getMessage();
				int code = getErrorCode(violation);
				errors.add(new ErrorBean(code, messageSource.getMessage(BackOfficeUsersConstants.REST.ErrorCodes.PREFIX + code,
						new Object[] { violation.getPropertyPath().toString(), message }, Locale.getDefault())));
			}
		}
		
		return errors;
	}
}
