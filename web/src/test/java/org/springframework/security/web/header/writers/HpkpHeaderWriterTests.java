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

package org.springframework.security.web.header.writers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * @author Tim Ysewyn
 * @author Ankur Pathak
 *
 */
public class HpkpHeaderWriterTests {

	private static final Map<String, String> DEFAULT_PINS;
	static {
		Map<String, String> defaultPins = new LinkedHashMap<>();
		defaultPins.put("d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=", "sha256");
		DEFAULT_PINS = Collections.unmodifiableMap(defaultPins);
	}
	private MockHttpServletRequest request;

	private MockHttpServletResponse response;

	private HpkpHeaderWriter writer;

	private static final String HPKP_HEADER_NAME = "Public-Key-Pins";

	private static final String HPKP_RO_HEADER_NAME = "Public-Key-Pins-Report-Only";

	@BeforeEach
	public void setup() {
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		this.writer = new HpkpHeaderWriter();
		Map<String, String> defaultPins = new LinkedHashMap<>();
		defaultPins.put("d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=", "sha256");
		this.writer.setPins(defaultPins);
		this.request.setSecure(true);
	}

	@Test
	public void writeHeadersDefaultValues() {
		this.writer.writeHeaders(this.request, this.response);
		assertThat(this.response.getHeaderNames()).hasSize(1);
		assertThat(this.response.getHeader("Public-Key-Pins-Report-Only"))
			.isEqualTo("max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"");
	}

	@Test
	public void maxAgeCustomConstructorWriteHeaders() {
		this.writer = new HpkpHeaderWriter(2592000);
		this.writer.setPins(DEFAULT_PINS);
		this.writer.writeHeaders(this.request, this.response);
		assertThat(this.response.getHeaderNames()).hasSize(1);
		assertThat(this.response.getHeader("Public-Key-Pins-Report-Only"))
			.isEqualTo("max-age=2592000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"");
	}

	@Test
	public void maxAgeAndIncludeSubdomainsCustomConstructorWriteHeaders() {
		this.writer = new HpkpHeaderWriter(2592000, true);
		this.writer.setPins(DEFAULT_PINS);
		this.writer.writeHeaders(this.request, this.response);
		assertThat(this.response.getHeaderNames()).hasSize(1);
		assertThat(this.response.getHeader("Public-Key-Pins-Report-Only")).isEqualTo(
				"max-age=2592000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; includeSubDomains");
	}

	@Test
	public void allArgsCustomConstructorWriteHeaders() {
		this.writer = new HpkpHeaderWriter(2592000, true, false);
		this.writer.setPins(DEFAULT_PINS);
		this.writer.writeHeaders(this.request, this.response);
		assertThat(this.response.getHeaderNames()).hasSize(1);
		assertThat(this.response.getHeader("Public-Key-Pins")).isEqualTo(
				"max-age=2592000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; includeSubDomains");
	}

	@Test
	public void writeHeadersCustomMaxAgeInSeconds() {
		this.writer.setMaxAgeInSeconds(2592000);
		this.writer.writeHeaders(this.request, this.response);
		assertThat(this.response.getHeaderNames()).hasSize(1);
		assertThat(this.response.getHeader("Public-Key-Pins-Report-Only"))
			.isEqualTo("max-age=2592000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"");
	}

	@Test
	public void writeHeadersIncludeSubDomains() {
		this.writer.setIncludeSubDomains(true);
		this.writer.writeHeaders(this.request, this.response);
		assertThat(this.response.getHeaderNames()).hasSize(1);
		assertThat(this.response.getHeader("Public-Key-Pins-Report-Only")).isEqualTo(
				"max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; includeSubDomains");
	}

	@Test
	public void writeHeadersTerminateConnection() {
		this.writer.setReportOnly(false);
		this.writer.writeHeaders(this.request, this.response);
		assertThat(this.response.getHeaderNames()).hasSize(1);
		assertThat(this.response.getHeader("Public-Key-Pins"))
			.isEqualTo("max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"");
	}

	@Test
	public void writeHeadersTerminateConnectionWithURI() throws URISyntaxException {
		this.writer.setReportOnly(false);
		this.writer.setReportUri(new URI("https://example.com/pkp-report"));
		this.writer.writeHeaders(this.request, this.response);
		assertThat(this.response.getHeaderNames()).hasSize(1);
		assertThat(this.response.getHeader("Public-Key-Pins")).isEqualTo(
				"max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; report-uri=\"https://example.com/pkp-report\"");
	}

	@Test
	public void writeHeadersTerminateConnectionWithURIAsString() {
		this.writer.setReportOnly(false);
		this.writer.setReportUri("https://example.com/pkp-report");
		this.writer.writeHeaders(this.request, this.response);
		assertThat(this.response.getHeaderNames()).hasSize(1);
		assertThat(this.response.getHeader("Public-Key-Pins")).isEqualTo(
				"max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; report-uri=\"https://example.com/pkp-report\"");
	}

	@Test
	public void writeHeadersAddSha256Pins() {
		this.writer.addSha256Pins("d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=",
				"E9CZ9INDbd+2eRQozYqqbQ2yXLVKB9+xcprMF+44U1g=");
		this.writer.writeHeaders(this.request, this.response);
		assertThat(this.response.getHeaderNames()).hasSize(1);
		assertThat(this.response.getHeader("Public-Key-Pins-Report-Only")).isEqualTo(
				"max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; pin-sha256=\"E9CZ9INDbd+2eRQozYqqbQ2yXLVKB9+xcprMF+44U1g=\"");
	}

	@Test
	public void writeHeadersInsecureRequestDoesNotWriteHeader() {
		this.request.setSecure(false);
		this.writer.writeHeaders(this.request, this.response);
		assertThat(this.response.getHeaderNames()).isEmpty();
	}

	@Test
	public void setMaxAgeInSecondsToNegative() {
		assertThatIllegalArgumentException().isThrownBy(() -> this.writer.setMaxAgeInSeconds(-1));
	}

	@Test
	public void addSha256PinsWithNullPin() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> this.writer.addSha256Pins("d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=", null));
	}

	@Test
	public void setIncorrectReportUri() {
		assertThatIllegalArgumentException().isThrownBy(() -> this.writer.setReportUri("some url here..."));
	}

	@Test
	public void writePublicKeyPinsHeaderOnlyWhenNotPresent() {
		String value = new String("value");
		this.response.setHeader(HPKP_HEADER_NAME, value);
		this.writer.setReportOnly(false);
		this.writer.writeHeaders(this.request, this.response);
		assertThat(this.response.getHeader(HPKP_HEADER_NAME)).isSameAs(value);
	}

	@Test
	public void writePublicKeyPinsReportOnlyHeaderWhenNotPresent() {
		String value = new String("value");
		this.response.setHeader(HPKP_RO_HEADER_NAME, value);
		this.writer.setReportOnly(false);
		this.writer.writeHeaders(this.request, this.response);
		assertThat(this.response.getHeader(HPKP_RO_HEADER_NAME)).isSameAs(value);
	}

}
