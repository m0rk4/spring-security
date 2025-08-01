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

package org.springframework.security.authorization.method;

import org.springframework.expression.Expression;
import org.springframework.util.Assert;

/**
 * An {@link ExpressionAttribute} that carries additional properties for
 * {@code @PreAuthorize}.
 *
 * @author Marcus da Coregio
 */
class PreAuthorizeExpressionAttribute extends ExpressionAttribute {

	private final MethodAuthorizationDeniedHandler handler;

	PreAuthorizeExpressionAttribute(Expression expression, MethodAuthorizationDeniedHandler handler) {
		super(expression);
		Assert.notNull(handler, "handler cannot be null");
		this.handler = handler;
	}

	MethodAuthorizationDeniedHandler getHandler() {
		return this.handler;
	}

}
