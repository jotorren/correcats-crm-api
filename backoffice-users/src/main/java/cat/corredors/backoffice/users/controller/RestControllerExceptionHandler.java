package cat.corredors.backoffice.users.controller;

import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Domain.ErrorCodes.ERR_UNEXPECTED;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.REST.ErrorCodes.ERR_000;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.REST.ErrorCodes.ERR_501;
import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.Security.ErrorCodes.ACCESS_DENIED;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
	   String err = messageSource.getMessage(BOUsersConstants.Security.ErrorCodes.PREFIX + ACCESS_DENIED,
               new Object[] { e.getMessage() }, e.getMessage(), Locale.getDefault());
       log.error(err, e);
       return new ResponseError(ACCESS_DENIED, err);
   }

   @ExceptionHandler(MissingServletRequestParameterException.class)
   @ResponseStatus(value = HttpStatus.BAD_REQUEST)
   public ResponseError handleMissinParameterException(final HttpServletRequest request,
           final HttpServletResponse response, final MissingServletRequestParameterException e) {
	   String err = messageSource.getMessage(BOUsersConstants.REST.ErrorCodes.PREFIX + ERR_501,
               new Object[] { e.getMessage() }, e.getMessage(), Locale.getDefault());
       log.error(err, e);
	   return new ResponseError(ERR_501, err);
   }

   @ExceptionHandler(MethodArgumentTypeMismatchException.class)
   @ResponseStatus(value = HttpStatus.BAD_REQUEST)
   public ResponseError handleTypeMismatchException(final HttpServletRequest request,
           final HttpServletResponse response, final MethodArgumentTypeMismatchException e) {
	   String err = messageSource.getMessage(BOUsersConstants.REST.ErrorCodes.PREFIX + ERR_501,
               new Object[] { e.getMostSpecificCause().getMessage() }, e.getMessage(), Locale.getDefault());
       log.error(err, e);
	   return new ResponseError(ERR_501, err);
   }
   
   @ExceptionHandler(ConstraintViolationException.class)
   @ResponseStatus(value = HttpStatus.BAD_REQUEST)
   public ResponseError handleConstraintViolationException(final HttpServletRequest request,
           final HttpServletResponse response, final ConstraintViolationException e) {
	   List<ErrorBean> errors = validator.process(e.getConstraintViolations());
	   errors.forEach(err -> {
		   log.error(err.getMessage());
	   });
	   return new ResponseError(errors);
   }
   
   @SuppressWarnings("unchecked")
   @ExceptionHandler(BOUsersSystemFault.class)
   @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
   public ResponseError defaultErrorHandlerSystemException(final HttpServletRequest request,
           final HttpServletResponse response, final BOUsersSystemFault e) {
		if (BOUsersConstants.REST.ErrorCodes.ERR_LST == e.getCode()) {
			List<ErrorBean> errors = (List<ErrorBean>) e.getParameters()[0];
			errors.forEach(err -> {
				log.error(err.getMessage());
			});
			return new ResponseError(errors);
		} else {
			String err =  messageSource
		               .getMessage(BOUsersConstants.REST.ErrorCodes.PREFIX + e.getCode(), e.getParameters(), e.getMessage(), Locale.getDefault());
			log.error(err);
			return new ResponseError(e.getCode(), err);
		}
   }
   
   @ExceptionHandler(BOUserNotFoundException.class)
   @ResponseStatus(value = HttpStatus.NOT_FOUND)
   public ResponseError handleNotFoundException(final HttpServletRequest request,
           final HttpServletResponse response, final BOUserNotFoundException e) {
	   String err = messageSource
               .getMessage(BOUsersConstants.REST.ErrorCodes.PREFIX + e.getCode(), e.getParameters(), e.getMessage(), Locale.getDefault());
	   log.error(err);
       return new ResponseError(e.getCode(), err);
   }  

   @ExceptionHandler(MemberPreconditionException.class)
   @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
   public ResponseError handlePrecondtionException(final HttpServletRequest request,
           final HttpServletResponse response, final MemberPreconditionException e) {
	   String err = messageSource
               .getMessage(BOUsersConstants.REST.ErrorCodes.PREFIX + e.getCode(), e.getParameters(), e.getMessage(), Locale.getDefault());
	   log.error(err);
       return new ResponseError(e.getCode(), err);
   }
   
   @ExceptionHandler(Exception.class)
   @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
   public ResponseError defaultErrorHandlerException(final HttpServletRequest request,
           final HttpServletResponse response, final Exception e) {
       String err = messageSource
               .getMessage(BOUsersConstants.REST.ErrorCodes.PREFIX + ERR_000, new Object[]{ ERR_UNEXPECTED }, e.getMessage(), Locale.getDefault());
       log.error(err, e);
       return new ResponseError(ERR_000, err);
   }
}