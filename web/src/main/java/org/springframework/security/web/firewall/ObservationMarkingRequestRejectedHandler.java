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

package org.springframework.security.web.firewall;

import java.io.IOException;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class ObservationMarkingRequestRejectedHandler implements RequestRejectedHandler {

	private final ObservationRegistry registry;

	public ObservationMarkingRequestRejectedHandler(ObservationRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, RequestRejectedException exception)
			throws IOException, ServletException {
		Observation observation = this.registry.getCurrentObservation();
		if (observation != null) {
			observation.error(exception);
		}
	}

}
