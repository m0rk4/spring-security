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

package org.springframework.security.web;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * @author Luke Taylor
 * @author Mark Chesney
 * @since 3.0
 */
public class DefaultRedirectStrategyTests {

	@Test
	public void contextRelativeUrlWithContextNameInHostnameIsHandledCorrectly() throws Exception {
		DefaultRedirectStrategy rds = new DefaultRedirectStrategy();
		rds.setContextRelative(true);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setContextPath("/context");
		MockHttpServletResponse response = new MockHttpServletResponse();
		rds.sendRedirect(request, response, "https://context.blah.com/context/remainder");
		assertThat(response.getRedirectedUrl()).isEqualTo("remainder");
	}

	// SEC-2177
	@Test
	public void contextRelativeUrlWithMultipleSchemesInHostnameIsHandledCorrectly() throws Exception {
		DefaultRedirectStrategy rds = new DefaultRedirectStrategy();
		rds.setContextRelative(true);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setContextPath("/context");
		MockHttpServletResponse response = new MockHttpServletResponse();
		rds.sendRedirect(request, response, "https://https://context.blah.com/context/remainder");
		assertThat(response.getRedirectedUrl()).isEqualTo("remainder");
	}

	@Test
	public void contextRelativeShouldThrowExceptionIfURLDoesNotContainContextPath() throws Exception {
		DefaultRedirectStrategy rds = new DefaultRedirectStrategy();
		rds.setContextRelative(true);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setContextPath("/context");
		MockHttpServletResponse response = new MockHttpServletResponse();
		assertThatIllegalArgumentException()
			.isThrownBy(() -> rds.sendRedirect(request, response, "https://redirectme.somewhere.else"));
	}

	@Test
	public void statusCodeIsHandledCorrectly() throws Exception {
		// given
		DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
		redirectStrategy.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		// when
		redirectStrategy.sendRedirect(request, response, "/requested");

		// then
		assertThat(response.isCommitted()).isTrue();
		assertThat(response.getRedirectedUrl()).isEqualTo("/requested");
		assertThat(response.getStatus()).isEqualTo(307);
	}

}
