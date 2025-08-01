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

package org.springframework.security.config.annotation.web.configuration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.PasswordEncodedUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Rob Winch
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebAppConfiguration
public class AuthenticationPrincipalArgumentResolverTests {

	@Autowired
	WebApplicationContext wac;

	@AfterEach
	public void cleanup() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void authenticationPrincipalExpressionWhenBeanExpressionSuppliedThenBeanUsed() throws Exception {
		User user = new User("user", "password", AuthorityUtils.createAuthorityList("ROLE_USER"));
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(
				UsernamePasswordAuthenticationToken.authenticated(user, user.getPassword(), user.getAuthorities()));
		SecurityContextHolder.setContext(context);
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		// @formatter:off
		mockMvc.perform(get("/users/self"))
				.andExpect(status().isOk())
				.andExpect(content().string("extracted-user"));
		// @formatter:on
	}

	@Configuration
	@EnableWebSecurity
	@EnableWebMvc
	static class Config {

		@Bean
		UserDetailsService userDetailsService() {
			return new InMemoryUserDetailsManager(PasswordEncodedUser.user());
		}

		@Bean
		public UsernameExtractor usernameExtractor() {
			return new UsernameExtractor();
		}

		@RestController
		static class UserController {

			@GetMapping("/users/self")
			public String usersSelf(
					@AuthenticationPrincipal(expression = "@usernameExtractor.extract(#this)") String userName) {
				return userName;
			}

		}

	}

	static class UsernameExtractor {

		public String extract(User u) {
			return "extracted-" + u.getUsername();
		}

	}

}
