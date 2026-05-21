package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

// 1. Сутність для ТЕМ інструктажів (Таблиця 1)
@Entity
@Table(name = "safety_topics")
@Getter
@Setter
public class SafetyTopicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_group_id")
    private StudentGroupEntity studentGroup;

    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String instructorName;
}

