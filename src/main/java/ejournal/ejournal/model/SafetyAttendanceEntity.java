package ejournal.ejournal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "safety_attendances")
@Getter
@Setter
public class SafetyAttendanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_group_id")
    private StudentGroupEntity studentGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "safety_topic_id")
    private SafetyTopicEntity safetyTopic; // Зв'язок з темою

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private StudentEntity student; // Хто був присутній
}
