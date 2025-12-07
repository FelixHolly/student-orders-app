package at.hollndonner.studentordersapp.service;


import at.hollndonner.studentordersapp.dto.order.CreateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.OrderResponse;
import at.hollndonner.studentordersapp.exception.ResourceNotFoundException;
import at.hollndonner.studentordersapp.model.Order;
import at.hollndonner.studentordersapp.model.OrderStatus;
import at.hollndonner.studentordersapp.model.Student;
import at.hollndonner.studentordersapp.repository.OrderRepository;
import at.hollndonner.studentordersapp.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final StudentRepository studentRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            StudentRepository studentRepository) {
        this.orderRepository = orderRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        OrderStatus status = parseStatus(request.status());

        Order order = Order.builder()
                .student(student)
                .total(request.total())
                .status(status)
                .build();

        Order saved = orderRepository.save(order);
        return OrderResponse.fromEntity(saved);
    }

    @Override
    public java.util.List<OrderResponse> getOrdersForStudent(Long studentId) {
        // verify student exists (optional but nicer for client)
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found");
        }

        return orderRepository.findByStudentId(studentId)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    private OrderStatus parseStatus(String rawStatus) {
        try {
            return OrderStatus.valueOf(rawStatus);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid order status. Allowed: pending, paid.");
        }
    }
}


