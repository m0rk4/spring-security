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

package org.springframework.security.oauth2.jwt;

import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.core.convert.converter.Converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link MappedJwtClaimSetConverter}
 *
 * @author Josh Cummings
 */
public class MappedJwtClaimSetConverterTests {

	@Test
	public void convertWhenUsingCustomExpiresAtConverterThenIssuedAtConverterStillConsultsIt() {
		Instant at = Instant.ofEpochMilli(1000000000000L);
		Converter<Object, Instant> expiresAtConverter = mock(Converter.class);
		given(expiresAtConverter.convert(any())).willReturn(at);
		MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter
			.withDefaults(Collections.singletonMap(JwtClaimNames.EXP, expiresAtConverter));
		Map<String, Object> source = new HashMap<>();
		Map<String, Object> target = converter.convert(source);
		assertThat(target).containsEntry(JwtClaimNames.IAT, Instant.ofEpochMilli(at.toEpochMilli()).minusSeconds(1));
	}

	@Test
	public void convertWhenUsingDefaultsThenBasesIssuedAtOffOfExpiration() {
		MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
		Map<String, Object> source = Collections.singletonMap(JwtClaimNames.EXP, 1000000000L);
		Map<String, Object> target = converter.convert(source);
		assertThat(target).containsEntry(JwtClaimNames.EXP, Instant.ofEpochSecond(1000000000L));
		assertThat(target).containsEntry(JwtClaimNames.IAT, Instant.ofEpochSecond(1000000000L).minusSeconds(1));
	}

	@Test
	public void convertWhenUsingDefaultsThenCoercesAudienceAccordingToJwtSpec() {
		MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
		Map<String, Object> source = Collections.singletonMap(JwtClaimNames.AUD, "audience");
		Map<String, Object> target = converter.convert(source);
		assertThat(target.get(JwtClaimNames.AUD)).isInstanceOf(Collection.class);
		assertThat(target).containsEntry(JwtClaimNames.AUD, Arrays.asList("audience"));
		source = Collections.singletonMap(JwtClaimNames.AUD, Arrays.asList("one", "two"));
		target = converter.convert(source);
		assertThat(target.get(JwtClaimNames.AUD)).isInstanceOf(Collection.class);
		assertThat(target).containsEntry(JwtClaimNames.AUD, Arrays.asList("one", "two"));
	}

	@Test
	public void convertWhenUsingDefaultsThenCoercesAllAttributesInJwtSpec() {
		MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
		Map<String, Object> source = new HashMap<>();
		source.put(JwtClaimNames.JTI, 1);
		source.put(JwtClaimNames.AUD, "audience");
		source.put(JwtClaimNames.EXP, 2000000000L);
		source.put(JwtClaimNames.IAT, new Date(1000000000000L));
		source.put(JwtClaimNames.ISS, "https://any.url");
		source.put(JwtClaimNames.NBF, 1000000000);
		source.put(JwtClaimNames.SUB, 1234);
		Map<String, Object> target = converter.convert(source);
		assertThat(target).containsEntry(JwtClaimNames.JTI, "1");
		assertThat(target).containsEntry(JwtClaimNames.AUD, Arrays.asList("audience"));
		assertThat(target).containsEntry(JwtClaimNames.EXP, Instant.ofEpochSecond(2000000000L));
		assertThat(target).containsEntry(JwtClaimNames.IAT, Instant.ofEpochSecond(1000000000L));
		assertThat(target).containsEntry(JwtClaimNames.ISS, "https://any.url");
		assertThat(target).containsEntry(JwtClaimNames.NBF, Instant.ofEpochSecond(1000000000L));
		assertThat(target).containsEntry(JwtClaimNames.SUB, "1234");
	}

	@Test
	public void convertWhenUsingCustomConverterThenAllOtherDefaultsAreStillUsed() {
		Converter<Object, String> claimConverter = mock(Converter.class);
		MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter
			.withDefaults(Collections.singletonMap(JwtClaimNames.SUB, claimConverter));
		given(claimConverter.convert(any(Object.class))).willReturn("1234");
		Map<String, Object> source = new HashMap<>();
		source.put(JwtClaimNames.JTI, 1);
		source.put(JwtClaimNames.AUD, "audience");
		source.put(JwtClaimNames.EXP, Instant.ofEpochSecond(2000000000L));
		source.put(JwtClaimNames.IAT, new Date(1000000000000L));
		source.put(JwtClaimNames.ISS, URI.create("https://any.url"));
		source.put(JwtClaimNames.NBF, "1000000000");
		source.put(JwtClaimNames.SUB, 2345);
		Map<String, Object> target = converter.convert(source);
		assertThat(target).containsEntry(JwtClaimNames.JTI, "1");
		assertThat(target).containsEntry(JwtClaimNames.AUD, Arrays.asList("audience"));
		assertThat(target).containsEntry(JwtClaimNames.EXP, Instant.ofEpochSecond(2000000000L));
		assertThat(target).containsEntry(JwtClaimNames.IAT, Instant.ofEpochSecond(1000000000L));
		assertThat(target).containsEntry(JwtClaimNames.ISS, "https://any.url");
		assertThat(target).containsEntry(JwtClaimNames.NBF, Instant.ofEpochSecond(1000000000L));
		assertThat(target).containsEntry(JwtClaimNames.SUB, "1234");
	}

