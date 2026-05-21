package ejournal.ejournal.repo;

import ejournal.ejournal.model.MethodicalWorkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MethodicalWorkRepository extends JpaRepository<MethodicalWorkEntity, Long> {
    List<MethodicalWorkEntity> findAllByStudentGroupIdOrderByDateAsc(Long studentGroupId);
}
