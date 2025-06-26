package com.okstatelibrary.redbud.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that manages and tracks all active HTTP sessions in the
 * application.
 * <p>
 * This class registers a {@link HttpSessionListener} as a Spring bean that
 * captures session life cycle events (creation and destruction). It maintains
 * an internal static map to track currently active sessions by their session
 * ID.
 * </p>
 *
 * <p>
 * Useful for administrative or monitoring purposes (e.g., viewing current user
 * sessions).
 * </p>
 * 
 * <p>
 * <strong>Note:</strong> This implementation is not distributed or clustered
 * safe— it stores sessions in-memory and only works within a single JVM
 * instance.
 * </p>
 * 
 * @author Damith Perera
 */
@Configuration
public class HttpSessionConfig {

	/**
	 * A static map to store all currently active HTTP sessions. Key: Session ID
	 * Value: HttpSession object
	 */
	private static final Map<String, HttpSession> sessions = new HashMap<>();

	/**
	 * Retrieves a list of currently active sessions.
	 *
	 * @return a list of {@link HttpSession} objects representing active sessions
	 */
	public List<HttpSession> getActiveSessions() {
		return new ArrayList<>(sessions.values());
	}

	/**
	 * Registers a {@link HttpSessionListener} to listen for session creation and
	 * destruction events.
	 * <p>
	 * - On session creation, adds the session to the sessions map.<br>
	 * - On session destruction, removes the session from the sessions map.
	 * </p>
	 *
	 * @return an instance of {@link HttpSessionListener}
	 */
	@Bean
	public HttpSessionListener httpSessionListener() {
		return new HttpSessionListener() {

			/**
			 * Called when a new HTTP session is created. Adds the session to the internal
			 * session map.
			 *
			 * @param hse the session event containing the created session
			 */
			@Override
			public void sessionCreated(HttpSessionEvent hse) {
				sessions.put(hse.getSession().getId(), hse.getSession());
			}

			/**
			 * Called when an HTTP session is destroyed. Removes the session from the
			 * internal session map.
			 *
			 * @param hse the session event containing the destroyed session
			 */
			@Override
			public void sessionDestroyed(HttpSessionEvent hse) {
				sessions.remove(hse.getSession().getId());
			}
		};
	}
}
