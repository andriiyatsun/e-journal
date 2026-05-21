package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class RemarkEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private LocalDate date;
    private String text;
    private String authorName; // Ім'я керівника відділу
    @ManyToOne(fetch = FetchType.LAZY)
    private StudentGroupEntity studentGroup;
}
