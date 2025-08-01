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

package org.springframework.security.crypto.password;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * <p>
 * TestCase for Md5PasswordEncoder.
 * </p>
 *
 * @author colin sampaleanu
 * @author Ben Alex
 * @author Ray Krueger
 * @author Luke Taylor
 */
@SuppressWarnings("deprecation")
public class MessageDigestPasswordEncoderTests extends AbstractPasswordEncoderValidationTests {

	@BeforeEach
	void setup() {
		setEncoder(new MessageDigestPasswordEncoder("MD5"));
	}

	@Test
	public void md5BasicFunctionality() {
		String raw = "abc123";
		assertThat(getEncoder().matches(raw, "{THIS_IS_A_SALT}a68aafd90299d0b137de28fb4bb68573")).isTrue();
	}

	@Test
	public void md5NonAsciiPasswordHasCorrectHash() {
		// $ echo -n "??" | md5
		// 7eca689f0d3389d9dea66ae112e5cfd7
		assertThat(getEncoder().matches("\u4F60\u597d", "7eca689f0d3389d9dea66ae112e5cfd7")).isTrue();
	}

	@Test
	public void md5Base64() {
		MessageDigestPasswordEncoder pe = getEncoder();
		pe.setEncodeHashAsBase64(true);
		assertThat(getEncoder().matches("abc123", "{THIS_IS_A_SALT}poqv2QKZ0LE33ij7S7aFcw==")).isTrue();
	}

	@Test
	public void md5StretchFactorIsProcessedCorrectly() {
		MessageDigestPasswordEncoder pe = getEncoder();
		pe.setIterations(2);
		// Calculate value using:
		// echo -n password{salt} | openssl md5 -binary | openssl md5
		assertThat(getEncoder().matches("password", "{salt}eb753fb0c370582b4ee01b30f304b9fc")).isTrue();
	}

	@Test
	public void md5MatchesWhenNullSalt() {
		assertThat(getEncoder().matches("password", "5f4dcc3b5aa765d61d8327deb882cf99")).isTrue();
	}

	@Test
	public void md5MatchesWhenEmptySalt() {
		assertThat(getEncoder().matches("password", "{}f1026a66095fc2058c1f8771ed05d6da")).isTrue();
	}

	@Test
	public void md5MatchesWhenHasSalt() {
		assertThat(getEncoder().matches("password", "{salt}ce421738b1c5540836bdc8ff707f1572")).isTrue();
	}

	@Test
	public void md5EncodeThenMatches() {
		String rawPassword = "password";
		String encode = getEncoder().encode(rawPassword);
		assertThat(getEncoder().matches(rawPassword, encode)).isTrue();
	}

	@Test
	public void testBasicFunctionality() {
		setEncoder(new MessageDigestPasswordEncoder("SHA-1"));
		String raw = "abc123";
		assertThat(getEncoder().matches(raw, "{THIS_IS_A_SALT}b2f50ffcbd3407fe9415c062d55f54731f340d32"));
	}

	@Test
	public void testBase64() {
		MessageDigestPasswordEncoder pe = new MessageDigestPasswordEncoder("SHA-1");
		pe.setEncodeHashAsBase64(true);
		String raw = "abc123";
		assertThat(getEncoder().matches(raw, "{THIS_IS_A_SALT}b2f50ffcbd3407fe9415c062d55f54731f340d32"));
	}

	@Test
	public void test256() {
		MessageDigestPasswordEncoder pe = new MessageDigestPasswordEncoder("SHA-1");
		String raw = "abc123";
		assertThat(getEncoder().matches(raw,
				"{THIS_IS_A_SALT}4b79b7de23eb23b78cc5ede227d532b8a51f89b2ec166f808af76b0dbedc47d7"));
	}

	@Test
	public void testInvalidStrength() {
		assertThatIllegalStateException().isThrownBy(() -> new MessageDigestPasswordEncoder("SHA-666"));
	}

}
