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

package org.springframework.security.config.core.userdetails;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Rob Winch
 * @since 5.0
 */
@ExtendWith(SpringExtension.class)
public class ReactiveUserDetailsServiceResourceFactoryBeanPropertiesResourceLocationITests {

	@Autowired
	ReactiveUserDetailsService users;

	@Test
	public void loadUserByUsernameWhenUserFoundThenNotNull() {
		assertThat(this.users.findByUsername("user").block()).isNotNull();
	}

	@Configuration
	static class Config {

		@Bean
		ReactiveUserDetailsServiceResourceFactoryBean userDetailsService() {
			return ReactiveUserDetailsServiceResourceFactoryBean.fromResourceLocation("classpath:users.properties");
		}

	}

}
