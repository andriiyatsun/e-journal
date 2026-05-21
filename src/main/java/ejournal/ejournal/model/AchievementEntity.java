package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AchievementEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    private StudentGroupEntity studentGroup;
}
