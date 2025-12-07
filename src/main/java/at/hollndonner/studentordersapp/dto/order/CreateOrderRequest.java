package at.hollndonner.studentordersapp.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotNull(message = "Student ID is required")
        @Positive(message = "Student ID must be positive")
        Long studentId,

        @NotNull(message = "Total is required")
        @DecimalMin(value = "0.01", message = "Total must be at least 0.01")
        BigDecimal total,

        @NotBlank(message = "Status is required")
        String status
) {}

