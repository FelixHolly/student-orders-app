package at.hollndonner.studentordersapp.repository;

import at.hollndonner.studentordersapp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
