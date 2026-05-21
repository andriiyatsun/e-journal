package ejournal.ejournal.repo;

import ejournal.ejournal.model.RemarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RemarkRepository extends JpaRepository<RemarkEntity, Long> {
    List<RemarkEntity> findAllByStudentGroupIdOrderByDateDesc(Long studentGroupId);
}
