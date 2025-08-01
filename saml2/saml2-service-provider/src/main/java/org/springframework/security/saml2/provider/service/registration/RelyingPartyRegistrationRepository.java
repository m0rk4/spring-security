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

package org.springframework.security.saml2.provider.service.registration;

/**
 * A repository for {@link RelyingPartyRegistration}s
 *
 * @author Filip Hanik
 * @author Josh Cummings
 * @since 5.2
 */
public interface RelyingPartyRegistrationRepository {

	/**
	 * Returns the relying party registration identified by the provided
	 * {@code registrationId}, or {@code null} if not found.
	 * @param registrationId the registration identifier
	 * @return the {@link RelyingPartyRegistration} if found, otherwise {@code null}
	 */
	RelyingPartyRegistration findByRegistrationId(String registrationId);

	/**
	 * Returns the unique relying party registration associated with the asserting party's
	 * {@code entityId} or {@code null} if there is no unique match.
	 * @param entityId the asserting party's entity id
	 * @return the unique {@link RelyingPartyRegistration} associated the given asserting
	 * party; {@code null} of there is no unique match asserting party
	 * @since 6.1
	 */
	default RelyingPartyRegistration findUniqueByAssertingPartyEntityId(String entityId) {
		return findByRegistrationId(entityId);
	}

}
