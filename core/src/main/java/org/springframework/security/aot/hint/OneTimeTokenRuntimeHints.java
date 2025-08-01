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

package org.springframework.security.aot.hint;

import org.jspecify.annotations.Nullable;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;

/**
 *
 * A JDBC implementation of an {@link OneTimeTokenService} that uses a
 * {@link JdbcOperations} for {@link OneTimeToken} persistence.
 *
 * @author Max Batischev
 * @since 6.4
 */
class OneTimeTokenRuntimeHints implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
		hints.resources().registerPattern("org/springframework/security/core/ott/jdbc/one-time-tokens-schema.sql");
	}

}
