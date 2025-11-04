package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Ця сутність представляє "Журнал" (або "Гурток", "Навчальну Групу").
 * Вона є конкретною реалізацією "Предмету" (Subject) у певному "Навчальному Році" (AcademicYear)
 * і має список "Викладачів" (User).
 */
@Entity
@Table(name = "student_groups") // Назва таблиці в БД
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Назва журналу, згенерована сервісом (напр., "3D-друк (2024-2025)")
    @Column(nullable = false)
    private String name;

    // Зв'язок з Навчальним Роком: Багато журналів в одному році
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false, foreignKey = @ForeignKey(name = "fk_group_academicyear"))
    private AcademicYearEntity academicYear;

    // Зв'язок з Предметом: Багато журналів (за різні роки) для одного предмету
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false, foreignKey = @ForeignKey(name = "fk_group_subject"))
    private SubjectEntity subject;

    // Зв'язок з Викладачами: Багато викладачів можуть вести один журнал,
    // і один викладач може вести багато журналів.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_group_teachers",
            joinColumns = @JoinColumn(name = "student_group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            foreignKey = @ForeignKey(name = "fk_group_teacher"),
            inverseForeignKey = @ForeignKey(name = "fk_teacher_group")
    )
    @Builder.Default
    private Set<UserEntity> teachers = new HashSet<>();

    // TODO: В майбутньому тут буде зв'язок зі списком студентів
    // @ManyToMany
    // private Set<StudentEntity> students = new HashSet<>();
}

