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

package org.springframework.security.core.context;

import org.jspecify.annotations.Nullable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.ObjectUtils;

/**
 * Base implementation of {@link SecurityContext}.
 * <p>
 * Used by default by {@link SecurityContextHolder} strategies.
 *
 * @author Ben Alex
 */
public class SecurityContextImpl implements SecurityContext {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private @Nullable Authentication authentication;

	public SecurityContextImpl() {
	}

	public SecurityContextImpl(Authentication authentication) {
		this.authentication = authentication;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SecurityContextImpl other) {
			if ((this.getAuthentication() == null) && (other.getAuthentication() == null)) {
				return true;
			}
			if ((this.getAuthentication() != null) && (other.getAuthentication() != null)
					&& this.getAuthentication().equals(other.getAuthentication())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public @Nullable Authentication getAuthentication() {
		return this.authentication;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(this.authentication);
	}

	@Override
	public void setAuthentication(@Nullable Authentication authentication) {
		this.authentication = authentication;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append(" [");
		if (this.authentication == null) {
			sb.append("Null authentication");
		}
		else {
			sb.append("Authentication=").append(this.authentication);
		}
		sb.append("]");
		return sb.toString();
	}

}
