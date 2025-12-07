package at.hollndonner.studentordersapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String grade;

    @Column(nullable = false, length = 150)
    private String school;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;
}
