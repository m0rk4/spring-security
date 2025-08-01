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

package org.springframework.security.config.annotation.web

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer
import org.springframework.web.cors.CorsConfigurationSource

/**
 * A Kotlin DSL to configure [HttpSecurity] CORS using idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 * @since 5.3
 * @property configurationSource the [CorsConfigurationSource] to use.
 */
@SecurityMarker
class CorsDsl {
    var configurationSource: CorsConfigurationSource? = null

    private var disabled = false

    /**
     * Disable CORS.
     */
    fun disable() {
        disabled = true
    }

    internal fun get(): (CorsConfigurer<HttpSecurity>) -> Unit {
        return { cors ->
            configurationSource?.also { cors.configurationSource(configurationSource) }
            if (disabled) {
                cors.disable()
            }
        }
    }
}
