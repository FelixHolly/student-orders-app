package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.student.CreateStudentRequest;
import at.hollndonner.studentordersapp.dto.student.StudentFilterRequest;
import at.hollndonner.studentordersapp.dto.student.StudentResponse;
import at.hollndonner.studentordersapp.dto.student.UpdateStudentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {

    StudentResponse createStudent(CreateStudentRequest request);

    StudentResponse getStudentById(Long id);

    Page<StudentResponse> getStudents(StudentFilterRequest filter, Pageable pageable);

    StudentResponse updateStudent(Long id, UpdateStudentRequest request);

    void deleteStudent(Long id);
}

