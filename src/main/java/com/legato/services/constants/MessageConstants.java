/**
 * 
 */
package com.legato.services.constants;

/**
 * @author AF83580
 *
 */
public final class MessageConstants {
	private MessageConstants() {}
	public static final String SUCCESS_MESSAGE = "Successfully proccessed request.";
	public static final String FAILURE_MESSAGE = "Failed while processing the request!";
	public static final String SESSION_EXPIRED_MESSAGE = "Session expired";
	public static final String LOGOUT_MESSAGE = "Logged out successfully";
	public static final String USER_ROLE_NOT_FOUND_MESSAGE = "Could not find the requested user role!";
	public static final String INVALID_PSWD_FORMAT_MESSAGE = "Password must be 6-20 characters and must contain at least 1 uppercase letter, 1 lowecase letter, 1 digit and 1 special character in [@#$%]";
	public static final String ACCESS_DENIED = "Access denied";
}