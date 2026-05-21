package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "methodical_works")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MethodicalWorkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date; // Дата

    @Column(length = 500)
    private String content; // Зміст роботи

    private String targetAudience; // Для кого проведено захід

    private String location; // Місце проведення

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_group_id")
    private StudentGroupEntity studentGroup;
}