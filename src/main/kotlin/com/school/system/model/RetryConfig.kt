package com.school.system.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Represents retry configuration for a specific task type.
 * Stored in the MongoDB collection "retryConfiguration".
 */
@Document(collection = "retryConfiguration")
data class RetryConfig(

    /**
     * MongoDB document ID. Automatically generated if null.
     */
    @Id
    val id: String? = null,

    /**
     * Logical identifier for the task type, e.g., "CBSE_ONBOARDING".
     */
    val taskType: String,

    /**
     * Maximum number of retry attempts allowed.
     */
    val maxRetryCount: Int,

    /**
     * Interval (in minutes) to wait between retries.
     */
    val retryAfterInMins: Int
)
