package ejournal.ejournal.repo;

import ejournal.ejournal.model.DepartmentEntity;
import ejournal.ejournal.model.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {
    List<SubjectEntity> findByDepartmentId(Long departmentId);
    List<SubjectEntity> findByDepartment(DepartmentEntity department);
    Optional<SubjectEntity> findByDepartmentAndName(DepartmentEntity department, String name);
}
