package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.order.CreateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.OrderFilterRequest;
import at.hollndonner.studentordersapp.dto.order.OrderResponse;
import at.hollndonner.studentordersapp.dto.order.UpdateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.UpdateOrderStatusRequest;
import at.hollndonner.studentordersapp.exception.ResourceNotFoundException;
import at.hollndonner.studentordersapp.model.Order;
import at.hollndonner.studentordersapp.model.OrderStatus;
import at.hollndonner.studentordersapp.model.Student;
import at.hollndonner.studentordersapp.repository.OrderRepository;
import at.hollndonner.studentordersapp.repository.StudentRepository;
import at.hollndonner.studentordersapp.repository.specification.OrderSpecification;
import at.hollndonner.studentordersapp.util.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

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
    public OrderResponse getOrderById(Long id) {
        log.debug("Fetching order with ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {}", id);
                    return new ResourceNotFoundException("Order not found");
                });
        return OrderResponse.fromEntity(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(OrderFilterRequest filter, Pageable pageable) {
        log.debug("Fetching orders with filter: {}", filter);

        OrderStatus status = null;
        if (filter.status() != null && !filter.status().isBlank()) {
            status = parseStatus(filter.status());
        }

        Specification<Order> spec = Specification.where(OrderSpecification.hasStudentId(filter.studentId()))
                .and(OrderSpecification.hasStatus(status))
                .and(OrderSpecification.hasMinTotal(filter.minTotal()))
                .and(OrderSpecification.hasMaxTotal(filter.maxTotal()));

        Page<OrderResponse> orders = orderRepository.findAll(spec, pageable)
                .map(OrderResponse::fromEntity);
        log.debug("Found {} orders", orders.getTotalElements());
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
