package ejournal.ejournal.repo;

import ejournal.ejournal.model.StudentGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentGroupRepository extends JpaRepository<StudentGroupEntity, Long> {

    /**
     * Знаходить всі журнали (групи) для конкретного відділу,
     * дивлячись на зв'язок "через" предмет.
     * Це буде корисно для вкладки "Журнали" в адмін-панелі.
     */
    @Query("SELECT sg FROM StudentGroupEntity sg WHERE sg.subject.department.id = :departmentId")
    List<StudentGroupEntity> findAllByDepartmentId(Long departmentId);
}
