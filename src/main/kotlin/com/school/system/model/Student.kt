package com.school.system.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("students")
data class Student(
    @Id
    val aadhaar: String,
    val rollNo: String,
    val name: String,
    val studentClass: String,
    val dob: LocalDate,
    val school: String = "ABC Public School"
)
