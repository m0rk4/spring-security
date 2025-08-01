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

package org.springframework.security.oauth2.client;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import reactor.core.publisher.Mono;

import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.util.Assert;

/**
 * An implementation of a {@link ReactiveOAuth2AuthorizedClientProvider} for the
 * {@link AuthorizationGrantType#CLIENT_CREDENTIALS client_credentials} grant.
 *
 * @author Joe Grandja
 * @since 5.2
 * @see ReactiveOAuth2AuthorizedClientProvider
 * @see WebClientReactiveClientCredentialsTokenResponseClient
 */
public final class ClientCredentialsReactiveOAuth2AuthorizedClientProvider
		implements ReactiveOAuth2AuthorizedClientProvider {

	private ReactiveOAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> accessTokenResponseClient = new WebClientReactiveClientCredentialsTokenResponseClient();

	private Duration clockSkew = Duration.ofSeconds(60);

	private Clock clock = Clock.systemUTC();

	/**
	 * Attempt to authorize (or re-authorize) the
	 * {@link OAuth2AuthorizationContext#getClientRegistration() client} in the provided
	 * {@code context}. Returns an empty {@code Mono} if authorization (or
	 * re-authorization) is not supported, e.g. the client's
	 * {@link ClientRegistration#getAuthorizationGrantType() authorization grant type} is
	 * not {@link AuthorizationGrantType#CLIENT_CREDENTIALS client_credentials} OR the
	 * {@link OAuth2AuthorizedClient#getAccessToken() access token} is not expired.
	 * @param context the context that holds authorization-specific state for the client
	 * @return the {@link OAuth2AuthorizedClient} or an empty {@code Mono} if
	 * authorization (or re-authorization) is not supported
	 */
	@Override
	public Mono<OAuth2AuthorizedClient> authorize(OAuth2AuthorizationContext context) {
		Assert.notNull(context, "context cannot be null");
		ClientRegistration clientRegistration = context.getClientRegistration();
		if (!AuthorizationGrantType.CLIENT_CREDENTIALS.equals(clientRegistration.getAuthorizationGrantType())) {
			return Mono.empty();
		}
		OAuth2AuthorizedClient authorizedClient = context.getAuthorizedClient();
		if (authorizedClient != null && !hasTokenExpired(authorizedClient.getAccessToken())) {
			// If client is already authorized but access token is NOT expired than no
			// need for re-authorization
			return Mono.empty();
		}
		// As per spec, in section 4.4.3 Access Token Response
		// https://tools.ietf.org/html/rfc6749#section-4.4.3
		// A refresh token SHOULD NOT be included.
		//
		// Therefore, renewing an expired access token (re-authorization)
		// is the same as acquiring a new access token (authorization).
		return Mono.just(new OAuth2ClientCredentialsGrantRequest(clientRegistration))
			.flatMap(this.accessTokenResponseClient::getTokenResponse)
			.onErrorMap(OAuth2AuthorizationException.class,
					(ex) -> new ClientAuthorizationException(ex.getError(), clientRegistration.getRegistrationId(), ex))
			.map((tokenResponse) -> new OAuth2AuthorizedClient(clientRegistration, context.getPrincipal().getName(),
					tokenResponse.getAccessToken()));
	}

	private boolean hasTokenExpired(OAuth2Token token) {
		return this.clock.instant().isAfter(token.getExpiresAt().minus(this.clockSkew));
	}

	/**
	 * Sets the client used when requesting an access token credential at the Token
	 * Endpoint for the {@code client_credentials} grant.
	 * @param accessTokenResponseClient the client used when requesting an access token
	 * credential at the Token Endpoint for the {@code client_credentials} grant
	 */
	public void setAccessTokenResponseClient(
			ReactiveOAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> accessTokenResponseClient) {
		Assert.notNull(accessTokenResponseClient, "accessTokenResponseClient cannot be null");
		this.accessTokenResponseClient = accessTokenResponseClient;
	}

	/**
	 * Sets the maximum acceptable clock skew, which is used when checking the
	 * {@link OAuth2AuthorizedClient#getAccessToken() access token} expiry. The default is
	 * 60 seconds.
	 *
	 * <p>
	 * An access token is considered expired if
	 * {@code OAuth2AccessToken#getExpiresAt() - clockSkew} is before the current time
	 * {@code clock#instant()}.
	 * @param clockSkew the maximum acceptable clock skew
	 */
	public void setClockSkew(Duration clockSkew) {
		Assert.notNull(clockSkew, "clockSkew cannot be null");
		Assert.isTrue(clockSkew.getSeconds() >= 0, "clockSkew must be >= 0");
		this.clockSkew = clockSkew;
	}

	/**
	 * Sets the {@link Clock} used in {@link Instant#now(Clock)} when checking the access
	 * token expiry.
	 * @param clock the clock
	 */
	public void setClock(Clock clock) {
		Assert.notNull(clock, "clock cannot be null");
		this.clock = clock;
	}

}
