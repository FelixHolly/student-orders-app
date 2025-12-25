package at.hollndonner.studentordersapp.repository;

import at.hollndonner.studentordersapp.model.Order;
import at.hollndonner.studentordersapp.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE " +
           "(:studentId IS NULL OR o.student.id = :studentId) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:minTotal IS NULL OR o.total >= :minTotal) AND " +
           "(:maxTotal IS NULL OR o.total <= :maxTotal)")
    Page<Order> findWithFilters(
            @Param("studentId") Long studentId,
            @Param("status") OrderStatus status,
            @Param("minTotal") BigDecimal minTotal,
            @Param("maxTotal") BigDecimal maxTotal,
            Pageable pageable);
}
