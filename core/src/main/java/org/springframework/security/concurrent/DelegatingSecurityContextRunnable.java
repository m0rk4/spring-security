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

import org.jspecify.annotations.Nullable;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;

/**
 * <p>
 * Wraps a delegate {@link Runnable} with logic for setting up a {@link SecurityContext}
 * before invoking the delegate {@link Runnable} and then removing the
 * {@link SecurityContext} after the delegate has completed.
 * </p>
 * <p>
 * If there is a {@link SecurityContext} that already exists, it will be restored after
 * the {@link #run()} method is invoked.
 * </p>
 *
 * @author Rob Winch
 * @since 3.2
 */
public final class DelegatingSecurityContextRunnable implements Runnable {

	private final Runnable delegate;

	private final boolean explicitSecurityContextProvided;

	private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
		.getContextHolderStrategy();

	/**
	 * The {@link SecurityContext} that the delegate {@link Runnable} will be ran as.
	 */
	private SecurityContext delegateSecurityContext;

	/**
	 * The {@link SecurityContext} that was on the {@link SecurityContextHolder} prior to
	 * being set to the delegateSecurityContext.
	 */
	private @Nullable SecurityContext originalSecurityContext;

	/**
	 * Creates a new {@link DelegatingSecurityContextRunnable} with a specific
	 * {@link SecurityContext}.
	 * @param delegate the delegate {@link Runnable} to run with the specified
	 * {@link SecurityContext}. Cannot be null.
	 * @param securityContext the {@link SecurityContext} to establish for the delegate
	 * {@link Runnable}. Cannot be null.
	 */
	public DelegatingSecurityContextRunnable(Runnable delegate, SecurityContext securityContext) {
		this(delegate, securityContext, true);
	}

	/**
	 * Creates a new {@link DelegatingSecurityContextRunnable} with the
	 * {@link SecurityContext} from the {@link SecurityContextHolder}.
	 * @param delegate the delegate {@link Runnable} to run under the current
	 * {@link SecurityContext}. Cannot be null.
	 */
	public DelegatingSecurityContextRunnable(Runnable delegate) {
		this(delegate, SecurityContextHolder.getContext(), false);
	}

	private DelegatingSecurityContextRunnable(Runnable delegate, SecurityContext securityContext,
			boolean explicitSecurityContextProvided) {
		Assert.notNull(delegate, "delegate cannot be null");
		Assert.notNull(securityContext, "securityContext cannot be null");
		this.delegate = delegate;
		this.delegateSecurityContext = securityContext;
		this.explicitSecurityContextProvided = explicitSecurityContextProvided;
	}

	@Override
	public void run() {
		this.originalSecurityContext = this.securityContextHolderStrategy.getContext();
		try {
			this.securityContextHolderStrategy.setContext(this.delegateSecurityContext);
			this.delegate.run();
		}
		finally {
			SecurityContext emptyContext = this.securityContextHolderStrategy.createEmptyContext();
			if (emptyContext.equals(this.originalSecurityContext)) {
				this.securityContextHolderStrategy.clearContext();
			}
			else {
				this.securityContextHolderStrategy.setContext(this.originalSecurityContext);
			}
			this.originalSecurityContext = null;
		}
	}

	/**
	 * Sets the {@link SecurityContextHolderStrategy} to use. The default action is to use
	 * the {@link SecurityContextHolderStrategy} stored in {@link SecurityContextHolder}.
	 *
	 * @since 5.8
	 */
	public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
		Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
		this.securityContextHolderStrategy = securityContextHolderStrategy;
		if (!this.explicitSecurityContextProvided) {
			this.delegateSecurityContext = this.securityContextHolderStrategy.getContext();
		}
	}

	@Override
	public String toString() {
		return this.delegate.toString();
	}

	/**
	 * Factory method for creating a {@link DelegatingSecurityContextRunnable}.
	 * @param delegate the original {@link Runnable} that will be delegated to after
	 * establishing a {@link SecurityContext} on the {@link SecurityContextHolder}. Cannot
	 * have null.
	 * @param securityContext the {@link SecurityContext} to establish before invoking the
	 * delegate {@link Runnable}. If null, the current {@link SecurityContext} from the
	 * {@link SecurityContextHolder} will be used.
	 * @return
	 */
	public static Runnable create(Runnable delegate, @Nullable SecurityContext securityContext) {
		Assert.notNull(delegate, "delegate cannot be  null");
		return (securityContext != null) ? new DelegatingSecurityContextRunnable(delegate, securityContext)
				: new DelegatingSecurityContextRunnable(delegate);
	}

	static Runnable create(Runnable delegate, @Nullable SecurityContext securityContext,
			SecurityContextHolderStrategy securityContextHolderStrategy) {
		Assert.notNull(delegate, "delegate cannot be  null");
		Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
		DelegatingSecurityContextRunnable runnable = (securityContext != null)
				? new DelegatingSecurityContextRunnable(delegate, securityContext)
				: new DelegatingSecurityContextRunnable(delegate);
		runnable.setSecurityContextHolderStrategy(securityContextHolderStrategy);
		return runnable;
	}

}
