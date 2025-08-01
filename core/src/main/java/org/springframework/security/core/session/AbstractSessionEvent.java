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

package org.springframework.security.core.session;

import java.io.Serial;

import org.springframework.context.ApplicationEvent;

/**
 * Abstract superclass for all session related events.
 *
 * @author Eleftheria Stein
 * @since 5.4
 */
public class AbstractSessionEvent extends ApplicationEvent {

	@Serial
	private static final long serialVersionUID = -6878881229287231479L;

	public AbstractSessionEvent(Object source) {
		super(source);
	}

}
