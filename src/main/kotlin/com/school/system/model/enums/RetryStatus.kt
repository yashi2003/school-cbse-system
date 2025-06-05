package com.school.system.model.enums

/**
 * Represents the retry state of a given task or event.
 */
enum class RetryStatus {

    /** The task failed but is scheduled to retry. */
    OPEN,

    /** The task succeeded or is no longer retried. */
    CLOSED,

    /** The task failed and won't be retried automatically. */
    FAILED
}
