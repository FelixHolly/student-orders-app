package at.hollndonner.studentordersapp.service;


import at.hollndonner.studentordersapp.dto.order.CreateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.OrderResponse;
import at.hollndonner.studentordersapp.dto.order.UpdateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.UpdateOrderStatusRequest;
import at.hollndonner.studentordersapp.exception.ResourceNotFoundException;
import at.hollndonner.studentordersapp.model.Order;
import at.hollndonner.studentordersapp.model.OrderStatus;
import at.hollndonner.studentordersapp.model.Student;
import at.hollndonner.studentordersapp.repository.OrderRepository;
import at.hollndonner.studentordersapp.repository.StudentRepository;
import at.hollndonner.studentordersapp.util.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final StudentRepository studentRepository;
    private final InputSanitizer inputSanitizer;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.debug("Creating order for student ID: {}", request.studentId());
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> {
                    log.error("Student not found with ID: {}", request.studentId());
                    return new ResourceNotFoundException("Student not found");
                });

        // Sanitize status input to prevent XSS attacks
        String sanitizedStatus = inputSanitizer.sanitizeText(request.status());
        OrderStatus status = parseStatus(sanitizedStatus);

        Order order = Order.builder()
                .student(student)
                .total(request.total())
                .createdAt(Instant.now())
                .status(status)
                .build();

        Order saved = orderRepository.save(order);
        log.debug("Order saved with ID: {} for student ID: {}", saved.getId(), student.getId());
        return OrderResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForStudent(Long studentId) {
        log.debug("Fetching orders for student ID: {}", studentId);
        // verify student exists (optional but nicer for client)
        if (!studentRepository.existsById(studentId)) {
            log.error("Student not found with ID: {}", studentId);
            throw new ResourceNotFoundException("Student not found");
        }

        List<OrderResponse> orders = orderRepository.findByStudentId(studentId)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
        log.debug("Found {} orders for student ID: {}", orders.size(), studentId);
        return orders;
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(Long id, UpdateOrderRequest request) {
        log.debug("Updating order with ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {}", id);
                    return new ResourceNotFoundException("Order not found");
                });

        String sanitizedStatus = inputSanitizer.sanitizeText(request.status());
        OrderStatus newStatus = parseStatus(sanitizedStatus);

        order.setTotal(request.total());
        order.setStatus(newStatus);
        order.setCreatedAt(request.createdAt());

        Order updated = orderRepository.save(order);
        log.debug("Order updated with ID: {}", id);
        return OrderResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        log.debug("Updating status for order ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {}", id);
                    return new ResourceNotFoundException("Order not found");
                });

        String sanitizedStatus = inputSanitizer.sanitizeText(request.status());
        OrderStatus newStatus = parseStatus(sanitizedStatus);

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        log.debug("Order status updated to {} for order ID: {}", newStatus, id);
        return OrderResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        log.debug("Deleting order with ID: {}", id);
        if (!orderRepository.existsById(id)) {
            log.error("Order not found with ID: {}", id);
            throw new ResourceNotFoundException("Order not found");
        }
        orderRepository.deleteById(id);
        log.debug("Order deleted with ID: {}", id);
    }

    private OrderStatus parseStatus(String rawStatus) {
        try {
            return OrderStatus.valueOf(rawStatus);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid order status provided: {}", rawStatus);
            throw new IllegalArgumentException("Invalid order status. Allowed: pending, paid.");
        }
    }
}


