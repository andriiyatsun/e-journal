package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "org_works")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrgWorkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date; // Дата

    @Column(length = 500)
    private String content; // Зміст заходу

    private String location; // Місце проведення

    private Integer participantsCount; // Кількість учасників

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_group_id")
    private StudentGroupEntity studentGroup;
}