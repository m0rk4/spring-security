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

package org.springframework.security.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.Nullable;

import org.springframework.core.SpringVersion;
import org.springframework.util.Assert;

/**
 * Internal class used for checking version compatibility in a deployed application.
 *
 * @author Luke Taylor
 * @author Rob Winch
 */
public final class SpringSecurityCoreVersion {

	private static final String DISABLE_CHECKS = SpringSecurityCoreVersion.class.getName().concat(".DISABLE_CHECKS");

	private static final Log logger = LogFactory.getLog(SpringSecurityCoreVersion.class);

	/**
	 * Global Serialization value for Spring Security classes.
	 */
	public static final long SERIAL_VERSION_UID = 620L;

	static final @Nullable String MIN_SPRING_VERSION = getSpringVersion();

	static {
		performVersionChecks();
	}

	private SpringSecurityCoreVersion() {
	}

	private static void performVersionChecks() {
		performVersionChecks(MIN_SPRING_VERSION);
	}

	/**
	 * Perform version checks with specific min Spring Version
	 * @param minSpringVersion
	 */
	private static void performVersionChecks(@Nullable String minSpringVersion) {
		if (minSpringVersion == null) {
			return;
		}
		// Check Spring Compatibility
		String springVersion = SpringVersion.getVersion();
		String version = getVersion();
		if (disableChecks(springVersion, version)) {
			return;
		}
		// should be disabled if springVersion is null
		Assert.notNull(springVersion, "springVersion cannot be null");
		logger.info("You are running with Spring Security Core " + version);
		if (new ComparableVersion(springVersion).compareTo(new ComparableVersion(minSpringVersion)) < 0) {
			logger.warn("**** You are advised to use Spring " + minSpringVersion
					+ " or later with this version. You are running: " + springVersion);
		}
	}

	public static @Nullable String getVersion() {
		Package pkg = SpringSecurityCoreVersion.class.getPackage();
		return (pkg != null) ? pkg.getImplementationVersion() : null;
	}

	/**
	 * Disable if springVersion and springSecurityVersion are the same to allow working
	 * with Uber Jars.
	 * @param springVersion
	 * @param springSecurityVersion
	 * @return
	 */
	private static boolean disableChecks(@Nullable String springVersion, @Nullable String springSecurityVersion) {
		if (springVersion == null || springVersion.equals(springSecurityVersion)) {
			return true;
		}
		return Boolean.getBoolean(DISABLE_CHECKS);
	}

	/**
	 * Loads the spring version or null if it cannot be found.
	 * @return
	 */
	private static @Nullable String getSpringVersion() {
		Properties properties = new Properties();
		try (InputStream is = SpringSecurityCoreVersion.class.getClassLoader()
			.getResourceAsStream("META-INF/spring-security.versions")) {
			properties.load(is);
		}
		catch (IOException | NullPointerException ex) {
			return null;
		}
		return properties.getProperty("org.springframework:spring-core");
	}

}
