package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subjects",
        uniqueConstraints = {
                // Унікальність назви ПРЕДМЕТУ в межах одного ВІДДІЛУ
                @UniqueConstraint(name = "uk_subject_name_department", columnNames = {"name", "department_id"})
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_subject_department"))
    private DepartmentEntity department;

    // Зворотний зв'язок: всі журнали (гуртки), створені для цього предмету
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<StudentGroupEntity> studentGroups = new HashSet<>();

    // ✅ НОВЕ: Зворотний зв'язок: всі записи КТП, пов'язані з цим предметом
    // (Хоча краще зв'язувати через StudentGroup, цей зв'язок може бути корисний для звітності)
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<LessonPlanEntity> lessonPlans = new HashSet<>();
}