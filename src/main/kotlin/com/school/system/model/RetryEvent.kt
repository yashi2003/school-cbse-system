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
    val aadhar: String,
    val taskType: String = "CBSE_ONBOARDING",
    val requestMetadata: Map<String, Any>,
    var responseMetadata: Map<String, Any>? = null,
    val createdDate: LocalDateTime = LocalDateTime.now(),
    var lastRunDate: LocalDateTime? = null,
    var nextRunTime: LocalDateTime? = null,
    var version: Int = 0,
    var status: RetryStatus
)
