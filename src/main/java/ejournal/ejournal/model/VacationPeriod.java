package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Сутність "Період Канікул".
 * Зв'язана з конкретним навчальним роком.
 */
@Entity
@Table(name = "vacation_periods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacationPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // Наприклад "Зимові канікули"

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    // --- Зв'язки ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vacation_academic_year"))
    private AcademicYearEntity academicYear;
}

