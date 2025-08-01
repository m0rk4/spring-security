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

package org.springframework.security.oauth2.core.endpoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link OAuth2AuthorizationExchange}.
 *
 * @author Joe Grandja
 */
public class OAuth2AuthorizationExchangeTests {

	@Test
	public void constructorWhenAuthorizationRequestIsNullThenThrowIllegalArgumentException() {
		assertThatIllegalArgumentException().isThrownBy(
				() -> new OAuth2AuthorizationExchange(null, TestOAuth2AuthorizationResponses.success().build()));
	}

	@Test
	public void constructorWhenAuthorizationResponseIsNullThenThrowIllegalArgumentException() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new OAuth2AuthorizationExchange(TestOAuth2AuthorizationRequests.request().build(), null));
	}

	@Test
	public void constructorWhenRequiredArgsProvidedThenCreated() {
		OAuth2AuthorizationRequest authorizationRequest = TestOAuth2AuthorizationRequests.request().build();
		OAuth2AuthorizationResponse authorizationResponse = TestOAuth2AuthorizationResponses.success().build();
		OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(authorizationRequest,
				authorizationResponse);
		assertThat(authorizationExchange.getAuthorizationRequest()).isEqualTo(authorizationRequest);
		assertThat(authorizationExchange.getAuthorizationResponse()).isEqualTo(authorizationResponse);
	}

	@Test
	void oauth2AuthorizationExchangeShouldBeSerializable() throws IOException {
		OAuth2AuthorizationExchange exchange = TestOAuth2AuthorizationExchanges.success();
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(baos)) {
			objectOutputStream.writeObject(exchange);
			objectOutputStream.flush();
			assertThat(baos.size()).isNotZero();
		}
	}

}
