/**
 * 
 */
package com.legato.services.controller;

import java.io.File;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legato.services.constants.MessageConstants;
import com.legato.services.enums.UserCategory;
import com.legato.services.enums.UserStatus;
import com.legato.services.exception.DuplicateFieldException;
import com.legato.services.exception.InvalidFormatException;
import com.legato.services.exception.ResourceNotFoundException;
import com.legato.services.model.UserProfile;
import com.legato.services.service.CommonService;
import com.legato.services.service.UserService;
import com.legato.services.util.LogDetail;
import com.legato.services.util.LoggingUtil;
import com.legato.services.view.request.UserRequestView;
import com.legato.services.view.response.AdminResponseView;
import com.legato.services.view.response.SimpleResponseEntity;
import com.legato.services.view.response.UserResponseView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Niranjan
 *
 */

@Api(value = "User")
@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private CommonService commonService;
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@PreAuthorize("hasRole('ADMIN')")
	@ApiOperation(value = "View a list of available users", response = List.class)
	@GetMapping
	public ResponseEntity<SimpleResponseEntity> findAll(Principal principal) {
		List<UserResponseView> views = userService.findAll();
		LoggingUtil.logInfo(this.getClass(), new LogDetail("", ""));
		return new ResponseEntity<>(
				new SimpleResponseEntity(HttpStatus.OK.value(), MessageConstants.SUCCESS_MSG, views),
				HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@ApiOperation(value = "Add a new user", response = UserRequestView.class)
	@PostMapping
	public ResponseEntity<SimpleResponseEntity> save(HttpServletRequest httpRequest, 
			@RequestPart("data") String formdata, @RequestParam("file") MultipartFile file) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserRequestView request = null;
		try {
			request = new ObjectMapper().readValue(formdata, UserRequestView.class);
			logger.info("{} trying to add user.", username);
			UserProfile user = userService.save(request);
			if(file != null) {
				String path = httpRequest.getServletContext().getRealPath(File.separator);
				commonService.saveFile(file, path, user.getId().toString());
			}
			request.setId(user.getId());
		} catch (DuplicateFieldException | InvalidFormatException exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail(username, httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok()
					.body(new SimpleResponseEntity(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), request));
		} catch (Exception exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail(username, httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok().body(
					new SimpleResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), MessageConstants.INTERNAL_SERVER_ERR_MSG, request));
		}
		logger.info("{} registered user successfully.", username);
		return ResponseEntity.ok()
				.body(new SimpleResponseEntity(HttpStatus.OK.value(), MessageConstants.SUCCESS_MSG, request));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@ApiOperation(value = "Update an existing user", response = UserRequestView.class)
	@PutMapping
	public ResponseEntity<SimpleResponseEntity> updateUser(Principal principal, 
			HttpServletRequest httpRequest, 
			@Valid @RequestBody UserRequestView request) {
		logger.info("User trying to update {}", request.getUsername());
		try {
			userService.update(request);
		} catch(ResourceNotFoundException exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail(principal.getName(), httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok()
					.body(new SimpleResponseEntity(HttpStatus.NOT_FOUND.value(), exception.getMessage(), ""));
		} catch(DuplicateFieldException exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail(principal.getName(), httpRequest.getRequestURI(), exception), exception);
			return ResponseEntity.ok()
					.body(new SimpleResponseEntity(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), ""));
		}
		logger.info("{} updated successfully.", request.getUsername());
		return ResponseEntity.ok()
				.body(new SimpleResponseEntity(HttpStatus.OK.value(), MessageConstants.SUCCESS_MSG, request));
	}
	
	@ApiOperation(value = "View a list of active admins", response = List.class)
	@GetMapping("/admins")
	public ResponseEntity<SimpleResponseEntity> findAdmins() {
		List<AdminResponseView> views = userService.findAdmins();
		return new ResponseEntity<>(
				new SimpleResponseEntity(HttpStatus.OK.value(), MessageConstants.SUCCESS_MSG, views),
				HttpStatus.OK);
	}
	
	@ApiOperation(value = "Registering a new user.")
	@PostMapping("/signup")
	public ResponseEntity<SimpleResponseEntity> registerUser(HttpServletRequest httpRequest, 
			@RequestPart("data") String formdata, @RequestParam("file") MultipartFile file) {
		UserRequestView request = null;
		try {
			request = new ObjectMapper().readValue(formdata, UserRequestView.class);
			request.setActive(false);
			request.setStatus(UserStatus.REQUESTED.getId());
			request.setUserCategory(UserCategory.USER.getId());
			// UserRequest user = userService.register(request);
			UserProfile user = userService.save(request);
			if(file != null) {
				String path = httpRequest.getServletContext().getRealPath(File.separator);
				commonService.saveFile(file, path, user.getId().toString());
			}
			request.setId(user.getId());
			return new ResponseEntity<>(
					new SimpleResponseEntity(HttpStatus.OK.value(), MessageConstants.REGISTRATION_SUCCESS_MSG, request),
					HttpStatus.OK);
		} catch (DuplicateFieldException exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", httpRequest.getRequestURI(), exception), exception);
			return new ResponseEntity<>(
					new SimpleResponseEntity(HttpStatus.CONFLICT.value(), exception.getMessage(), request),
					HttpStatus.CONFLICT);
		} catch (InvalidFormatException exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", httpRequest.getRequestURI(), exception), exception);
			return new ResponseEntity<>(
					new SimpleResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY.value(), exception.getMessage(), request),
					HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (ResourceNotFoundException exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", httpRequest.getRequestURI(), exception), exception);
			return new ResponseEntity<>(
					new SimpleResponseEntity(HttpStatus.NOT_FOUND.value(), exception.getMessage(), request),
					HttpStatus.NOT_FOUND);
		} catch (Exception exception) {
			LoggingUtil.logError(this.getClass(), new LogDetail("", httpRequest.getRequestURI(), exception), exception);
			return new ResponseEntity<>(
					new SimpleResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), MessageConstants.INTERNAL_SERVER_ERR_MSG, request), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}