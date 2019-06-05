/**
 * 
 */
package com.legato.services.service;

import java.util.List;

import com.legato.services.model.UserProfile;
import com.legato.services.view.request.UserRequestView;
import com.legato.services.view.response.UserResponseView;

/**
 * @author Niranjan
 *
 */
public interface UserService {
	/**
	 * @param user
	 * @return
	 */
	public UserResponseView getResponseView(UserProfile user);

	/**
	 * @param request
	 * @return
	 */
	UserProfile save(UserRequestView request);

	/**
	 * @return
	 */
	List<UserResponseView> findAll();

	/**
	 * @param request
	 * @return
	 */
	UserProfile update(UserRequestView request);
}