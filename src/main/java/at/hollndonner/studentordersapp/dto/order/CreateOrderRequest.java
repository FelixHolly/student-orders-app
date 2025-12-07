package at.hollndonner.studentordersapp.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotNull Long studentId,
        @NotNull @DecimalMin("0.0") BigDecimal total,
        @NotBlank String status
) {}

