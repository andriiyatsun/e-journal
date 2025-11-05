package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Ця сутність представляє "Журнал" (або "Гурток", "Навчальну Групу").
 */
@Entity
@Table(name = "student_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // ✅ Це викликає помилку через невірний порядок у конструкторі в сервісі
@Builder
public class StudentGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- ОСНОВНЕ ПОЛЕ (ПОВИННО БУТИ ПЕРШИМ ДЛЯ ЧИТАННЯ) ---
    @Column(nullable = false)
    private String name;

    // --- НОВІ ПОЛЯ з "Основних відомостей" ---
    @Column(name = "program_name", length = 500)
    private String programName;

    @Column(name = "program_approval_date")
    private LocalDate programApprovalDate;

    @Column(name = "study_level", length = 50)
    private String studyLevel;

    @Column(name = "study_year", length = 50)
    private String studyYear;

    @Column(name = "hours_per_week")
    private Integer hoursPerWeek;

    @Column(name = "schedule_json", length = 500)
    private String scheduleJson;

    @Column(name = "group_number", length = 10)
    private String groupNumber;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // --- Зв'язки ---

    // Зв'язок з Навчальним Роком
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false, foreignKey = @ForeignKey(name = "fk_group_academicyear"))
    private AcademicYearEntity academicYear;

    // Зв'язок з Предметом
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false, foreignKey = @ForeignKey(name = "fk_group_subject"))
    private SubjectEntity subject;

    // Зв'язок з Викладачами
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

    // Зв'язок з Вихованцями
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

    // Зв'язок з КТП
    @OneToMany(mappedBy = "studentGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<LessonPlanEntity> lessonPlans = new HashSet<>();

    // ✅ ПОВНИЙ КОНСТРУКТОР ДЛЯ НАДІЙНОСТІ (ВКЛЮЧАЄ ВСІ ПОЛЯ, КРІМ ID ТА ЗВ'ЯЗКІВ @Builder.Default)
    // ID ми не включаємо, бо він генерується. Зв'язки (Set<...>) ми не включаємо, бо вони ініціалізуються @Builder.Default.
    public StudentGroupEntity(String name, String programName, LocalDate programApprovalDate, String studyLevel, String studyYear, Integer hoursPerWeek, String scheduleJson, String groupNumber, LocalDate startDate, LocalDate endDate, AcademicYearEntity academicYear, SubjectEntity subject) {
        this.name = name;
        this.programName = programName;
        this.programApprovalDate = programApprovalDate;
        this.studyLevel = studyLevel;
        this.studyYear = studyYear;
        this.hoursPerWeek = hoursPerWeek;
        this.scheduleJson = scheduleJson;
        this.groupNumber = groupNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.academicYear = academicYear;
        this.subject = subject;
        // Зв'язки teachers, students, lessonPlans ініціалізуються через @Builder.Default
    }
}