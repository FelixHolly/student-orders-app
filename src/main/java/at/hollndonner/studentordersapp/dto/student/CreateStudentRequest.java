package at.hollndonner.studentordersapp.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateStudentRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 20) String grade,
        @NotBlank @Size(max = 150) String school
) {
}

