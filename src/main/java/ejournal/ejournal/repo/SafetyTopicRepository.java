package ejournal.ejournal.repo;

import ejournal.ejournal.model.SafetyTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SafetyTopicRepository extends JpaRepository<SafetyTopicEntity, Long> {
    List<SafetyTopicEntity> findAllByStudentGroupIdOrderByDateAsc(Long groupId);
}
