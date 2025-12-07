package at.hollndonner.studentordersapp.dto.student;

import at.hollndonner.studentordersapp.model.Student;

public record StudentResponse(
        Long id,
        String name,
        String grade,
        String school
) {
    public static StudentResponse fromEntity(Student s) {
        return new StudentResponse(s.getId(), s.getName(), s.getGrade(), s.getSchool());
    }
}

