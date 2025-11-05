package ejournal.ejournal.repo;

import ejournal.ejournal.model.LessonPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonPlanRepository extends JpaRepository<LessonPlanEntity, Long> {

    /**
     * Знаходить усі записи КТП для конкретного журналу, відсортовані за номером заняття.
     */
    List<LessonPlanEntity> findAllByStudentGroupIdOrderByLessonNumberAsc(Long studentGroupId);
}
