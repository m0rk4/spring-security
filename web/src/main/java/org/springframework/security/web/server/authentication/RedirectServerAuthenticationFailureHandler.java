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

package org.springframework.security.web.server.authentication;

import java.net.URI;

import reactor.core.publisher.Mono;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.util.Assert;

/**
 * Performs a redirect to a specified location.
 *
 * @author Rob Winch
 * @since 5.0
 */
public class RedirectServerAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

	private final URI location;

	private ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

	/**
	 * Creates an instance
	 * @param location the location to redirect to (i.e. "/login?failed")
	 */
	public RedirectServerAuthenticationFailureHandler(String location) {
		Assert.notNull(location, "location cannot be null");
		this.location = URI.create(location);
	}

	/**
	 * Sets the RedirectStrategy to use.
	 * @param redirectStrategy the strategy to use. Default is DefaultRedirectStrategy.
	 */
	public void setRedirectStrategy(ServerRedirectStrategy redirectStrategy) {
		Assert.notNull(redirectStrategy, "redirectStrategy cannot be null");
		this.redirectStrategy = redirectStrategy;
	}

	@Override
	public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
		return this.redirectStrategy.sendRedirect(webFilterExchange.getExchange(), this.location);
	}

}
