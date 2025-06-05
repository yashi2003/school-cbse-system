package com.school.system.dto

import java.time.LocalDate

/**
 * Data Transfer Object representing a student onboarding event
 * received via Kafka or API during the onboarding process.
 */

data class StudentOnboardingEvent(
    val aadhaar: String,
    val rollNo: String,
    val name: String,
    val studentClass: String,
    val dob: LocalDate,

    /**
     * Defaults to "ABC Public School" if not provided.
     */
    val school: String = "ABC Public School"
)