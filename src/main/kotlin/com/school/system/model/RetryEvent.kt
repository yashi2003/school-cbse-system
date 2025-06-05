package com.school.system.model

import com.school.system.model.enums.RetryStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

/**
 * Represents a retryable event for processing operations like student onboarding.
 * Stored in the MongoDB collection "retry_events".
 */
@Document(collection = "retry_events")
data class RetryEvent(

    /**
     * Unique identifier for this retry event. Defaults to a randomly generated UUID.
     */
    @Id
    val retryId: UUID = UUID.randomUUID(),

    /**
     * Roll number of the student associated with this event.
     */
    val studentRollNo: String,

    /**
     * Type of the task associated with this event.
     * Defaults to "CBSE_ONBOARDING".
     */
    val taskType: String = "CBSE_ONBOARDING",

    /**
     * Metadata related to the original request. Often includes fields like Aadhaar, name, etc.
     */
    val requestMetadata: Map<String, Any>,

    /**
     * Metadata returned by the external system/API in response.
     */
    var responseMetadata: Map<String, Any>? = null,

    /**
     * Timestamp of when the event was first created.
     */
    val createdDate: LocalDateTime = LocalDateTime.now(),

    /**
     * Timestamp of the most recent retry attempt.
     */
    var lastRunDate: LocalDateTime? = null,

    /**
     * Timestamp when the next retry attempt is scheduled.
     */
    var nextRunTime: LocalDateTime? = null,

    /**
     * Version counter for optimistic locking or retry tracking.
     */
    var version: Int = 0,

    /**
     * Current status of the retry event (e.g., OPEN, CLOSED, FAILED).
     */
    var status: RetryStatus
)
