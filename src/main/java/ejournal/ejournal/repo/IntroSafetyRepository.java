package ejournal.ejournal.repo;
import ejournal.ejournal.model.IntroSafetyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntroSafetyRepository extends JpaRepository<IntroSafetyEntity, Long> {
    List<IntroSafetyEntity> findAllByStudentGroupId(Long groupId);
    void deleteAllByStudentGroupId(Long groupId);
}