package at.hollndonner.studentordersapp.controller;

import at.hollndonner.studentordersapp.dto.order.CreateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.OrderResponse;
import at.hollndonner.studentordersapp.dto.order.UpdateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.UpdateOrderStatusRequest;
import at.hollndonner.studentordersapp.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
@CrossOrigin
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Creating order for student ID: {} with total: {}", request.studentId(), request.total());
        OrderResponse created = orderService.createOrder(request);
        log.info("Order created successfully with ID: {}", created.id());
        return ResponseEntity
                .created(URI.create("/orders/" + created.id()))
                .body(created);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@RequestParam("studentId") Long studentId) {
        log.info("Fetching orders for student ID: {}", studentId);
        List<OrderResponse> orders = orderService.getOrdersForStudent(studentId);
        log.info("Retrieved {} orders for student ID: {}", orders.size(), studentId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequest request) {
        log.info("Updating order with ID: {}", id);
        OrderResponse updated = orderService.updateOrder(id, request);
        log.info("Order updated successfully with ID: {}", id);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        log.info("Updating status for order ID: {} to {}", id, request.status());
        OrderResponse updated = orderService.updateOrderStatus(id, request);
        log.info("Order status updated successfully for ID: {}", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("Deleting order with ID: {}", id);
        orderService.deleteOrder(id);
        log.info("Order deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
