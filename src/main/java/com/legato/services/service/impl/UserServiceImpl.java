/**
 * 
 */
package com.legato.services.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.legato.services.constants.MessageConstants;
import com.legato.services.enums.UserCategory;
import com.legato.services.exception.DuplicateFieldException;
import com.legato.services.exception.InvalidFormatException;
import com.legato.services.exception.ResourceNotFoundException;
import com.legato.services.jwt.security.dto.UserDto;
import com.legato.services.model.UserAuthority;
import com.legato.services.model.UserProfile;
import com.legato.services.repository.AuthorityRepository;
import com.legato.services.repository.UserRepository;
import com.legato.services.service.PasswordValidator;
import com.legato.services.service.UserService;
import com.legato.services.view.request.UserRequestView;
import com.legato.services.view.response.UserResponseView;

/**
 * @author Niranjan
 *
 */

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthorityRepository authorityRepository;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private PasswordValidator passwordValidator;
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	@Transactional
	public List<UserResponseView> findAll(){
		List<UserProfile> users = userRepository.findAll();
		List<UserResponseView> userViews = new ArrayList<>();
		users.forEach(user -> userViews.add(getResponseView(user)));
		return userViews;
	}
	
	@Override
	@Transactional
	public UserProfile save(UserRequestView request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			logger.error("Duplicate username found !");
			throw new DuplicateFieldException("Username already exists !");
		}
		if (userRepository.existsByEmail(request.getEmail())) {
			logger.error("Duplicate email found !");
			throw new DuplicateFieldException("Email already exists !");
		}
		
		if (userRepository.existsByMobile(request.getMobile())) {
			logger.error("Duplicate mobile number found !");
			throw new DuplicateFieldException("Mobile number already exists !");
		}
		
		if (!passwordValidator.isValid(request.getPassword())) {
			logger.error("Invalid password format !");
			throw new InvalidFormatException(MessageConstants.INVALID_PSWD_FORMAT_MESSAGE);
		}

		UserProfile user = new UserProfile();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getFirstName());
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setMobile(request.getMobile());
		user.setAddressLine1(request.getAddressLine1());
		user.setAddressLine2(request.getAddressLine2());
		user.setBirthDate(request.getBirthDate());
		user.setProfilePic(request.getProfilePic());
		user.setPassword(encoder.encode(request.getPassword()));
		user.setStatus(request.getStatus());
		user.setActive(request.getActive());

		Set<Long> roleIds = request.getRole();
		Set<UserAuthority> roles = new HashSet<>();
		
		if(roleIds != null && !roleIds.isEmpty()) {
			roleIds.forEach(roleId -> {
				UserAuthority authority = authorityRepository.findByAuthorityId(roleId)
						.orElseThrow(() -> new ResourceNotFoundException(MessageConstants.USER_ROLE_NOT_FOUND_MESSAGE));
				roles.add(authority);
			});
		}

		user.setAuthorities(roles);
		return userRepository.save(user);
	}
	
	@Override
	@Transactional
	public UserProfile update(UserRequestView request) {
		UserProfile user = userRepository.findById(request.getId()).orElse(null);
		
		if (user == null) throw new ResourceNotFoundException("User not found with id : " + request.getId());
		
		if (userRepository.existsByEmailExceptUser(request.getId(), request.getEmail())) {
			logger.error("Duplicate email found !");
			throw new DuplicateFieldException("Email already registered !");
		}
		
		if (userRepository.existsByMobileExceptUser(request.getId(), request.getMobile())) {
			logger.error("Duplicate mobile number found !");
			throw new DuplicateFieldException("Mobile number already registered !");
		}
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setEmail(request.getEmail());
		user.setMobile(request.getMobile());
		user.setAddressLine1(request.getAddressLine1());
		user.setAddressLine2(request.getAddressLine2());
		user.setProfilePic(request.getProfilePic());
		Set<Long> roleIds = request.getRole();
		Set<UserAuthority> roles = new HashSet<>();
		
		if(roleIds != null && !roleIds.isEmpty()) {
			roleIds.forEach(roleId -> {
				UserAuthority authority = authorityRepository.findByAuthorityId(roleId)
						.orElseThrow(() -> new ResourceNotFoundException(MessageConstants.USER_ROLE_NOT_FOUND_MESSAGE));
				roles.add(authority);
			});
		}
		user.setAuthorities(roles);
		return userRepository.save(user);
	}
	
	@Override
	public UserDto getUser(String username){
		UserDto userDetails = null;
		UserProfile user = userRepository.findActiveUserByUsername(username);
		if (null != user && user.getUsername().equalsIgnoreCase(username)) {
			userDetails = new UserDto();
			userDetails.setFirstName(user.getFirstName());
			userDetails.setLastName(user.getLastName());
			userDetails.setAdmin(UserCategory.ADMIN.getId() == user.getUserCategory());
		}
		return userDetails;
	}
	
	@Override
	public UserResponseView getResponseView(UserProfile user) {
		UserResponseView userResponse = new UserResponseView();
		userResponse.setId(user.getId());
		userResponse.setCreatedAt(user.getCreatedAt());
		userResponse.setCreatedBy(user.getCreatedBy());
		userResponse.setUpdatedAt(user.getUpdatedAt());
		userResponse.setUpdatedBy(user.getUpdatedBy());
		userResponse.setEmail(user.getEmail());
		userResponse.setMobile(user.getMobile());
		userResponse.setAddressLine1(user.getAddressLine1());
		userResponse.setAddressLine2(user.getAddressLine2());
		userResponse.setRoles(user.getAuthorities());
		userResponse.setUsername(user.getUsername());
		userResponse.setActive(user.isActive());
		userResponse.setProfilePic(user.getProfilePic());
		return userResponse;
	}
}