package ejournal.ejournal.repo;

import ejournal.ejournal.model.OrgWorkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrgWorkRepository extends JpaRepository<OrgWorkEntity, Long> {
    List<OrgWorkEntity> findAllByStudentGroupIdOrderByDateAsc(Long studentGroupId);
}