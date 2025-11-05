package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Сутність "Календарно-тематичний план" (КТП) або "Заняття".
 * Зберігає план, скориговану дату, тему, кількість годин та підтвердження проведення.
 */
@Entity
@Table(name = "lesson_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Зв'язки ---

    // Зв'язок з журналом (гурток)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_group_id", nullable = false, foreignKey = @ForeignKey(name = "fk_lesson_group"))
    private StudentGroupEntity studentGroup;

    // Викладач, який підписав факт проведення (Зміст Роботи)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", foreignKey = @ForeignKey(name = "fk_lesson_teacher_signer"))
    private UserEntity signedByTeacher;

    // Акомпаніатор/додатковий педагог (Функція "Другий підпис")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accompanist_id", foreignKey = @ForeignKey(name = "fk_lesson_accompanist_signer"))
    private UserEntity signedByAccompanist;

    // --- Дані КТП ---

    @Column(name = "lesson_number", nullable = false)
    private Integer lessonNumber; // № з/п

    @Column(name = "planned_date")
    private LocalDate plannedDate; // Дата планова (генерується системою)

    @Column(name = "corrected_date")
    private LocalDate correctedDate; // Дата скоригована (вноситься вручну)

    @Column(name = "lesson_hours", nullable = false)
    private Integer hours; // Кількість годин

    @Lob // Велике текстове поле для детальної теми
    @Column(nullable = false)
    private String topic; // Тема заняття

    @Column(length = 500)
    private String note; // Примітка (Відпустка, Відрядження, Лист непрацездатності тощо)

    // --- Статус ---

    @Column(name = "is_conducted", nullable = false)
    @Builder.Default
    private Boolean isConducted = false; // Факт проведення заняття (підтверджено підписом)

    /**
     * Повертає фактичну дату, яку потрібно показувати у журналі.
     * Якщо є скоригована дата, повертає її, інакше - планову.
     */
    public LocalDate getActualDate() {
        return correctedDate != null ? correctedDate : plannedDate;
    }
}
