package at.hollndonner.studentordersapp.repository;

import at.hollndonner.studentordersapp.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStudentId(Long studentId);
}

