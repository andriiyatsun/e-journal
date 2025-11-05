package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Сутність "Вихованець" (Учень).
 */
@Entity
@Table(name = "students",
        uniqueConstraints = {
                // Унікальність (ПІБ + Дата Народження)
                @UniqueConstraint(name = "uk_student_full_name_dob", columnNames = {"surname", "name", "patronymic", "date_of_birth"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Основні дані (А2) ---
    @Column(nullable = false, length = 80)
    private String surname;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(length = 80)
    private String patronymic;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    // --- Облік та Статус ---
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Column(name = "expulsion_date")
    private LocalDate expulsionDate;

    @Column(length = 100)
    private String category; // Наприклад, "ВПО", "Сирота"

    // --- Освіта ---
    @Column(length = 255)
    private String schoolName;

    @Column(length = 50)
    private String schoolClass; // "11-й", "5-й"

    @Column(length = 80)
    private String schoolDistrict; // Район закладу освіти

    // --- Контакти ---
    @Column(length = 20)
    private String studentPhone;

    @Column(length = 80)
    private String parentSurname;

    @Column(length = 80)
    private String parentName;

    @Column(length = 80)
    private String parentPatronymic;

    @Column(length = 20)
    private String parentPhone;

    @Column(length = 255)
    private String parentEmail;

    // --- Згода (Compliance) ---
    @Column(name = "consent_data_processing")
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Builder.Default
    private Boolean consentDataProcessing = false;

    @Column(name = "consent_photo_video")
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Builder.Default
    private Boolean consentPhotoVideo = false;

    // --- Зв'язок з гуртками (Журналами) ---
    // Один вихованець може бути зарахований до багатьох гуртків.
    // Напрямок @ManyToMany для зручності: від учня до груп.
    @ManyToMany(mappedBy = "students", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<StudentGroupEntity> studentGroups = new HashSet<>();
}
