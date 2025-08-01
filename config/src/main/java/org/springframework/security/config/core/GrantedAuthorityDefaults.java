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

package org.springframework.security.config.core;

import org.springframework.security.core.GrantedAuthority;

/**
 * Allows providing defaults for {@link GrantedAuthority}
 *
 * @author Eddú Meléndez
 * @since 4.2.0
 */
public final class GrantedAuthorityDefaults {

	private final String rolePrefix;

	public GrantedAuthorityDefaults(String rolePrefix) {
		this.rolePrefix = rolePrefix;
	}

	/**
	 * The default prefix used with role based authorization. Default is "ROLE_".
	 * @return the default role prefix
	 */
	public String getRolePrefix() {
		return this.rolePrefix;
	}

}
