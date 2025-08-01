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

import java.lang.reflect.Method;

import org.jspecify.annotations.Nullable;

import org.springframework.expression.Expression;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;
import org.springframework.security.core.annotation.SecurityAnnotationScanner;
import org.springframework.security.core.annotation.SecurityAnnotationScanners;

/**
 * For internal use only, as this contract is likely to change.
 *
 * @author Evgeniy Cheban
 * @author DingHao
 * @since 5.8
 */
final class PreFilterExpressionAttributeRegistry
		extends AbstractExpressionAttributeRegistry<PreFilterExpressionAttributeRegistry.PreFilterExpressionAttribute> {

	private SecurityAnnotationScanner<PreFilter> scanner = SecurityAnnotationScanners.requireUnique(PreFilter.class);

	@Override
	@Nullable PreFilterExpressionAttribute resolveAttribute(Method method, @Nullable Class<?> targetClass) {
		PreFilter preFilter = findPreFilterAnnotation(method, targetClass);
		if (preFilter == null) {
			return null;
		}
		Expression preFilterExpression = getExpressionHandler().getExpressionParser()
			.parseExpression(preFilter.value());
		return new PreFilterExpressionAttribute(preFilterExpression, preFilter.filterTarget());
	}

	void setTemplateDefaults(AnnotationTemplateExpressionDefaults defaults) {
		this.scanner = SecurityAnnotationScanners.requireUnique(PreFilter.class, defaults);
	}

	private @Nullable PreFilter findPreFilterAnnotation(Method method, @Nullable Class<?> targetClass) {
		Class<?> targetClassToUse = targetClass(method, targetClass);
		return this.scanner.scan(method, targetClassToUse);
	}

	static final class PreFilterExpressionAttribute extends ExpressionAttribute {

		private final String filterTarget;

		private PreFilterExpressionAttribute(Expression expression, String filterTarget) {
			super(expression);
			this.filterTarget = filterTarget;
		}

		String getFilterTarget() {
			return this.filterTarget;
		}

	}

}
