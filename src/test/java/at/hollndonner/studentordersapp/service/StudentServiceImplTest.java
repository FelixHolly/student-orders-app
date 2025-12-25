package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.student.CreateStudentRequest;
import at.hollndonner.studentordersapp.dto.student.StudentFilterRequest;
import at.hollndonner.studentordersapp.dto.student.StudentResponse;
import at.hollndonner.studentordersapp.exception.ResourceNotFoundException;
import at.hollndonner.studentordersapp.model.Student;
import at.hollndonner.studentordersapp.repository.StudentRepository;
import at.hollndonner.studentordersapp.util.InputSanitizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        when(inputSanitizer.sanitizeText(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponse response = studentService.createStudent(createRequest);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("John Doe");
        assertThat(response.grade()).isEqualTo("10th Grade");
        assertThat(response.school()).isEqualTo("Test High School");

        verify(inputSanitizer, times(3)).sanitizeText(anyString());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void getStudentById_WithValidId_ShouldReturnStudent() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentResponse response = studentService.getStudentById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("John Doe");

        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void getStudentById_WithInvalidId_ShouldThrowException() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Student not found");

        verify(studentRepository, times(1)).findById(999L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getStudents_ShouldReturnPageOfStudents() {
        Student student2 = Student.builder()
                .id(2L)
                .name("Jane Smith")
                .grade("11th Grade")
                .school("Another School")
                .build();

        List<Student> students = Arrays.asList(student, student2);
        Page<Student> studentPage = new PageImpl<>(students);
        Pageable pageable = PageRequest.of(0, 20);
        StudentFilterRequest filter = new StudentFilterRequest(null, null, null);

        when(studentRepository.findAll(any(Example.class), eq(pageable))).thenReturn(studentPage);

        Page<StudentResponse> responses = studentService.getStudents(filter, pageable);

        assertThat(responses.getContent()).hasSize(2);
        assertThat(responses.getContent().get(0).name()).isEqualTo("John Doe");
        assertThat(responses.getContent().get(1).name()).isEqualTo("Jane Smith");

        verify(studentRepository, times(1)).findAll(any(Example.class), eq(pageable));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getStudents_WhenEmpty_ShouldReturnEmptyPage() {
        Page<Student> emptyPage = new PageImpl<>(List.of());
        Pageable pageable = PageRequest.of(0, 20);
        StudentFilterRequest filter = new StudentFilterRequest(null, null, null);

        when(studentRepository.findAll(any(Example.class), eq(pageable))).thenReturn(emptyPage);

        Page<StudentResponse> responses = studentService.getStudents(filter, pageable);

        assertThat(responses.getContent()).isEmpty();
        verify(studentRepository, times(1)).findAll(any(Example.class), eq(pageable));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getStudents_WithFilter_ShouldReturnFilteredStudents() {
        List<Student> students = List.of(student);
        Page<Student> studentPage = new PageImpl<>(students);
        Pageable pageable = PageRequest.of(0, 20);
        StudentFilterRequest filter = new StudentFilterRequest("John", null, null);

        when(studentRepository.findAll(any(Example.class), eq(pageable))).thenReturn(studentPage);

        Page<StudentResponse> responses = studentService.getStudents(filter, pageable);

        assertThat(responses.getContent()).hasSize(1);
        assertThat(responses.getContent().get(0).name()).isEqualTo("John Doe");

        verify(studentRepository, times(1)).findAll(any(Example.class), eq(pageable));
    }

    @Test
    void deleteStudent_WithValidId_ShouldDeleteStudent() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        studentService.deleteStudent(1L);

        verify(studentRepository, times(1)).existsById(1L);
        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteStudent_WithInvalidId_ShouldThrowException() {
        when(studentRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> studentService.deleteStudent(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Student not found");

        verify(studentRepository, times(1)).existsById(999L);
        verify(studentRepository, never()).deleteById(anyLong());
    }
}
