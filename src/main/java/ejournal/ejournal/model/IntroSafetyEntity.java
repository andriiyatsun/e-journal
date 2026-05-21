package ejournal.ejournal.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

// 2. Сутність для ВСТУПНОГО інструктажу (Таблиця 2)
@Entity
@Table(name = "safety_intro_records")
@Getter
@Setter
public class IntroSafetyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_group_id")
    private StudentGroupEntity studentGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private StudentEntity student; // Сутність учня

    private LocalDate introDate;
    private String instructorName;
}
