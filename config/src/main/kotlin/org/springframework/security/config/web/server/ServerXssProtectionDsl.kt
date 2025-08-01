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

package org.springframework.security.config.web.server

import org.springframework.security.web.server.header.XXssProtectionServerHttpHeadersWriter.HeaderValue

/**
 * A Kotlin DSL to configure the [ServerHttpSecurity] XSS protection header using
 * idiomatic Kotlin code.
 *
 * @property headerValue the value of the X-XSS-Protection header. OWASP recommends [HeaderValue.DISABLED].
 *
 * @author Eleftheria Stein
 * @since 5.4
 */
@ServerSecurityMarker
class ServerXssProtectionDsl {
    private var disabled = false
    var headerValue: HeaderValue? = null

    /**
     * Disables cache control response headers
     */
    fun disable() {
        disabled = true
    }

    internal fun get(): (ServerHttpSecurity.HeaderSpec.XssProtectionSpec) -> Unit {
        return { xss ->
            headerValue?.also { xss.headerValue(headerValue) }
            if (disabled) {
                xss.disable()
            }
        }
    }
}
