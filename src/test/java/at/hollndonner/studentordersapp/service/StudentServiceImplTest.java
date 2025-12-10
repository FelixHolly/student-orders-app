package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.student.CreateStudentRequest;
import at.hollndonner.studentordersapp.dto.student.StudentResponse;
import at.hollndonner.studentordersapp.model.Student;
import at.hollndonner.studentordersapp.repository.StudentRepository;
import at.hollndonner.studentordersapp.util.InputSanitizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private InputSanitizer inputSanitizer;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private CreateStudentRequest createRequest;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L)
                .name("John Doe")
                .grade("10th Grade")
                .school("Test High School")
                .build();

        createRequest = new CreateStudentRequest(
                "John Doe",
                "10th Grade",
                "Test High School"
        );
    }

    @Test
    void createStudent_ShouldReturnStudentResponse() {
        // Given
        when(inputSanitizer.sanitizeText(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // When
        StudentResponse response = studentService.createStudent(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("John Doe");
        assertThat(response.grade()).isEqualTo("10th Grade");
        assertThat(response.school()).isEqualTo("Test High School");

        verify(inputSanitizer, times(3)).sanitizeText(anyString());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void getAllStudents_ShouldReturnListOfStudents() {
        // Given
        Student student2 = Student.builder()
                .id(2L)
                .name("Jane Smith")
                .grade("11th Grade")
                .school("Another School")
                .build();

        when(studentRepository.findAll()).thenReturn(Arrays.asList(student, student2));

        // When
        List<StudentResponse> responses = studentService.getAllStudents();

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("John Doe");
        assertThat(responses.get(1).name()).isEqualTo("Jane Smith");

        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void getAllStudents_WhenEmpty_ShouldReturnEmptyList() {
        // Given
        when(studentRepository.findAll()).thenReturn(List.of());

        // When
        List<StudentResponse> responses = studentService.getAllStudents();

        // Then
        assertThat(responses).isEmpty();
        verify(studentRepository, times(1)).findAll();
    }
}
