package at.hollndonner.studentordersapp.controller;

import at.hollndonner.studentordersapp.dto.order.CreateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.OrderFilterRequest;
import at.hollndonner.studentordersapp.dto.order.OrderResponse;
import at.hollndonner.studentordersapp.dto.order.UpdateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.UpdateOrderStatusRequest;
import at.hollndonner.studentordersapp.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
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
                .created(URI.create("/api/v1/orders/" + created.id()))
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.info("Fetching order with ID: {}", id);
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minTotal,
            @RequestParam(required = false) BigDecimal maxTotal,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching orders with filters - studentId: {}, status: {}, minTotal: {}, maxTotal: {}",
                studentId, status, minTotal, maxTotal);
        OrderFilterRequest filter = new OrderFilterRequest(studentId, status, minTotal, maxTotal);
        Page<OrderResponse> orders = orderService.getOrders(filter, pageable);
        log.info("Retrieved {} orders (page {} of {})",
                orders.getNumberOfElements(),
                orders.getNumber() + 1,
                orders.getTotalPages());
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
