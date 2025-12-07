package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.student.CreateStudentRequest;
import at.hollndonner.studentordersapp.dto.student.StudentResponse;
import at.hollndonner.studentordersapp.model.Student;
import at.hollndonner.studentordersapp.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentResponse createStudent(CreateStudentRequest request) {
        Student student = Student.builder()
                .name(request.name())
                .grade(request.grade())
                .school(request.school())
                .build();

        Student saved = studentRepository.save(student);
        return StudentResponse.fromEntity(saved);
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentResponse::fromEntity)
                .toList();
    }
}

