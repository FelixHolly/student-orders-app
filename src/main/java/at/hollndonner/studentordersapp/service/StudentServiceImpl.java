package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.student.CreateStudentRequest;
import at.hollndonner.studentordersapp.dto.student.StudentResponse;
import at.hollndonner.studentordersapp.model.Student;
import at.hollndonner.studentordersapp.repository.StudentRepository;
import at.hollndonner.studentordersapp.util.InputSanitizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final InputSanitizer inputSanitizer;

    public StudentServiceImpl(StudentRepository studentRepository, InputSanitizer inputSanitizer) {
        this.studentRepository = studentRepository;
        this.inputSanitizer = inputSanitizer;
    }

    @Override
    public StudentResponse createStudent(CreateStudentRequest request) {
        log.debug("Creating student: {}", request.name());

        // Sanitize input to prevent XSS attacks
        String sanitizedName = inputSanitizer.sanitizeText(request.name());
        String sanitizedGrade = inputSanitizer.sanitizeText(request.grade());
        String sanitizedSchool = inputSanitizer.sanitizeText(request.school());

        Student student = Student.builder()
                .name(sanitizedName)
                .grade(sanitizedGrade)
                .school(sanitizedSchool)
                .build();

        Student saved = studentRepository.save(student);
        log.debug("Student saved with ID: {}", saved.getId());
        return StudentResponse.fromEntity(saved);
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        log.debug("Fetching all students from repository");
        List<StudentResponse> students = studentRepository.findAll()
                .stream()
                .map(StudentResponse::fromEntity)
                .toList();
        log.debug("Found {} students", students.size());
        return students;
    }
}

