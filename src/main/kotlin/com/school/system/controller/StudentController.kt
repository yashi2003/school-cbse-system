package com.school.system.controller

import com.school.system.model.Student
import com.school.system.service.StudentService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/students")
class StudentController(
    private val studentService: StudentService
) {
    private val logger = LoggerFactory.getLogger(StudentController::class.java)

    @PostMapping
    fun createStudent(@RequestBody student: Student): Mono<ResponseEntity<Student>> {
        logger.info("Received request to create student with Aadhaar: ${student.aadhaar}, RollNo: ${student.rollNo}")
        return studentService.createStudent(student)
            .map { createdStudent ->
                logger.info("Successfully created student with Aadhaar: ${createdStudent.aadhaar}, RollNo: ${createdStudent.rollNo}")
                ResponseEntity.ok(createdStudent)
            }
            .doOnError { error ->
                logger.error("Failed to create student with Aadhaar: ${student.aadhaar}", error)
            }
    }

//    @GetMapping("/{aadhaar}")
//    fun getStudent(@PathVariable aadhaar: String): Mono<ResponseEntity<Student>> {
//        logger.info("Received request to fetch student with Aadhaar: $aadhaar")
//        return studentService.getStudentByAadhaar(aadhaar)
//            .map { student ->
//                logger.info("Student found with Aadhaar: $aadhaar")
//                ResponseEntity.ok(student)
//            }
//            .defaultIfEmpty(ResponseEntity.notFound().build())
//            .doOnError { error ->
//                logger.error("Error fetching student with Aadhaar: $aadhaar", error)
//            }
//    }
}
