package com.school.system.repository

import com.school.system.model.RetryConfig
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

/**
 * Reactive MongoDB repository for accessing retry configuration settings.
 */
interface RetryConfigRepository : ReactiveMongoRepository<RetryConfig, String> {

    /**
     * Retrieves the retry configuration for the given task type.
     *
     * @param taskType the identifier for the task (e.g., "CBSE_ONBOARDING")
     * @return a [Mono] emitting the [RetryConfig], or empty if not found
     */
    fun findByTaskType(taskType: String): Mono<RetryConfig>
}
