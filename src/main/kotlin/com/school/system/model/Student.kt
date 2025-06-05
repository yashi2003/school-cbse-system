package com.school.system.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

/**
 * Represents a student entity stored in the "students" collection in MongoDB.
 */

// Annotates this class to be stored as a MongoDB document in the "students" collection
@Document("students")
data class Student(
    // Marks this field as the primary key (_id) in the MongoDB document
    @Id
    val aadhaar: String,
    val rollNo: String,
    val name: String,
    val studentClass: String,
    val dob: LocalDate,
    val school: String = "ABC Public School"
)
