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

    // ✅ НОВЕ: Зворотний зв'язок, що показує всі журнали (гуртки),
    // створені для цього предмету в різні роки.
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<StudentGroupEntity> studentGroups = new HashSet<>();
}
