package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.student.CreateStudentRequest;
import at.hollndonner.studentordersapp.dto.student.StudentResponse;

import java.util.List;

public interface StudentService {

    StudentResponse createStudent(CreateStudentRequest request);

    List<StudentResponse> getAllStudents();

    void deleteStudent(Long id);
}

