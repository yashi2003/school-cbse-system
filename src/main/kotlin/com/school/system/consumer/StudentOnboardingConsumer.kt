package com.school.system.consumer

import com.school.system.dto.StudentOnboardingEvent
import com.school.system.model.RetryEvent
import com.school.system.model.enums.RetryStatus
import com.school.system.repository.RetryEventRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class StudentOnboardingConsumer(
    private val retryEventRepository: RetryEventRepository
) {
    private val logger = LoggerFactory.getLogger(StudentOnboardingConsumer::class.java)

    @KafkaListener(
        topics = ["student-onboarding"],
        groupId = "school-service",
        containerFactory = "kafkaListenerFactory"
    )
    fun listen(event: StudentOnboardingEvent) {
        try {
            logger.info("Consumed Kafka event for student rollNo='${event.rollNo}', Aadhaar ending with '${event.aadhaar.last()}'")

            val (httpStatus, responseMessage) = determineResponse(event.aadhaar.last())

            val retryStatus = mapHttpStatusToRetryStatus(httpStatus)

            val retryEvent = buildRetryEvent(event, httpStatus, responseMessage, retryStatus)

            retryEventRepository.save(retryEvent)
                .doOnSuccess {
                    logger.info("RetryEvent saved successfully with status='$retryStatus' and HTTP status=${httpStatus.value()}")
                }
                .doOnError { error ->
                    logger.error("Failed to save RetryEvent for student rollNo='${event.rollNo}':", error)
                }
                .subscribe()
        } catch (ex: Exception) {
            logger.error("Error processing Kafka event for student rollNo='${event.rollNo}':", ex)
        }
    }

    private fun determineResponse(aadhaarLastDigit: Char): Pair<HttpStatus, String> = when (aadhaarLastDigit) {
        '0' -> HttpStatus.OK to "Student enrolled successfully"
        '1' -> HttpStatus.CONFLICT to "Student already enrolled"
        '2' -> HttpStatus.INTERNAL_SERVER_ERROR to "Internal server error"
        else -> HttpStatus.BAD_REQUEST to "Unhandled Aadhaar pattern"
    }

    private fun mapHttpStatusToRetryStatus(httpStatus: HttpStatus): RetryStatus = when (httpStatus) {
        HttpStatus.OK -> RetryStatus.CLOSED
        HttpStatus.CONFLICT -> RetryStatus.FAILED
        HttpStatus.INTERNAL_SERVER_ERROR -> RetryStatus.OPEN
        else -> RetryStatus.FAILED
    }

    private fun buildRetryEvent(
        event: StudentOnboardingEvent,
        httpStatus: HttpStatus,
        responseMessage: String,
        retryStatus: RetryStatus
    ): RetryEvent {
        val now = LocalDateTime.now()

        return RetryEvent(
            aadhar = event.aadhaar,
            taskType = "CBSE_ONBOARDING",
            requestMetadata = mapOf(
                "aadhaar" to event.aadhaar,
                "name" to event.name,
                "rollNo" to event.rollNo,
                "studentClass" to event.studentClass,
                "school" to event.school,
                "dob" to event.dob.toString()
            ),
            responseMetadata = mapOf(
                "status" to httpStatus.value(),
                "message" to responseMessage
            ),
            createdDate = now,
            lastRunDate = now,
            nextRunTime = now.plusMinutes(5),
            version = 0,
            status = retryStatus
        )
    }
}
