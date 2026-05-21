package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "achievements")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AchievementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName; // Прізвище, ім'я

    @Column(length = 500)
    private String participation; // Участь в оглядах, конкурсах...

    @Column(length = 500)
    private String practicalResult; // Результат практичної діяльності...

    @Column(length = 255)
    private String evaluation; // Оцінка творчих досягнень...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_group_id")
    private StudentGroupEntity studentGroup;
}