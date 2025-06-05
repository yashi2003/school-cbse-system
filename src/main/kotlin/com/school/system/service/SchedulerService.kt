package com.school.system.service

import com.school.system.model.enums.RetryStatus
import com.school.system.model.RetryEvent
import com.school.system.repository.RetryConfigRepository
import com.school.system.repository.RetryEventRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

/**
 * Periodically retries failed or pending student onboarding tasks
 * based on configuration in the retry config repository.
 */
@Service
class SchedulerService(
    private val retryEventRepository: RetryEventRepository,
    private val retryConfigRepository: RetryConfigRepository
) {
    private val logger = LoggerFactory.getLogger(SchedulerService::class.java)

    /**
     * Scheduled job that runs every 60 seconds to retry open tasks
     * whose nextRunTime is due.
     */
    @Scheduled(fixedRate = 60000)
    fun retryOpenTasks() {
        val now = LocalDateTime.now()
        logger.info("Retry scheduler triggered at $now")

        retryEventRepository.findByStatusAndNextRunTimeBefore(RetryStatus.OPEN, now)
            .doOnNext { event ->
                logger.info("Retrying student: ${event.studentRollNo}, version: ${event.version}")
            }
            .flatMap { event ->
                retryConfigRepository.findByTaskType(event.taskType)
                    .doOnNext { config ->
                        logger.info("Config for ${event.taskType} -> maxRetry=${config.maxRetryCount}, retryAfter=${config.retryAfterInMins} mins")
                    }
                    .flatMap { config ->
                        processRetry(event, config.maxRetryCount, config.retryAfterInMins)
                    }
            }
            .subscribe(
                { updated ->
                    logger.info("Successfully updated retry event for student: ${updated.studentRollNo}, status: ${updated.status}")
                },
                { error ->
                    logger.error("Error occurred during retry processing: ", error)
                }
            )
    }

    /**
     * Processes a retry for a given event based on its current version and config constraints.
     *
     * @param event The event to retry.
     * @param maxRetry The maximum retry count allowed.
     * @param retryAfterMins The delay before the next retry attempt.
     * @return The updated retry event or Mono.empty() if max retry is exceeded.
     */
    private fun processRetry(event: RetryEvent, maxRetry: Int, retryAfterMins: Int): Mono<RetryEvent> {
        if (event.version >= maxRetry) {
            logger.warn("Max retries exceeded for student: ${event.studentRollNo}")
            return Mono.empty()
        }

        val aadhaarLastDigit = event.requestMetadata["aadhaar"]?.toString()?.lastOrNull()
        val (httpStatus, message) = when (aadhaarLastDigit) {
            '0' -> HttpStatus.OK to "Student enrolled successfully"
            '1' -> HttpStatus.CONFLICT to "Student already enrolled"
            '2' -> HttpStatus.INTERNAL_SERVER_ERROR to "Internal server error"
            else -> HttpStatus.BAD_REQUEST to "Unhandled Aadhaar pattern"
        }

        val newStatus = when (httpStatus) {
            HttpStatus.OK -> RetryStatus.CLOSED
            HttpStatus.CONFLICT -> RetryStatus.FAILED
            HttpStatus.INTERNAL_SERVER_ERROR ->
                if (event.version + 1 >= maxRetry) RetryStatus.FAILED else RetryStatus.OPEN
            else -> RetryStatus.FAILED
        }

        logger.info(
            """
            Updating retry event:
            Student: ${event.studentRollNo}
            HTTP: ${httpStatus.value()} - $message
            New Status: $newStatus
            Retry Count: ${event.version + 1}/$maxRetry
            Next Retry: ${LocalDateTime.now().plusMinutes(retryAfterMins.toLong())}
            """.trimIndent()
        )

        val updatedEvent = event.copy(
            responseMetadata = mapOf("status" to httpStatus.value(), "message" to message),
            lastRunDate = LocalDateTime.now(),
            nextRunTime = LocalDateTime.now().plusMinutes(retryAfterMins.toLong()),
            status = newStatus,
            version = event.version + 1
        )

        return retryEventRepository.save(updatedEvent)
    }
}
