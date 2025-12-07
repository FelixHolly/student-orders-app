package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.student.CreateStudentRequest;
import at.hollndonner.studentordersapp.dto.student.StudentResponse;
import at.hollndonner.studentordersapp.model.Student;
import at.hollndonner.studentordersapp.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentResponse createStudent(CreateStudentRequest request) {
        log.debug("Creating student: {}", request.name());
        Student student = Student.builder()
                .name(request.name())
                .grade(request.grade())
                .school(request.school())
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

