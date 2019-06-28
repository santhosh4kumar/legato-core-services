/**
 * 
 */
package com.legato.services.exception;

import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.legato.services.constants.ApplicationConstants;
import com.legato.services.util.LogDetail;
import com.legato.services.util.LoggingUtil;
import com.legato.services.view.response.SimpleResponseEntity;

/**
 * @author af83580
 *
 */

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, 
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		LoggingUtil.logError(this.getClass(), new LogDetail(ApplicationConstants.SYSTEM_NAME, "", "", exception), exception);
		SimpleResponseEntity errorDetails = new SimpleResponseEntity(HttpServletResponse.SC_BAD_REQUEST,
				"Form validation failed!", request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> resourceNotFoundException(Principal principal, ResourceNotFoundException exception,
			WebRequest request) {
		LoggingUtil.logError(this.getClass(), new LogDetail(ApplicationConstants.SYSTEM_NAME, "", "", exception), exception);
		SimpleResponseEntity errorDetails = new SimpleResponseEntity(HttpServletResponse.SC_NOT_FOUND,
				exception.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> badCredentialsExceptionHandler(Exception exception, WebRequest request) {
		LoggingUtil.logError(this.getClass(), new LogDetail(ApplicationConstants.SYSTEM_NAME, "", "", exception), exception);
		SimpleResponseEntity errorDetails = new SimpleResponseEntity(HttpServletResponse.SC_UNAUTHORIZED,
				exception.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(BadRequest.class)
	public ResponseEntity<?> badRequestExceptionHandler(Exception exception, WebRequest request) {
		LoggingUtil.logError(this.getClass(), new LogDetail(ApplicationConstants.SYSTEM_NAME, "", "", exception), exception);
		SimpleResponseEntity errorDetails = new SimpleResponseEntity(HttpServletResponse.SC_BAD_REQUEST,
				exception.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> globleExcpetionHandler(Exception exception, WebRequest request) {
		LoggingUtil.logError(this.getClass(), new LogDetail(ApplicationConstants.SYSTEM_NAME, "", "", exception), exception);
		SimpleResponseEntity errorDetails = new SimpleResponseEntity(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				exception.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}