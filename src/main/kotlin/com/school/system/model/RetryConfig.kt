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
    val taskType: String,
    val maxRetryCount: Int,
    val retryAfterInMins: Int
)
