package com.school.system.service

import com.school.system.dto.StudentOnboardingEvent
import com.school.system.model.Student
import com.school.system.repository.StudentRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val kafkaTemplate: KafkaTemplate<String, StudentOnboardingEvent>
) {

    private val logger = LoggerFactory.getLogger(StudentService::class.java)
    private val topic = "student-onboarding"

    /**
     * Persists a student record and publishes an onboarding event to Kafka.
     */
    fun createStudent(student: Student): Mono<Student> {
        return studentRepository.save(student)
            .doOnSuccess {
                val event = it.toOnboardingEvent()
                kafkaTemplate.send(topic, event)
                logger.info("Student saved and onboarding event published for rollNo=${it.rollNo}, aadhaar=${it.aadhaar}")
            }
            .doOnError {
                logger.error("Failed to save student with aadhaar=${student.aadhaar}", it)
            }
    }

    /**
     * Retrieves student details using Aadhaar number.
     */
    fun getStudentByAadhaar(aadhaar: String): Mono<Student> {
        return studentRepository.findById(aadhaar)
            .doOnSuccess {
                logger.info("Fetched student data for aadhaar=$aadhaar")
            }
            .doOnError {
                logger.error("Error fetching student with aadhaar=$aadhaar", it)
            }
    }

    /**
     * Extension function to convert Student to StudentOnboardingEvent.
     */
    private fun Student.toOnboardingEvent() = StudentOnboardingEvent(
        aadhaar = this.aadhaar,
        rollNo = this.rollNo,
        name = this.name,
        studentClass = this.studentClass,
        dob = this.dob,
        school = this.school
    )
}
