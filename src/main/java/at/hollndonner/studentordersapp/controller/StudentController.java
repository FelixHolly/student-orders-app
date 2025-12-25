package at.hollndonner.studentordersapp.controller;

import at.hollndonner.studentordersapp.dto.student.CreateStudentRequest;
import at.hollndonner.studentordersapp.dto.student.StudentFilterRequest;
import at.hollndonner.studentordersapp.dto.student.StudentResponse;
import at.hollndonner.studentordersapp.dto.student.UpdateStudentRequest;
import at.hollndonner.studentordersapp.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/students")
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
                .created(URI.create("/api/v1/students/" + created.id()))
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        log.info("Fetching student with ID: {}", id);
        StudentResponse student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping
    public ResponseEntity<Page<StudentResponse>> getStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String school,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Fetching students with filters - name: {}, grade: {}, school: {}", name, grade, school);
        StudentFilterRequest filter = new StudentFilterRequest(name, grade, school);
        Page<StudentResponse> students = studentService.getStudents(filter, pageable);
        log.info("Retrieved {} students (page {} of {})",
                students.getNumberOfElements(),
                students.getNumber() + 1,
                students.getTotalPages());
        return ResponseEntity.ok(students);
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
