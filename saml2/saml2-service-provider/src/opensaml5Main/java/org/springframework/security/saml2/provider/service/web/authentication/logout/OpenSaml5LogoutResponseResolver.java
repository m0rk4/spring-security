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

package org.springframework.security.saml2.provider.service.web.authentication.logout;

import java.time.Clock;
import java.time.Instant;
import java.util.function.Consumer;

import jakarta.servlet.http.HttpServletRequest;
import org.opensaml.saml.saml2.core.LogoutRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationException;
import org.springframework.security.saml2.provider.service.authentication.logout.Saml2LogoutResponse;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.util.Assert;

/**
 * A {@link Saml2LogoutResponseResolver} for resolving SAML 2.0 Logout Responses with
 * OpenSAML 5
 *
 * @author Josh Cummings
 * @since 5.6
 */
public final class OpenSaml5LogoutResponseResolver implements Saml2LogoutResponseResolver {

	private final BaseOpenSamlLogoutResponseResolver delegate;

	public OpenSaml5LogoutResponseResolver(RelyingPartyRegistrationRepository registrations) {
		this.delegate = new BaseOpenSamlLogoutResponseResolver(registrations, (request, id) -> {
			if (id == null) {
				return null;
			}
			return registrations.findByRegistrationId(id);
		}, new OpenSaml5Template());
	}

	/**
	 * Construct a {@link OpenSaml5LogoutResponseResolver}
	 */
	public OpenSaml5LogoutResponseResolver(RelyingPartyRegistrationResolver relyingPartyRegistrationResolver) {
		this.delegate = new BaseOpenSamlLogoutResponseResolver(null, relyingPartyRegistrationResolver,
				new OpenSaml5Template());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Saml2LogoutResponse resolve(HttpServletRequest request, Authentication authentication) {
		return this.delegate.resolve(request, authentication);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Saml2LogoutResponse resolve(HttpServletRequest request, Authentication authentication,
			Saml2AuthenticationException exception) {
		return this.delegate.resolve(request, authentication, exception);
	}

	/**
	 * Set a {@link Consumer} for modifying the OpenSAML {@link LogoutRequest}
	 * @param parametersConsumer a consumer that accepts an
	 * {@link OpenSaml5LogoutRequestResolver.LogoutRequestParameters}
	 */
	public void setParametersConsumer(Consumer<LogoutResponseParameters> parametersConsumer) {
		Assert.notNull(parametersConsumer, "parametersConsumer cannot be null");
		this.delegate
			.setParametersConsumer((parameters) -> parametersConsumer.accept(new LogoutResponseParameters(parameters)));
	}

	/**
	 * Use this {@link Clock} for determining the issued {@link Instant}
	 * @param clock the {@link Clock} to use
	 */
	public void setClock(Clock clock) {
		Assert.notNull(clock, "clock must not be null");
		this.delegate.setClock(clock);
	}

	public static final class LogoutResponseParameters {

		private final HttpServletRequest request;

		private final RelyingPartyRegistration registration;

		private final Authentication authentication;

		private final LogoutRequest logoutRequest;

		public LogoutResponseParameters(HttpServletRequest request, RelyingPartyRegistration registration,
				Authentication authentication, LogoutRequest logoutRequest) {
			this.request = request;
			this.registration = registration;
			this.authentication = authentication;
			this.logoutRequest = logoutRequest;
		}

		LogoutResponseParameters(BaseOpenSamlLogoutResponseResolver.LogoutResponseParameters parameters) {
			this(parameters.getRequest(), parameters.getRelyingPartyRegistration(), parameters.getAuthentication(),
					parameters.getLogoutRequest());
		}

		public HttpServletRequest getRequest() {
			return this.request;
		}

		public RelyingPartyRegistration getRelyingPartyRegistration() {
			return this.registration;
		}

		public Authentication getAuthentication() {
			return this.authentication;
		}

		public LogoutRequest getLogoutRequest() {
			return this.logoutRequest;
		}

	}

}
