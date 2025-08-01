/*
 * Copyright 2004-present the original author or authors.
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

import org.jspecify.annotations.Nullable;

/**
 * An API for changing a {@link UserDetails} password.
 *
 * @author Rob Winch
 * @since 5.1
 */
public interface UserDetailsPasswordService {

	UserDetailsPasswordService NOOP = (user, newPassword) -> user;

	/**
	 * Modify the specified user's password. This should change the user's password in the
	 * persistent user repository (database, LDAP etc).
	 * @param user the user to modify the password for
	 * @param newPassword the password to change to, encoded by the configured
	 * {@code PasswordEncoder}
	 * @return the updated UserDetails with the new password
	 */
	UserDetails updatePassword(UserDetails user, @Nullable String newPassword);

}
