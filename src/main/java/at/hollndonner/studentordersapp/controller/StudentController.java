package at.hollndonner.studentordersapp.controller;

import at.hollndonner.studentordersapp.dto.student.CreateStudentRequest;
import at.hollndonner.studentordersapp.dto.student.StudentResponse;
import at.hollndonner.studentordersapp.service.StudentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/students")
@CrossOrigin // in README mention this is dev-only
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        log.info("Creating student with name: {}", request.name());
        StudentResponse created = studentService.createStudent(request);
        log.info("Student created successfully with ID: {}", created.id());
        return ResponseEntity
                .created(URI.create("/students/" + created.id()))
                .body(created);
    }

    @GetMapping
    public List<StudentResponse> getStudents() {
        log.info("Fetching all students");
        List<StudentResponse> students = studentService.getAllStudents();
        log.info("Retrieved {} students", students.size());
        return students;
    }
}

