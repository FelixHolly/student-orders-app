package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.order.CreateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.OrderFilterRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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
        when(inputSanitizer.sanitizeText(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = orderService.createOrder(createRequest);

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
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(createRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Student not found");

        verify(studentRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WithInvalidStatus_ShouldThrowException() {
        CreateOrderRequest invalidRequest = new CreateOrderRequest(
                1L,
                new BigDecimal("50.00"),
                "invalid_status"
        );
        when(inputSanitizer.sanitizeText(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> orderService.createOrder(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid order status");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrderById_WithValidId_ShouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.total()).isEqualTo(new BigDecimal("50.00"));

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_WithInvalidId_ShouldThrowException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order not found");

        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getOrders_ShouldReturnPageOfOrders() {
        Order order2 = Order.builder()
                .id(2L)
                .student(student)
                .total(new BigDecimal("75.00"))
                .status(OrderStatus.paid)
                .build();

        List<Order> orders = Arrays.asList(order, order2);
        Page<Order> orderPage = new PageImpl<>(orders);
        Pageable pageable = PageRequest.of(0, 20);
        OrderFilterRequest filter = new OrderFilterRequest(null, null, null, null);

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(orderPage);

        Page<OrderResponse> responses = orderService.getOrders(filter, pageable);

        assertThat(responses.getContent()).hasSize(2);
        assertThat(responses.getContent().get(0).total()).isEqualTo(new BigDecimal("50.00"));
        assertThat(responses.getContent().get(1).total()).isEqualTo(new BigDecimal("75.00"));

        verify(orderRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getOrders_WhenEmpty_ShouldReturnEmptyPage() {
        Page<Order> emptyPage = new PageImpl<>(List.of());
        Pageable pageable = PageRequest.of(0, 20);
        OrderFilterRequest filter = new OrderFilterRequest(null, null, null, null);

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        Page<OrderResponse> responses = orderService.getOrders(filter, pageable);

        assertThat(responses.getContent()).isEmpty();
        verify(orderRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getOrders_WithStudentIdFilter_ShouldReturnFilteredOrders() {
        List<Order> orders = List.of(order);
        Page<Order> orderPage = new PageImpl<>(orders);
        Pageable pageable = PageRequest.of(0, 20);
        OrderFilterRequest filter = new OrderFilterRequest(1L, null, null, null);

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(orderPage);

        Page<OrderResponse> responses = orderService.getOrders(filter, pageable);

        assertThat(responses.getContent()).hasSize(1);
        assertThat(responses.getContent().get(0).studentId()).isEqualTo(1L);

        verify(orderRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getOrders_WithStatusFilter_ShouldReturnFilteredOrders() {
        List<Order> orders = List.of(order);
        Page<Order> orderPage = new PageImpl<>(orders);
        Pageable pageable = PageRequest.of(0, 20);
        OrderFilterRequest filter = new OrderFilterRequest(null, "pending", null, null);

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(orderPage);

        Page<OrderResponse> responses = orderService.getOrders(filter, pageable);

        assertThat(responses.getContent()).hasSize(1);
        assertThat(responses.getContent().get(0).status()).isEqualTo(OrderStatus.pending);

        verify(orderRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void deleteOrder_WithValidId_ShouldDeleteOrder() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteOrder_WithInvalidId_ShouldThrowException() {
        when(orderRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> orderService.deleteOrder(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order not found");

        verify(orderRepository, times(1)).existsById(999L);
        verify(orderRepository, never()).deleteById(anyLong());
    }
}
