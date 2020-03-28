package cat.corredors.backoffice.users.controller;
 
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cat.corredors.backoffice.users.crosscutting.BOUserNotFoundException;
import cat.corredors.backoffice.users.crosscutting.BOUsersConstants;
import cat.corredors.backoffice.users.crosscutting.BOUsersSystemFault;
import cat.corredors.backoffice.users.crosscutting.ErrorBean;
import cat.corredors.backoffice.users.crosscutting.MemberPreconditionException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class RestControllerExceptionHandler {
 
   @Autowired(required = false)
   private MessageSource messageSource;
 
   @Autowired(required = false)
   private BeanValidator validator;

   @ExceptionHandler(AccessDeniedException.class)
   @ResponseStatus(value = HttpStatus.FORBIDDEN)
   public ResponseError defaultErrorHandlerAccessDeniedException(final HttpServletRequest request,
           final HttpServletResponse response, final AccessDeniedException e) {
       log.error(e.getMessage(), e);
       return new ResponseError(BOUsersConstants.Security.ErrorCodes.ACCESS_DENIED,
               messageSource.getMessage(BOUsersConstants.Security.ErrorCodes.PREFIX + BOUsersConstants.Security.ErrorCodes.ACCESS_DENIED,
                       new Object[] { e.getMessage() }, e.getMessage(), Locale.getDefault()));
   }
   
   @ExceptionHandler(ConstraintViolationException.class)
   @ResponseStatus(value = HttpStatus.BAD_REQUEST)
   public ResponseError handleConstraintViolationException(final HttpServletRequest request,
           final HttpServletResponse response, final ConstraintViolationException e) {
	   log.error(e.getMessage(), e);
	   return new ResponseError(validator.process(e.getConstraintViolations()));
   }
   
   @SuppressWarnings("unchecked")
   @ExceptionHandler(BOUsersSystemFault.class)
   @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
   public ResponseError defaultErrorHandlerSystemException(final HttpServletRequest request,
           final HttpServletResponse response, final BOUsersSystemFault e) {
       log.error(e.getMessage(), e);
		if (BOUsersConstants.REST.ErrorCodes.ERR_LST == e.getCode()) {
			return new ResponseError((List<ErrorBean>) e.getParameters()[0]);
		} else {
	       return new ResponseError(e.getCode(), messageSource
	               .getMessage(BOUsersConstants.REST.ErrorCodes.PREFIX + e.getCode(), e.getParameters(), e.getMessage(), Locale.getDefault()));
		}
   }
   
   @ExceptionHandler(BOUserNotFoundException.class)
   @ResponseStatus(value = HttpStatus.NOT_FOUND)
   public ResponseError handleNotFoundException(final HttpServletRequest request,
           final HttpServletResponse response, final BOUserNotFoundException e) {
	   log.error(e.getMessage(), e);
       return new ResponseError(e.getCode(), messageSource
               .getMessage(BOUsersConstants.REST.ErrorCodes.PREFIX + e.getCode(), e.getParameters(), e.getMessage(), Locale.getDefault()));
   }  

   @ExceptionHandler(MemberPreconditionException.class)
   @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
   public ResponseError handlePrecondtionException(final HttpServletRequest request,
           final HttpServletResponse response, final MemberPreconditionException e) {
	   log.error(e.getMessage(), e);
       return new ResponseError(e.getCode(), messageSource
               .getMessage(BOUsersConstants.REST.ErrorCodes.PREFIX + e.getCode(), e.getParameters(), e.getMessage(), Locale.getDefault()));
   }
}