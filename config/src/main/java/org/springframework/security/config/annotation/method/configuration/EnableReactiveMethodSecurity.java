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

package org.springframework.security.config.annotation.method.configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.security.authorization.ReactiveAuthorizationManager;

/**
 * @author Rob Winch
 * @since 5.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ReactiveMethodSecuritySelector.class)
public @interface EnableReactiveMethodSecurity {

	/**
	 * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed to
	 * standard Java interface-based proxies. The default is {@code false}. <strong>
	 * Applicable only if {@link #mode()} is set to {@link AdviceMode#PROXY}</strong>.
	 * <p>
	 * Note that setting this attribute to {@code true} will affect <em>all</em>
	 * Spring-managed beans requiring proxying, not just those marked with
	 * {@code @Cacheable}. For example, other beans marked with Spring's
	 * {@code @Transactional} annotation will be upgraded to subclass proxying at the same
	 * time. This approach has no negative impact in practice unless one is explicitly
	 * expecting one type of proxy vs another, e.g. in tests.
	 */
	boolean proxyTargetClass() default false;

	/**
	 * Indicate how security advice should be applied. The default is
	 * {@link AdviceMode#PROXY}.
	 * @see AdviceMode
	 * @return the {@link AdviceMode} to use
	 */
	AdviceMode mode() default AdviceMode.PROXY;

	/**
	 * Indicate the ordering of the execution of the security advisor when multiple
	 * advices are applied at a specific joinpoint. The default is
	 * {@link Ordered#LOWEST_PRECEDENCE}.
	 * @return the order the security advisor should be applied
	 */
	int order() default Ordered.LOWEST_PRECEDENCE;

	/**
	 * Indicate whether {@link ReactiveAuthorizationManager} based Method Security to be
	 * used.
	 * @since 5.8
	 */
	boolean useAuthorizationManager() default true;

}
