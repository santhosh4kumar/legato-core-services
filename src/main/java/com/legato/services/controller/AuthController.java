/**
 * 
 */

package com.legato.services.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.legato.services.constants.MessageConstants;
import com.legato.services.enums.UserStatus;
import com.legato.services.exception.DuplicateFieldException;
import com.legato.services.exception.InvalidFormatException;
import com.legato.services.exception.ResourceNotFoundException;
import com.legato.services.jwt.security.config.TokenProvider;
import com.legato.services.jwt.security.dto.AppUser;
import com.legato.services.model.UserProfile;
import com.legato.services.service.UserService;
import com.legato.services.util.LogDetail;
import com.legato.services.util.LoggingUtil;
import com.legato.services.view.request.AuthRequest;
import com.legato.services.view.request.UserRequestView;
import com.legato.services.view.response.JwtResponse;
import com.legato.services.view.response.SimpleResponseEntity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author af83580
 *
 */

@Api(value = "Authentication")
@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserService userService;
	@Autowired
	private TokenProvider tokenProvider;
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@ApiOperation(value = "Signing in to the application.")
	@PostMapping("/signin")
	public ResponseEntity<SimpleResponseEntity> authenticateUser(@Valid @RequestBody AuthRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = tokenProvider.generateToken((AppUser) authentication.getPrincipal());
		return ResponseEntity.ok().body(new SimpleResponseEntity(HttpStatus.OK.value(), MessageConstants.SUCCESS_MESSAGE, new JwtResponse(token)));
	}
	
	@ApiOperation(value = "Register a new user.")
	@PostMapping("/signup")
	public ResponseEntity<SimpleResponseEntity> registerUser(HttpServletRequest httpRequest, 
			@Valid @RequestBody UserRequestView request) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		logger.info("{} trying to register user.", username);
		try {
			request.setActive(false);
			request.setStatus(UserStatus.REQUESTED.getId());
			UserProfile user = userService.save(request);
			request.setId(user.getId());
			logger.info("{} registered user successfully.", username);
			return ResponseEntity.ok()
					.body(new SimpleResponseEntity(HttpStatus.OK.value(), MessageConstants.SUCCESS_MESSAGE, request));
		} catch (DuplicateFieldException | InvalidFormatException | ResourceNotFoundException exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", null, username, httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok()
					.body(new SimpleResponseEntity(HttpStatus.BAD_REQUEST.value(), MessageConstants.FAILURE_MESSAGE, exception.getMessage()));
		} catch (Exception exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", null, username, httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok().body(
					new SimpleResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error !", ""));
		}
	}
}