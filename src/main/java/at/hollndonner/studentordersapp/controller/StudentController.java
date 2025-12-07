package at.hollndonner.studentordersapp.controller;

import at.hollndonner.studentordersapp.dto.student.CreateStudentRequest;
import at.hollndonner.studentordersapp.dto.student.StudentResponse;
import at.hollndonner.studentordersapp.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
        StudentResponse created = studentService.createStudent(request);
        return ResponseEntity
                .created(URI.create("/students/" + created.id()))
                .body(created);
    }

    @GetMapping
    public List<StudentResponse> getStudents() {
        return studentService.getAllStudents();
    }
}

