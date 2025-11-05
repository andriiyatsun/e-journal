package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "academic_years", uniqueConstraints = {
        @UniqueConstraint(name = "uk_academic_years_name", columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicYearEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // Наприклад "2025-2026"

    @Column(nullable = false)
    private LocalDate startDate; // Наприклад 2025-09-01

    @Column(nullable = false)
    private LocalDate endDate; // Наприклад 2026-05-31

    // --- Зв'язки ---
    // (Зв'язки з VacationPeriod та Holiday залишаються без змін)
    @OneToMany(mappedBy = "academicYear", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<VacationPeriod> vacationPeriods = new HashSet<>();

    @OneToMany(mappedBy = "academicYear", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Holiday> holidays = new HashSet<>();
}