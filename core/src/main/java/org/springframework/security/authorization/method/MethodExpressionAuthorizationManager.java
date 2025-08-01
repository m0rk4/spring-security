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

import java.util.function.Supplier;

import org.aopalliance.intercept.MethodInvocation;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.ExpressionAuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

/**
 * An expression-based {@link AuthorizationManager} that determines the access by
 * evaluating the provided expression against the {@link MethodInvocation}.
 *
 * @author Josh Cummings
 * @since 5.8
 */
public final class MethodExpressionAuthorizationManager implements AuthorizationManager<MethodInvocation> {

	private SecurityExpressionHandler<MethodInvocation> expressionHandler = new DefaultMethodSecurityExpressionHandler();

	private Expression expression;

	/**
	 * Creates an instance.
	 * @param expressionString the raw expression string to parse
	 */
	public MethodExpressionAuthorizationManager(String expressionString) {
		Assert.hasText(expressionString, "expressionString cannot be empty");
		this.expression = this.expressionHandler.getExpressionParser().parseExpression(expressionString);
	}

	/**
	 * Sets the {@link SecurityExpressionHandler} to be used. The default is
	 * {@link DefaultMethodSecurityExpressionHandler}.
	 * @param expressionHandler the {@link SecurityExpressionHandler} to use
	 */
	public void setExpressionHandler(SecurityExpressionHandler<MethodInvocation> expressionHandler) {
		Assert.notNull(expressionHandler, "expressionHandler cannot be null");
		this.expressionHandler = expressionHandler;
		this.expression = expressionHandler.getExpressionParser()
			.parseExpression(this.expression.getExpressionString());
	}

	/**
	 * Determines the access by evaluating the provided expression.
	 * @param authentication the {@link Supplier} of the {@link Authentication} to check
	 * @param context the {@link MethodInvocation} to check
	 * @return an {@link ExpressionAuthorizationDecision} based on the evaluated
	 * expression
	 */
	@Override
	public AuthorizationResult authorize(Supplier<Authentication> authentication, MethodInvocation context) {
		EvaluationContext ctx = this.expressionHandler.createEvaluationContext(authentication, context);
		boolean granted = ExpressionUtils.evaluateAsBoolean(this.expression, ctx);
		return new ExpressionAuthorizationDecision(granted, this.expression);
	}

	@Override
	public String toString() {
		return "WebExpressionAuthorizationManager[expression='" + this.expression + "']";
	}

}
