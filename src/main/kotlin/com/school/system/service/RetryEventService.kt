package com.school.system.service

import com.school.system.dto.StudentOnboardingEvent
import com.school.system.model.RetryEvent
import com.school.system.model.enums.RetryStatus
import com.school.system.repository.RetryEventRepository
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime

/**
 * Service to process student onboarding events and persist retry metadata.
 */
@Service
class RetryEventService(
    private val webClient: WebClient,
    private val retryEventRepository: RetryEventRepository
) {

    /**
     * Processes a StudentOnboardingEvent by posting it to the CBSE onboarding endpoint,
     * and records a RetryEvent based on the response status.
     *
     * @param event The incoming student onboarding event.
     * @return A Mono that completes after saving the retry event.
     */
    fun processStudentOnboarding(event: StudentOnboardingEvent): Mono<Void> {
        val requestPayload = mapOf(
            "aadhaar" to event.aadhaar,
            "rollNo" to event.rollNo,
            "name" to event.name,
            "studentClass" to event.studentClass,
            "school" to event.school,
            "dob" to event.dob.toString() // ISO 8601: yyyy-MM-dd
        )

        return webClient.post()
            .uri("/enroll")
            .bodyValue(requestPayload)
            .retrieve()
            .toBodilessEntity()
            .map { response ->
                val status = when (response.statusCode.value()) {
                    200 -> RetryStatus.CLOSED
                    409 -> RetryStatus.FAILED
                    else -> RetryStatus.OPEN
                }

                RetryEvent(
                    studentRollNo = event.rollNo,
                    requestMetadata = requestPayload,
                    responseMetadata = mapOf("statusCode" to response.statusCode.value()),
                    createdDate = LocalDateTime.now(),
                    lastRunDate = LocalDateTime.now(),
                    nextRunTime = LocalDateTime.now().plusMinutes(5),
                    version = 0,
                    status = status
                )
            }
            .flatMap { retryEventRepository.save(it) }
            .then()
    }
}
