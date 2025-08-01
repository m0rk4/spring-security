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

package org.springframework.security.concurrent;

import java.util.concurrent.Executor;

import org.jspecify.annotations.Nullable;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;

/**
 * An {@link Executor} which wraps each {@link Runnable} in a
 * {@link DelegatingSecurityContextRunnable}.
 *
 * @author Rob Winch
 * @since 3.2
 */
public class DelegatingSecurityContextExecutor extends AbstractDelegatingSecurityContextSupport implements Executor {

	private final Executor delegate;

	/**
	 * Creates a new {@link DelegatingSecurityContextExecutor} that uses the specified
	 * {@link SecurityContext}.
	 * @param delegateExecutor the {@link Executor} to delegate to. Cannot be null.
	 * @param securityContext the {@link SecurityContext} to use for each
	 * {@link DelegatingSecurityContextRunnable} or null to default to the current
	 * {@link SecurityContext}
	 */
	public DelegatingSecurityContextExecutor(Executor delegateExecutor, @Nullable SecurityContext securityContext) {
		super(securityContext);
		Assert.notNull(delegateExecutor, "delegateExecutor cannot be null");
		this.delegate = delegateExecutor;
	}

	/**
	 * Creates a new {@link DelegatingSecurityContextExecutor} that uses the current
	 * {@link SecurityContext} from the {@link SecurityContextHolder} at the time the task
	 * is submitted.
	 * @param delegate the {@link Executor} to delegate to. Cannot be null.
	 */
	public DelegatingSecurityContextExecutor(Executor delegate) {
		this(delegate, null);
	}

	@Override
	public final void execute(Runnable task) {
		this.delegate.execute(wrap(task));
	}

	protected final Executor getDelegateExecutor() {
		return this.delegate;
	}

	/**
	 * Sets the {@link SecurityContextHolderStrategy} to use. The default action is to use
	 * the {@link SecurityContextHolderStrategy} stored in {@link SecurityContextHolder}.
	 *
	 * @since 5.8
	 */
	public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
		super.setSecurityContextHolderStrategy(securityContextHolderStrategy);
	}

}
