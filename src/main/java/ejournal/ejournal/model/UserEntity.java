package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set; // Додано

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 80)
    private String surname;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    private boolean enabled = true;

    // Зв'язок на одну роль
    @ManyToOne(fetch = FetchType.EAGER) // EAGER для Security
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_user_role"))
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_user_department"))
    private DepartmentEntity department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", foreignKey = @ForeignKey(name = "fk_user_subject"))
    private SubjectEntity subject; // (Це поле може бути "основний" предмет викладача)

    // ✅ НОВЕ: Зворотний зв'язок, що показує, які журнали веде цей викладач
    @ManyToMany(mappedBy = "teachers")
    @Builder.Default
    private Set<StudentGroupEntity> teachingGroups = new HashSet<>();
}