	// gh-10135
	@Test
	public void convertWhenConverterReturnsNullThenClaimIsRemoved() {
		MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter
			.withDefaults(Collections.singletonMap(JwtClaimNames.NBF, (nbfClaimValue) -> null));
		Map<String, Object> source = Collections.singletonMap(JwtClaimNames.NBF, Instant.now());
		Map<String, Object> target = converter.convert(source);
		assertThat(target).doesNotContainKey(JwtClaimNames.NBF);
	}

	@Test
	public void convertWhenClaimValueIsNullThenClaimIsRemoved() {
		MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
		Map<String, Object> source = Collections.singletonMap(JwtClaimNames.ISS, null);
		Map<String, Object> target = converter.convert(source);
		assertThat(target).doesNotContainKey(JwtClaimNames.ISS);
	}

	@Test
	public void convertWhenConverterReturnsValueWhenEntryIsMissingThenEntryIsAdded() {
		Converter<Object, String> claimConverter = mock(Converter.class);
		MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter
			.withDefaults(Collections.singletonMap("custom-claim", claimConverter));
		given(claimConverter.convert(any())).willReturn("custom-value");
		Map<String, Object> source = new HashMap<>();
		Map<String, Object> target = converter.convert(source);
		assertThat(target).containsEntry("custom-claim", "custom-value");
	}

	@Test
	public void convertWhenUsingConstructorThenOnlyConvertersInThatMapAreUsedForConversion() {
		Converter<Object, String> claimConverter = mock(Converter.class);
		MappedJwtClaimSetConverter converter = new MappedJwtClaimSetConverter(
				Collections.singletonMap(JwtClaimNames.SUB, claimConverter));
		given(claimConverter.convert(any(Object.class))).willReturn("1234");
		Map<String, Object> source = new HashMap<>();
		source.put(JwtClaimNames.JTI, new Object());
		source.put(JwtClaimNames.AUD, new Object());
		source.put(JwtClaimNames.EXP, Instant.ofEpochSecond(1L));
		source.put(JwtClaimNames.IAT, Instant.ofEpochSecond(1L));
		source.put(JwtClaimNames.ISS, new Object());
		source.put(JwtClaimNames.NBF, new Object());
		source.put(JwtClaimNames.SUB, new Object());
		Map<String, Object> target = converter.convert(source);
		assertThat(target).containsEntry(JwtClaimNames.JTI, source.get(JwtClaimNames.JTI));
		assertThat(target).containsEntry(JwtClaimNames.AUD, source.get(JwtClaimNames.AUD));
		assertThat(target).containsEntry(JwtClaimNames.EXP, source.get(JwtClaimNames.EXP));
		assertThat(target).containsEntry(JwtClaimNames.IAT, source.get(JwtClaimNames.IAT));
		assertThat(target).containsEntry(JwtClaimNames.ISS, source.get(JwtClaimNames.ISS));
		assertThat(target).containsEntry(JwtClaimNames.NBF, source.get(JwtClaimNames.NBF));
		assertThat(target).containsEntry(JwtClaimNames.SUB, "1234");
	}

	@Test
	public void convertWhenUsingDefaultsThenFailedConversionThrowsIllegalStateException() {
		MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
		Map<String, Object> badIssuer = Collections.singletonMap(JwtClaimNames.ISS, "https://badly formed iss");
		assertThatIllegalStateException().isThrownBy(() -> converter.convert(badIssuer));
		Map<String, Object> badIssuedAt = Collections.singletonMap(JwtClaimNames.IAT, "badly-formed-iat");
		assertThatIllegalStateException().isThrownBy(() -> converter.convert(badIssuedAt));
		Map<String, Object> badExpiresAt = Collections.singletonMap(JwtClaimNames.EXP, "badly-formed-exp");
		assertThatIllegalStateException().isThrownBy(() -> converter.convert(badExpiresAt));
		Map<String, Object> badNotBefore = Collections.singletonMap(JwtClaimNames.NBF, "badly-formed-nbf");
		assertThatIllegalStateException().isThrownBy(() -> converter.convert(badNotBefore));
	}

	// gh-6073
	@Test
	public void convertWhenIssuerIsNotAUriThenConvertsToString() {
		MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
		Map<String, Object> nonUriIssuer = Collections.singletonMap(JwtClaimNames.ISS, "issuer");
		Map<String, Object> target = converter.convert(nonUriIssuer);
		assertThat(target).containsEntry(JwtClaimNames.ISS, "issuer");
	}

	// gh-6073
	@Test
	public void convertWhenIssuerIsOfTypeURLThenConvertsToString() throws Exception {
		MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
		Map<String, Object> issuer = Collections.singletonMap(JwtClaimNames.ISS, new URL("https://issuer"));
		Map<String, Object> target = converter.convert(issuer);
		assertThat(target).containsEntry(JwtClaimNames.ISS, "https://issuer");
	}

	@Test
	public void constructWhenAnyParameterIsNullThenIllegalArgumentException() {
		assertThatIllegalArgumentException().isThrownBy(() -> new MappedJwtClaimSetConverter(null));
	}

	@Test
	public void withDefaultsWhenAnyParameterIsNullThenIllegalArgumentException() {
		assertThatIllegalArgumentException().isThrownBy(() -> MappedJwtClaimSetConverter.withDefaults(null));
	}

}
