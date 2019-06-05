/**
 * 
 */
package com.legato.services.util;

/**
 * @author Af83580
 *
 */
public class LogDetail {
	private String system;
	private String userId;
	private String username;
	private String uri;
	private Exception exception;
	
	public LogDetail() {
		// Auto-generated constructor stub
	}
	
	public LogDetail(String system, String userId, String username, String uri) {
		super();
		this.system = system;
		this.userId = userId;
		this.username = username;
		this.uri = uri;
	}
	
	public LogDetail(String system, String userId, String username, String uri, Exception exception) {
		super();
		this.system = system;
		this.userId = userId;
		this.username = username;
		this.uri = uri;
		this.exception = exception;
	}

	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
}