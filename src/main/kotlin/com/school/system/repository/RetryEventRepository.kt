package com.school.system.repository

import com.school.system.model.RetryEvent
import com.school.system.model.enums.RetryStatus
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import java.time.LocalDateTime
import java.util.*

/**
 * Reactive MongoDB repository for accessing and managing retryable events.
 */
interface RetryEventRepository : ReactiveMongoRepository<RetryEvent, UUID> {

    /**
     * Finds all retry events with the specified [status] and a [nextRunTime]
     * earlier than the given [time]. This is useful for identifying tasks due for retry.
     *
     * @param status the retry status to filter by (e.g., OPEN)
     * @param time the upper bound for the next run time
     * @return a [Flux] emitting matching [RetryEvent]s
     */
    fun findByStatusAndNextRunTimeBefore(
        status: RetryStatus,
        time: LocalDateTime
    ): Flux<RetryEvent>
}
