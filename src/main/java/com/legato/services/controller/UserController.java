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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.legato.services.jwt.security.dto.AppUser;
import com.legato.services.model.UserProfile;
import com.legato.services.repository.AuthorityRepository;
import com.legato.services.repository.UserRepository;
import com.legato.services.service.UserService;
import com.legato.services.util.LogDetail;
import com.legato.services.util.LoggingUtil;
import com.legato.services.view.request.UserRequestView;
import com.legato.services.view.response.SimpleResponseEntity;
import com.legato.services.view.response.UserResponseView;

/**
 * @author Niranjan
 *
 */

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	AuthorityRepository roleRepository;
	@Autowired
	PasswordEncoder encoder;
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@GetMapping
	public ResponseEntity<SimpleResponseEntity> findAll(Principal principal) {
		AppUser appuser = (AppUser)((Authentication)principal).getPrincipal();
		System.out.println(appuser.getFirstName());
		List<UserResponseView> userViews = userService.findAll();
		return new ResponseEntity<>(
				new SimpleResponseEntity(HttpStatus.OK.value(), MessageConstants.SUCCESS_MESSAGE, userViews),
				HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<SimpleResponseEntity> registerUser(HttpServletRequest httpRequest, @Valid @RequestBody UserRequestView request) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		logger.info("{} trying to register user.", username);
		try {
			UserProfile user = userService.save(request);
			request.setId(user.getId());
		} catch (DuplicateFieldException | InvalidFormatException exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", null, username, httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok()
					.body(new SimpleResponseEntity(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), ""));
		} catch (Exception exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", null, username, httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok().body(
					new SimpleResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error !", ""));
		}
		logger.info("{} registered user successfully.", username);
		return ResponseEntity.ok()
				.body(new SimpleResponseEntity(HttpStatus.OK.value(), MessageConstants.SUCCESS_MESSAGE, request));
	}

	@PutMapping
	public ResponseEntity<SimpleResponseEntity> updateUser(Principal principal, HttpServletRequest httpRequest, @Valid @RequestBody UserRequestView request) {
		logger.info("User trying to update {}", request.getUsername());
		try {
			userService.update(request);
		} catch(ResourceNotFoundException exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", principal.getName(), principal.getName(), httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok()
					.body(new SimpleResponseEntity(HttpStatus.NOT_FOUND.value(), exception.getMessage(), ""));
		} catch(DuplicateFieldException exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", principal.getName(), principal.getName(), httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok()
					.body(new SimpleResponseEntity(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), ""));
		}
		logger.info("{} updated successfully.", request.getUsername());
		return ResponseEntity.ok()
				.body(new SimpleResponseEntity(HttpStatus.OK.value(), MessageConstants.SUCCESS_MESSAGE, request));
	}
}