package cat.corredors.backoffice.users.controller;

import java.util.Locale;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cat.corredors.backoffice.users.crosscutting.BOUsersConstants;

@Aspect
@Component
public class ResponseDataMessageAspect {

	@Autowired
	private MessageSource messageSource;

	@Pointcut("within(@org.springframework.stereotype.Controller *)")
	public void anyController() {
	}

	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	public void anyRestController() {
	}

	@Pointcut(value = "execution(org.springframework.http.ResponseEntity *(..))")
	public void responseEntityMethod() {
	}

	@Pointcut(value = "execution(cat.corredors.backoffice.users.controller.ResponseData *(..))")
	public void responseMethod() {
	}

	@Around("(anyController() || anyRestController()) && (responseEntityMethod() || responseMethod())")
	public Object i18nCodeMessage(ProceedingJoinPoint point) throws Throwable {
		Object result = point.proceed();

		ResponseData<?> respBean = null;
		if (result instanceof ResponseEntity) {
			ResponseEntity<?> response = (ResponseEntity<?>) result;
			Object body = response.getBody();
			if (body instanceof ResponseData) {
				respBean = (ResponseData<?>) body;
			}
		} else if (result instanceof ResponseData) {
			respBean = (ResponseData<?>) result;
		}

		if (null != respBean) {
			respBean.setMessage(
					messageSource.getMessage(BOUsersConstants.REST.InfoCodes.PREFIX + respBean.getCode(), respBean.getMessageParams(), (String) null, Locale.getDefault()));
		}

		return result;
	}

}
