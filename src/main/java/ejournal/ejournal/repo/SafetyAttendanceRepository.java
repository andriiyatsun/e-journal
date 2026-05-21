package ejournal.ejournal.repo;

import ejournal.ejournal.model.SafetyTopicEntity;
import ejournal.ejournal.model.IntroSafetyEntity;
import ejournal.ejournal.model.SafetyAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SafetyAttendanceRepository extends JpaRepository<SafetyAttendanceEntity, Long> {
    List<SafetyAttendanceEntity> findAllByStudentGroupId(Long groupId);
    void deleteAllByStudentGroupId(Long groupId);
}
