package at.hollndonner.studentordersapp.dto.order;

import java.math.BigDecimal;

public record OrderFilterRequest(
        Long studentId,
        String status,
        BigDecimal minTotal,
        BigDecimal maxTotal
) {}
