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

package org.springframework.security.oauth2.core.oidc;

import java.time.Instant;
import java.util.List;

/**
 * Test {@link OidcIdToken}s
 *
 * @author Josh Cummings
 */
public final class TestOidcIdTokens {

	private TestOidcIdTokens() {
	}

	public static OidcIdToken.Builder idToken() {
		// @formatter:off
		return OidcIdToken.withTokenValue("id-token")
				.issuer("https://example.com")
				.audience(List.of("client-id"))
				.subject("subject")
				.issuedAt(Instant.now())
				.expiresAt(Instant.now()
				.plusSeconds(86400))
				.claim("id", "id");
		// @formatter:on
	}

}
