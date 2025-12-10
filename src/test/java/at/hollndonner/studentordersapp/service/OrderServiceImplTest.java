package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.order.CreateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.OrderResponse;
import at.hollndonner.studentordersapp.exception.ResourceNotFoundException;
import at.hollndonner.studentordersapp.model.Order;
import at.hollndonner.studentordersapp.model.OrderStatus;
import at.hollndonner.studentordersapp.model.Student;
import at.hollndonner.studentordersapp.repository.OrderRepository;
import at.hollndonner.studentordersapp.repository.StudentRepository;
import at.hollndonner.studentordersapp.util.InputSanitizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private InputSanitizer inputSanitizer;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Student student;
    private Order order;
    private CreateOrderRequest createRequest;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L)
                .name("John Doe")
                .grade("10th Grade")
                .school("Test High School")
                .build();

        order = Order.builder()
                .id(1L)
                .student(student)
                .total(new BigDecimal("50.00"))
                .status(OrderStatus.pending)
                .build();

        createRequest = new CreateOrderRequest(
                1L,
                new BigDecimal("50.00"),
                "pending"
        );
    }

    @Test
    void createOrder_WithValidData_ShouldReturnOrderResponse() {
        // Given
        when(inputSanitizer.sanitizeText(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        OrderResponse response = orderService.createOrder(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.studentId()).isEqualTo(1L);
        assertThat(response.total()).isEqualTo(new BigDecimal("50.00"));
        assertThat(response.status()).isEqualTo(OrderStatus.pending);

        verify(inputSanitizer, times(1)).sanitizeText(anyString());
        verify(studentRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_WithNonExistentStudent_ShouldThrowException() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(createRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Student not found");

        verify(studentRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WithInvalidStatus_ShouldThrowException() {
        // Given
        CreateOrderRequest invalidRequest = new CreateOrderRequest(
                1L,
                new BigDecimal("50.00"),
                "invalid_status"
        );
        when(inputSanitizer.sanitizeText(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid order status");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrdersForStudent_WithValidStudentId_ShouldReturnOrders() {
        // Given
        Order order2 = Order.builder()
                .id(2L)
                .student(student)
                .total(new BigDecimal("75.00"))
                .status(OrderStatus.paid)
                .build();

        when(studentRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findByStudentId(1L)).thenReturn(Arrays.asList(order, order2));

        // When
        List<OrderResponse> responses = orderService.getOrdersForStudent(1L);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).total()).isEqualTo(new BigDecimal("50.00"));
        assertThat(responses.get(1).total()).isEqualTo(new BigDecimal("75.00"));

        verify(studentRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).findByStudentId(1L);
    }

    @Test
    void getOrdersForStudent_WithNonExistentStudent_ShouldThrowException() {
        // Given
        when(studentRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> orderService.getOrdersForStudent(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Student not found");

        verify(studentRepository, times(1)).existsById(999L);
        verify(orderRepository, never()).findByStudentId(anyLong());
    }

    @Test
    void getOrdersForStudent_WhenNoOrders_ShouldReturnEmptyList() {
        // Given
        when(studentRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findByStudentId(1L)).thenReturn(List.of());

        // When
        List<OrderResponse> responses = orderService.getOrdersForStudent(1L);

        // Then
        assertThat(responses).isEmpty();
        verify(orderRepository, times(1)).findByStudentId(1L);
    }
}
