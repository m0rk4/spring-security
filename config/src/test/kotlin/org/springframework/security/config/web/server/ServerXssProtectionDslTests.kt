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

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.test.SpringTestContext
import org.springframework.security.config.test.SpringTestContextExtension
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.header.XXssProtectionServerHttpHeadersWriter
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.config.EnableWebFlux

/**
 * Tests for [ServerXssProtectionDsl]
 *
 * @author Eleftheria Stein
 */
@ExtendWith(SpringTestContextExtension::class)
class ServerXssProtectionDslTests {
    @JvmField
    val spring = SpringTestContext(this)

    private lateinit var client: WebTestClient

    @Autowired
    fun setup(context: ApplicationContext) {
        this.client = WebTestClient
                .bindToApplicationContext(context)
                .configureClient()
                .build()
    }

    @Test
    fun `request when xss protection configured then xss header in response`() {
        this.spring.register(XssConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .exchange()
                .expectHeader().valueEquals(XXssProtectionServerHttpHeadersWriter.X_XSS_PROTECTION, "0")
    }

    @Configuration
    @EnableWebFluxSecurity
    @EnableWebFlux
    open class XssConfig {
        @Bean
        open fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                headers {
                    xssProtection { }
                }
            }
        }
    }

    @Test
    fun `request when xss protection disabled then no xss header in response`() {
        this.spring.register(XssDisabledConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .exchange()
                .expectHeader().doesNotExist(XXssProtectionServerHttpHeadersWriter.X_XSS_PROTECTION)
    }

    @Configuration
    @EnableWebFluxSecurity
    @EnableWebFlux
    open class XssDisabledConfig {
        @Bean
        open fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                headers {
                    xssProtection {
                        disable()
                    }
                }
            }
        }
    }

    @Test
    fun `request when xss protection value disabled then xss header in response`() {
        this.spring.register(XssValueDisabledConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .exchange()
                .expectHeader().valueEquals(XXssProtectionServerHttpHeadersWriter.X_XSS_PROTECTION, "1")
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    open class XssValueDisabledConfig {
        @Bean
        open fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                headers {
                    xssProtection {
                        headerValue = XXssProtectionServerHttpHeadersWriter.HeaderValue.ENABLED
                    }
                }
            }
        }
    }
}
