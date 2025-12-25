package at.hollndonner.studentordersapp.controller;

import at.hollndonner.studentordersapp.dto.student.CreateStudentRequest;
import at.hollndonner.studentordersapp.dto.student.StudentResponse;
import at.hollndonner.studentordersapp.dto.student.UpdateStudentRequest;
import at.hollndonner.studentordersapp.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/students")
@CrossOrigin
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

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

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request) {
        log.info("Updating student with ID: {}", id);
        StudentResponse updated = studentService.updateStudent(id, request);
        log.info("Student updated successfully with ID: {}", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        log.info("Deleting student with ID: {}", id);
        studentService.deleteStudent(id);
        log.info("Student deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}

