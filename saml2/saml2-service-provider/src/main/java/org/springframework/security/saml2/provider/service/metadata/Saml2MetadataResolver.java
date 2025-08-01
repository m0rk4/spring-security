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

package org.springframework.security.saml2.provider.service.metadata;

import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;

/**
 * Resolves the SAML 2.0 Relying Party Metadata for a given
 * {@link RelyingPartyRegistration}
 *
 * @author Jakub Kubrynski
 * @author Josh Cummings
 * @since 5.4
 */
public interface Saml2MetadataResolver {

	/**
	 * Resolve the given relying party's metadata
	 * @param relyingPartyRegistration the relying party
	 * @return the relying party's metadata
	 */
	String resolve(RelyingPartyRegistration relyingPartyRegistration);

	default String resolve(Iterable<RelyingPartyRegistration> relyingPartyRegistrations) {
		return resolve(relyingPartyRegistrations.iterator().next());
	}

}
