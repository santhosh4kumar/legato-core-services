/**
 * 
 */
package com.legato.services.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.legato.services.constants.MessageConstants;
import com.legato.services.exception.DuplicateFieldException;
import com.legato.services.exception.InvalidFormatException;
import com.legato.services.exception.ResourceNotFoundException;
import com.legato.services.model.UserAuthority;
import com.legato.services.service.AuthorityService;
import com.legato.services.util.LogDetail;
import com.legato.services.util.LoggingUtil;
import com.legato.services.view.request.AuthorityRequestView;
import com.legato.services.view.response.AuthorityResponseView;
import com.legato.services.view.response.SimpleResponseEntity;

/**
 * @author af83580
 *
 */

@RestController
@RequestMapping("/authority")
@PreAuthorize("hasAuthority('ADMIN')")
public class AuthorityController {
	@Autowired
	private AuthorityService authorityService;
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityController.class);
	
	@GetMapping
	public ResponseEntity<SimpleResponseEntity> findAll() {
		List<AuthorityResponseView> responses = authorityService.findAll();
		return new ResponseEntity<>(
				new SimpleResponseEntity(HttpStatus.OK.value(), MessageConstants.SUCCESS_MESSAGE, responses),
				HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<SimpleResponseEntity> saveAuthority(HttpServletRequest httpRequest,
			@Valid @RequestBody AuthorityRequestView request, 
			Principal principal) {
		String username = principal.getName();
		LOGGER.info("{} trying to save authority.", username);
		try {
			UserAuthority authority = authorityService.save(request);
			request.setId(authority.getId());
		} catch (DuplicateFieldException | InvalidFormatException exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", null, username, httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok()
					.body(new SimpleResponseEntity(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), ""));
		} catch (Exception exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", null, username, httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok().body(
					new SimpleResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error !", ""));
		}
		LOGGER.info("{} saved authority successfully.", username);
		return ResponseEntity.ok()
				.body(new SimpleResponseEntity(HttpStatus.OK.value(), MessageConstants.SUCCESS_MESSAGE, request));
	}

	@PutMapping
	public ResponseEntity<SimpleResponseEntity> updateAuthority(Principal principal, 
			HttpServletRequest httpRequest, 
			@Valid @RequestBody AuthorityRequestView request) {
		LOGGER.info("User trying to update {}", principal.getName());
		try {
			authorityService.update(request);
		} catch(ResourceNotFoundException exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", principal.getName(), principal.getName(), httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok()
					.body(new SimpleResponseEntity(HttpStatus.NOT_FOUND.value(), exception.getMessage(), ""));
		} catch(DuplicateFieldException exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", principal.getName(), principal.getName(), httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok()
					.body(new SimpleResponseEntity(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), ""));
		}
		LOGGER.info("{} updated successfully.", principal.getName());
		return ResponseEntity.ok()
				.body(new SimpleResponseEntity(HttpStatus.OK.value(), MessageConstants.SUCCESS_MESSAGE, request));
	}
}