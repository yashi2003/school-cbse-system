package com.school.system.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    private val logger = LoggerFactory.getLogger(WebClientConfig::class.java)

    @Bean
    fun webClient(): WebClient {
        val baseUrl = "http://localhost:8080" // CBSE Mock API URL
        logger.info("Initializing WebClient with base URL: $baseUrl")
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build()
    }
}

