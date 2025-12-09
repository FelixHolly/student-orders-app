package at.hollndonner.studentordersapp.dto.order;

import at.hollndonner.studentordersapp.model.Order;
import at.hollndonner.studentordersapp.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
        Long id,
        Long studentId,
        BigDecimal total,
        Instant createdAt,
        OrderStatus status
) {
    public static OrderResponse fromEntity(Order o) {
        return new OrderResponse(
                o.getId(),
                o.getStudent().getId(),
                o.getTotal(),
                o.getCreatedAt(),
                o.getStatus()
        );
    }
}
