package ejournal.ejournal.repo;

import ejournal.ejournal.model.AchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AchievementRepository extends JpaRepository<AchievementEntity, Long> {
    List<AchievementEntity> findAllByStudentGroupId(Long studentGroupId);
}