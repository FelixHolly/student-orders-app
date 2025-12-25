package at.hollndonner.studentordersapp.dto.student;

public record StudentFilterRequest(
        String name,
        String grade,
        String school
) {}
