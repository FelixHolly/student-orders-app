package at.hollndonner.studentordersapp.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateOrderRequest(
        @NotNull(message = "Total is required")
        @DecimalMin(value = "0.01", message = "Total must be at least 0.01")
        BigDecimal total,

        @NotBlank(message = "Status is required")
        String status,

        @NotNull(message = "Created date is required")
        Instant createdAt
) {}
