package com.school.system.repository

import com.school.system.model.Student
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

/**
 * Reactive repository interface for Student documents.
 */
interface StudentRepository : ReactiveMongoRepository<Student, String> {

    /**
     * Finds a student by their roll number.
     * @param rollNo the student's roll number
     * @return a Mono emitting the found student or empty if none found
     */
    fun findByRollNo(rollNo: String): Mono<Student>


}
