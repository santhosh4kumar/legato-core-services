/**
 * 
 */
package com.legato.services.util;

import org.slf4j.LoggerFactory;

/**
 * @author Af83580
 * 
 *         LoggingUtil - An utility class to handle all the logging
 *         functionality of the application.
 */
public class LoggingUtil {
	private LoggingUtil() {
		super();
	}

	public static void logError(Class<?> clazz, LogDetail logDetail, Exception exception) {
		String system = "";
		String userId = "";
		String username = "";
		String uri = "";
		String errorMessage = "" + exception;

		if (logDetail != null) {
			if (logDetail.getSystem() == null) {
				system = "";
			}
			if (logDetail.getUserId() == null) {
				userId = "";
			}
			if (logDetail.getUsername() == null) {
				username = "";
			}
			if (logDetail.getUri() == null) {
				uri = "";
			}

			system = system.replace('\n', '_').replace('\r', '_');
			userId = userId.replace('\n', '_').replace('\r', '_');
			username = username.replace('\n', '_').replace('\r', '_');
			uri = uri.replace('\n', '_').replace('\r', '_');
			errorMessage = errorMessage.replace('\n', '_').replace('\r', '_');

			LoggerFactory.getLogger(clazz).error(
					"Exception - SYSTEM={}, USERID={}, USERNAME={}, SERVICE_URI={}, ERROR_MESSAGE={}", system, userId,
					username, uri, errorMessage);

		}

	}

	public static void logInfo(Class<?> clazz, LogDetail logDetail) {
		String system = "";
		String userId = "";
		String username = "";
		String uri = "";

		if (logDetail != null) {
			if (logDetail.getSystem() == null) {
				system = "";
			}
			if (logDetail.getUserId() == null) {
				userId = "";
			}
			if (logDetail.getUsername() == null) {
				username = "";
			}
			if (logDetail.getUri() == null) {
				uri = "";
			}

			system = system.replace('\n', '_').replace('\r', '_');
			userId = userId.replace('\n', '_').replace('\r', '_');
			username = username.replace('\n', '_').replace('\r', '_');
			uri = uri.replace('\n', '_').replace('\r', '_');

			LoggerFactory.getLogger(clazz).info("Info - SYSTEM={}, USERID={}, USERNAME={}, SERVICE_URI={}", system,
					userId, username, uri);

		}
	}
}