package at.hollndonner.studentordersapp.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateStudentRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Grade is required")
        @Size(max = 20, message = "Grade must not exceed 20 characters")
        String grade,

        @NotBlank(message = "School is required")
        @Size(max = 150, message = "School must not exceed 150 characters")
        String school
) {
}

