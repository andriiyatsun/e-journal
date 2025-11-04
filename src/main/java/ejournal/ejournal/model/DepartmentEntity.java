package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_departments_code", columnNames = "code")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DepartmentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Короткий код: ЮН-ПРЕС, ВТКС, ІМАП, АКЦ, ВВМ, STEM, ВРД ДЖЕРЕЛЬЦЕ, ВСІП, ВХТ, БІО
     */
    @Column(nullable = false, length = 64)
    private String code;

    /** Повна назва. */
    @Column(nullable = false, length = 128)
    private String name;
}
