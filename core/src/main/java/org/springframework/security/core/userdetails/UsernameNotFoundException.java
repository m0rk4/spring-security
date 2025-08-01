/*
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.core.userdetails;

import java.io.Serial;

import org.jspecify.annotations.Nullable;

import org.springframework.security.core.AuthenticationException;

/**
 * Thrown if an {@link UserDetailsService} implementation cannot locate a {@link User} by
 * its username.
 *
 * @author Ben Alex
 */
public class UsernameNotFoundException extends AuthenticationException {

	@Serial
	private static final long serialVersionUID = 1410688585992297006L;

	private static final String DEFAULT_USER_NOT_FOUND_MESSAGE = "user not found";

	private final @Nullable String name;

	/**
	 * Constructs a <code>UsernameNotFoundException</code> with the specified message.
	 * @param msg the detail message.
	 */
	public UsernameNotFoundException(String msg) {
		super(msg);
		this.name = null;
	}

	/**
	 * Constructs a {@code UsernameNotFoundException} with the specified message and root
	 * cause.
	 * @param msg the detail message.
	 * @param cause root cause
	 */
	public UsernameNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
		this.name = null;
	}

	private UsernameNotFoundException(String msg, String name) {
		super(msg);
		this.name = name;
	}

	private UsernameNotFoundException(String msg, String name, Throwable cause) {
		super(msg, cause);
		this.name = name;
	}

	/**
	 * Construct an exception based on a specific username
	 * @param username the invalid username
	 * @return the {@link UsernameNotFoundException}
	 * @since 7.0
	 */
	public static UsernameNotFoundException fromUsername(String username) {
		return new UsernameNotFoundException(DEFAULT_USER_NOT_FOUND_MESSAGE, username);
	}

	/**
	 * Construct an exception based on a specific username
	 * @param username the invalid username
	 * @param cause any underlying cause
	 * @return the {@link UsernameNotFoundException}
	 * @since 7.0
	 */
	public static UsernameNotFoundException fromUsername(String username, Throwable cause) {
		return new UsernameNotFoundException(DEFAULT_USER_NOT_FOUND_MESSAGE, username, cause);
	}

	/**
	 * Get the username that couldn't be found
	 * @return the username
	 * @since 7.0
	 */
	public @Nullable String getName() {
		return this.name;
	}

}
