/**
 * 
 */
package com.legato.services.exception;

import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.legato.services.view.response.SimpleResponseEntity;

/**
 * @author af83580
 *
 */

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> resourceNotFoundException(Principal principal, ResourceNotFoundException exception, WebRequest request) {
		SimpleResponseEntity errorDetails = new SimpleResponseEntity(HttpServletResponse.SC_NOT_FOUND, exception.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> badCredentialsExceptionHandler(Exception exception, WebRequest request) {
		SimpleResponseEntity errorDetails = new SimpleResponseEntity(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> globleExcpetionHandler(Exception exception, WebRequest request) {
		SimpleResponseEntity errorDetails = new SimpleResponseEntity(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}