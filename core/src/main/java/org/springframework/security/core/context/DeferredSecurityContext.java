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

package org.springframework.security.core.context;

import java.util.function.Supplier;

/**
 * An interface that allows delayed access to a {@link SecurityContext} that may be
 * generated.
 *
 * @author Steve Riesenberg
 * @since 5.8
 */
public interface DeferredSecurityContext extends Supplier<SecurityContext> {

	/**
	 * Returns true if {@link #get()} refers to a generated {@link SecurityContext} or
	 * false if it already existed.
	 * @return true if {@link #get()} refers to a generated {@link SecurityContext} or
	 * false if it already existed
	 */
	boolean isGenerated();

}
