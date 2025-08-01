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

package org.springframework.security.oauth2.jwt;

/**
 * The Registered Claim Names defined by the JSON Web Token (JWT) specification that may
 * be contained in the JSON object JWT Claims Set.
 *
 * @author Joe Grandja
 * @since 5.0
 * @see JwtClaimAccessor
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7519#section-4">JWT
 * Claims</a>
 */
public final class JwtClaimNames {

	/**
	 * {@code iss} - the Issuer claim identifies the principal that issued the JWT
	 */
	public static final String ISS = "iss";

	/**
	 * {@code sub} - the Subject claim identifies the principal that is the subject of the
	 * JWT
	 */
	public static final String SUB = "sub";

	/**
	 * {@code aud} - the Audience claim identifies the recipient(s) that the JWT is
	 * intended for
	 */
	public static final String AUD = "aud";

	/**
	 * {@code exp} - the Expiration time claim identifies the expiration time on or after
	 * which the JWT MUST NOT be accepted for processing
	 */
	public static final String EXP = "exp";

	/**
	 * {@code nbf} - the Not Before claim identifies the time before which the JWT MUST
	 * NOT be accepted for processing
	 */
	public static final String NBF = "nbf";

	/**
	 * {@code iat} - The Issued at claim identifies the time at which the JWT was issued
	 */
	public static final String IAT = "iat";

	/**
	 * {@code jti} - The JWT ID claim provides a unique identifier for the JWT
	 */
	public static final String JTI = "jti";

	private JwtClaimNames() {
	}

}
