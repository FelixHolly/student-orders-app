package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.student.CreateStudentRequest;
import at.hollndonner.studentordersapp.dto.student.StudentResponse;
import at.hollndonner.studentordersapp.dto.student.UpdateStudentRequest;
import at.hollndonner.studentordersapp.model.Student;
import at.hollndonner.studentordersapp.repository.StudentRepository;
import at.hollndonner.studentordersapp.util.InputSanitizer;
import at.hollndonner.studentordersapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final InputSanitizer inputSanitizer;

    @Override
    @Transactional
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
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        log.debug("Fetching all students from repository");
        List<StudentResponse> students = studentRepository.findAll()
                .stream()
                .map(StudentResponse::fromEntity)
                .toList();
        log.debug("Found {} students", students.size());
        return students;
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {
        log.debug("Updating student with ID: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Student not found with ID: {}", id);
                    return new ResourceNotFoundException("Student not found");
                });

        String sanitizedName = inputSanitizer.sanitizeText(request.name());
        String sanitizedGrade = inputSanitizer.sanitizeText(request.grade());
        String sanitizedSchool = inputSanitizer.sanitizeText(request.school());

        student.setName(sanitizedName);
        student.setGrade(sanitizedGrade);
        student.setSchool(sanitizedSchool);

        Student updated = studentRepository.save(student);
        log.debug("Student updated with ID: {}", id);
        return StudentResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        log.debug("Deleting student with ID: {}", id);
        if (!studentRepository.existsById(id)) {
            log.error("Student not found with ID: {}", id);
            throw new ResourceNotFoundException("Student not found");
        }
        studentRepository.deleteById(id);
        log.debug("Student deleted with ID: {}", id);
    }
}

