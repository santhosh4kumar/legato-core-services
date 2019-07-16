/**
 * 
 */
package com.legato.services.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.legato.services.model.UserRequest;
import com.legato.services.repository.AuthorityRepository;
import com.legato.services.repository.UserRepository;
import com.legato.services.repository.UserRequestRepository;
import com.legato.services.service.PasswordValidator;
import com.legato.services.service.UserService;
import com.legato.services.view.request.UserRequestView;
import com.legato.services.view.response.AdminResponseView;
import com.legato.services.view.response.UserResponseView;

/**
 * @author Niranjan
 *
 */

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	private UserRepository userRepository;
	private UserRequestRepository userRequestRepository;
	@Autowired
	private AuthorityRepository authorityRepository;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private PasswordValidator passwordValidator;

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
	public UserProfile findById(long id){
		return userRepository.findById(id).orElse(null);
	}
	
	@Override
	@Transactional
	public UserProfile save(UserRequestView request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new DuplicateFieldException(MessageConstants.DUPLICATE_USERNAME_MSG);
		}
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new DuplicateFieldException(MessageConstants.DUPLICATE_EMAIL_MSG);
		}
		
		if (userRepository.existsByMobile(request.getMobile())) {
			throw new DuplicateFieldException(MessageConstants.DUPLICATE_MOBILE_MSG);
		}
		
		if (!passwordValidator.isValid(request.getPassword())) {
			throw new InvalidFormatException(MessageConstants.INVALID_PSWD_FORMAT_MSG);
		}
		UserProfile manager = userRepository.findById(Long.valueOf(request.getManagerId())).orElse(null);
		if(manager == null) {
			throw new ResourceNotFoundException(MessageConstants.MANGER_NOT_FOUND);
		}

		UserProfile user = new UserProfile();
		user.setManagerId(manager);
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
		user.setAdmin(request.getUserCategory() == UserCategory.ADMIN.getId());

		Set<Long> roleIds = request.getRole();
		Set<UserAuthority> roles = new HashSet<>();
		
		if(roleIds != null && !roleIds.isEmpty()) {
			roleIds.forEach(roleId -> {
				UserAuthority authority = authorityRepository.findByAuthorityId(roleId)
						.orElseThrow(() -> new ResourceNotFoundException(MessageConstants.USER_ROLE_NOT_FOUND_MSG));
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
			throw new DuplicateFieldException(MessageConstants.DUPLICATE_EMAIL_MSG);
		}
		
		if (userRepository.existsByMobileExceptUser(request.getId(), request.getMobile())) {
			throw new DuplicateFieldException(MessageConstants.DUPLICATE_MOBILE_MSG);
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
						.orElseThrow(() -> new ResourceNotFoundException(MessageConstants.USER_ROLE_NOT_FOUND_MSG));
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
			userDetails.setAdmin(user.getAdmin());
		}
		return userDetails;
	}
	
	@Override
	@Transactional
	public List<AdminResponseView> findAdmins(){
		List<UserProfile> users = userRepository.findAdmins();
		List<AdminResponseView> views = new ArrayList<>();
		users.forEach(user -> {
			AdminResponseView view = new AdminResponseView();
			view.setId(user.getId());
			view.setUsername(user.getUsername());
			view.setFirstName(user.getFirstName());
			view.setLastName(user.getLastName());
			view.setProfilePic(user.getProfilePic());
			views.add(view);
		});
		return views;
	}
	
	@Override
	public UserResponseView getResponseView(UserProfile user) {
		UserResponseView view = new UserResponseView();
		view.setId(user.getId());
		view.setCreatedAt(user.getCreatedAt());
		view.setCreatedBy(user.getCreatedBy());
		view.setUpdatedAt(user.getUpdatedAt());
		view.setUpdatedBy(user.getUpdatedBy());
		view.setEmail(user.getEmail());
		view.setMobile(user.getMobile());
		view.setAddressLine1(user.getAddressLine1());
		view.setAddressLine2(user.getAddressLine2());
		view.setRoles(user.getAuthorities());
		view.setUsername(user.getUsername());
		view.setFirstName(user.getFirstName());
		view.setLastName(user.getLastName());
		view.setGender(user.getGender());
		view.setActive(user.isActive());
		view.setProfilePic(user.getProfilePic());
		return view;
	}
	
	@Override
	@Transactional
	public UserRequest register(UserRequestView request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new DuplicateFieldException("Username already exists !");
		}
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new DuplicateFieldException("Email already exists !");
		}
		
		if (userRepository.existsByMobile(request.getMobile())) {
			throw new DuplicateFieldException("Mobile number already exists !");
		}
		
		if (!passwordValidator.isValid(request.getPassword())) {
			throw new InvalidFormatException(MessageConstants.INVALID_PSWD_FORMAT_MSG);
		}
		UserProfile manager = userRepository.findById(Long.valueOf(request.getManagerId())).orElse(null);
		if(manager == null) {
			throw new ResourceNotFoundException(MessageConstants.MANGER_NOT_FOUND);
		}

		UserRequest user = new UserRequest();
		user.setManagerId(manager);
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
		user.setAdmin(request.getUserCategory() == UserCategory.ADMIN.getId());
		return userRequestRepository.save(user);
	}
}