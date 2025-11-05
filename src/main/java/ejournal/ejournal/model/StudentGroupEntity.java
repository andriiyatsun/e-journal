package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Ця сутність представляє "Журнал" (або "Гурток", "Навчальну Групу").
 * Вона є конкретною реалізацією "Предмету" (Subject) у певному "Навчальному Році" (AcademicYear).
 *
 * ✅ ОНОВЛЕНО: Додано поля для "Основних відомостей" та зв'язок з "Вихованцями".
 */
// ... (імпорти залишаються без змін)

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

    // ... (існуючі поля залишаються без змін)

    // --- НОВІ ПОЛЯ з "Основних відомостей" ---

    @Column(name = "program_name", length = 500)
    private String programName; // Навчальна програма

    @Column(name = "program_approval_date")
    private LocalDate programApprovalDate; // Коли та ким затверджена

    @Column(name = "study_level", length = 50)
    private String studyLevel; // Рівень навчання (початковий, основний, вищий)

    @Column(name = "study_year", length = 50)
    private String studyYear; // Рік навчання (перший, другий, третій)

    @Column(name = "hours_per_week")
    private Integer hoursPerWeek; // Кількість годин на тиждень

    @Column(name = "schedule_json", length = 500)
    // Зберігаємо розклад у вигляді JSON/String (наприклад: "ЧТ: 16:00-17:45, ПТ: 16:00-17:45")
    private String scheduleJson;

    @Column(name = "group_number", length = 10)
    private String groupNumber; // Номер групи (1, 2, 3)

    @Column(name = "start_date")
    private LocalDate startDate; // Дата початку занять

    @Column(name = "end_date")
    private LocalDate endDate; // Дата закінчення занять

    // --- Зв'язки ---

    // Зв'язок з Навчальним Роком
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false, foreignKey = @ForeignKey(name = "fk_group_academicyear"))
    private AcademicYearEntity academicYear;

    // Зв'язок з Предметом
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false, foreignKey = @ForeignKey(name = "fk_group_subject"))
    private SubjectEntity subject;

    // Зв'язок з Викладачами (ManyToMany) - ЗАЛИШАЄТЬСЯ
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

    // Зв'язок з Вихованцями (ManyToMany) - СТВОРЕНО НА ПОПЕРЕДНЬОМУ КРОЦІ
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "group_enrollments",
            joinColumns = @JoinColumn(name = "student_group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"),
            foreignKey = @ForeignKey(name = "fk_enrollment_group"),
            inverseForeignKey = @ForeignKey(name = "fk_enrollment_student")
    )
    @Builder.Default
    private Set<StudentEntity> students = new HashSet<>();

    // ✅ НОВИЙ ЗВ'ЯЗОК: Календарно-тематичний план (КТП)
    @OneToMany(mappedBy = "studentGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<LessonPlanEntity> lessonPlans = new HashSet<>();
}