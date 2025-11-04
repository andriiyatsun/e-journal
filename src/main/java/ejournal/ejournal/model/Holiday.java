package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Сутність "Святковий/Вихідний день".
 * Зв'язана з конкретним навчальним роком (оскільки свята можуть переноситись).
 */
@Entity
@Table(name = "holidays")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // Наприклад "День Незалежності"

    @Column(nullable = false)
    private LocalDate date; // Конкретна дата свята

    // --- Зв'язки ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false, foreignKey = @ForeignKey(name = "fk_holiday_academic_year"))
    private AcademicYearEntity academicYear;
}
